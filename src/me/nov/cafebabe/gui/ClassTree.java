package me.nov.cafebabe.gui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.objectweb.asm.tree.ClassNode;

import com.alee.extended.window.WebProgressDialog;
import com.alee.laf.tree.TreeSelectionStyle;
import com.alee.laf.tree.WebTree;
import com.alee.utils.ThreadUtils;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.node.SortedTreeClassNode;
import me.nov.cafebabe.gui.smalleditor.ClassEditorPanel;
import me.nov.cafebabe.gui.ui.ClassTreeCellRenderer;
import me.nov.cafebabe.loading.FrameHack;
import me.nov.cafebabe.loading.Loader;
import me.nov.cafebabe.loading.Saver;
import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.drop.IDropUser;
import me.nov.cafebabe.utils.drop.JarDropHandler;

public class ClassTree extends WebTree<SortedTreeClassNode> implements IDropUser {
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel model;
	private HashMap<JarEntry, ClassNode> classes;
	private Map<String, String> knownCommons; // used for frame regeneration
	private SortedTreeClassNode root;
	public File inputFile;

	public ClassTree(ClassMemberList ml) {
		ClassEditorPanel editor = new ClassEditorPanel(this);
		this.setRootVisible(false);
		this.setShowsRootHandles(true);
		this.setFocusable(false);
		this.setCellRenderer(new ClassTreeCellRenderer());
		this.setSelectionStyle(TreeSelectionStyle.group);

		this.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SortedTreeClassNode node = (SortedTreeClassNode) getLastSelectedPathComponent();
				if (node != null && node.getClazz() != null) {
					ml.setMethods(node.getClazz());
					Cafebabe.gui.smallEditorPanel.setViewportView(editor);
					editor.editClass(node.getClazz());
				}
			}
		});
		this.model = new DefaultTreeModel(root = new SortedTreeClassNode(""));
		this.setModel(model);
		this.setTransferHandler(new JarDropHandler(this, 0));
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	@Override
	public void preLoadJars(int id) {

	}
	
	public static boolean useFrameRegeneration = true;
	
	@Override
	public void onJarLoad(int id, File input) {
		final WebProgressDialog progress = new WebProgressDialog(Cafebabe.gui, Translations.get("Loading"));
		progress.setText(Translations.get("Loading .jar file..."));

		// loading algorithm taken from loader class
		final Thread loader = new Thread(new Runnable() {


			@Override
			public void run() {
				try {
					inputFile = input;
					JarFile jf = new JarFile(input);
					classes = new HashMap<>();
					knownCommons = new HashMap<>();
					int files = 0;
					Enumeration<JarEntry> entries = jf.entries();
					while (entries.hasMoreElements()) {
						files++; // count files
						entries.nextElement();
					}

					int fileNum = 0;
					entries = jf.entries();
					while (entries.hasMoreElements()) {
						try {
							JarEntry entry = entries.nextElement();
							if (entry.getSize() < 3) {
								continue;
							}
							progress.setProgress((int) (fileNum / (double) files * 50d));
							progress.setText(Translations.get("Loading file") + " " + entry.getName());
							InputStream stream = jf.getInputStream(entry);

							ByteArrayOutputStream bos = new ByteArrayOutputStream();

							int read = 0;
							byte[] cafebabe = new byte[4];
							stream.read(cafebabe);
							bos.write(cafebabe, 0, 4);
							if (Arrays.equals(bos.toByteArray(), Loader.javaMagic)) {
								byte[] buff = new byte[1024];

								while ((read = stream.read(buff)) != -1) {
									bos.write(buff, 0, read);
								}
								byte[] data = bos.toByteArray();
								ClassNode cn = Loader.convertToASM(data);
								if (useFrameRegeneration) {
									FrameHack.findCommonParents(knownCommons, cn); // preload common super classes for frame regeneration
								}
								classes.put(entry, cn);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						fileNum++;
					}

					int size = classes.keySet().size();
					fileNum = 0;
					for (JarEntry e : classes.keySet()) {
						progress.setProgress(50 + (int) (fileNum / (double) size * 50d));
						progress.setText(Translations.get("Creating tree element") + " " + e.getName());
						String[] path = e.getName().split("/");
						SortedTreeClassNode current = root;
						for (int i = 0; i < path.length; i++) {
							SortedTreeClassNode next = null;
							for (int j = 0; j < current.getChildCount(); j++) {

								SortedTreeClassNode child = (SortedTreeClassNode) current.getChildAt(j);
								if (child.getText().equals(path[i])) {
									next = child;
									break;
								}
							}
							if (next == null) {
								if (i + 1 >= path.length) { // last one
									next = new SortedTreeClassNode(classes.get(e));
								} else {
									next = new SortedTreeClassNode(path[i]);
								}
								current.add(next);
							}
							current = next;
						}
						fileNum++;
					}
					jf.close();
					root.sort();
					model.reload();
					ClassTree.this.repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
				progress.setProgress(100);
				progress.setText(Translations.get("Finished loading!"));
				ThreadUtils.sleepSafely(500);
				progress.setVisible(false);
			}
		});
		loader.setDaemon(true);
		loader.start();

		// Displaying dialog
		progress.setModal(true);
		progress.setVisible(true);

	}

	@Override
	public String getToolTipText() {
		if (inputFile != null)
			return inputFile.getAbsolutePath();
		return Translations.get("Drop a .jar file here");
	}

	public void saveJar(File output) {
		final WebProgressDialog progress = new WebProgressDialog(Cafebabe.gui, Translations.get("Saving"));
		progress.setText(Translations.get(Translations.get("Saving as") + " " + output.getName() + "..."));

		// loading algorithm taken from loader class
		final Thread loader = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileOutputStream fos = new FileOutputStream(output);
					JarOutputStream out = new JarOutputStream(fos);
					JarFile inputJarFile = new JarFile(inputFile);

					int files = 0;
					Enumeration<JarEntry> inputEntries = inputJarFile.entries();
					while (inputEntries.hasMoreElements()) {
						files++; // count files
						inputEntries.nextElement();
					}

					int fileNum = 0;
					inputEntries = inputJarFile.entries();
					while (inputEntries.hasMoreElements()) {
						try {
							JarEntry entry = inputEntries.nextElement();
							progress.setProgress((int) (fileNum / (double) files * 50d));
							progress.setText(Translations.get("Rewriting file") + " " + entry.getName());
							InputStream stream = inputJarFile.getInputStream(entry);

							ByteArrayOutputStream bos = new ByteArrayOutputStream();

							int read = 0;
							if (entry.getSize() > 3) {
								byte[] cafebabe = new byte[4];
								stream.read(cafebabe);
								bos.write(cafebabe, 0, 4);
								if (Arrays.equals(bos.toByteArray(), Saver.javaMagic)) {
									continue;
								}
							}
							byte[] buff = new byte[1024];

							while ((read = stream.read(buff)) != -1) {
								bos.write(buff, 0, read);
							}
							byte[] data = bos.toByteArray();
							Saver.packFile(out, entry, entry.getName(), data);
						} catch (IOException e) {
							e.printStackTrace();
						}
						fileNum++;
					}
					Map<String, ClassNode> clazzes = new HashMap<>();
					for (JarEntry oldEntry : classes.keySet()) {
						ClassNode cn = classes.get(oldEntry);
						clazzes.put(cn.name, cn);
					}
					fileNum = 0;
					int size = classes.keySet().size();
					for (JarEntry oldEntry : classes.keySet()) {
						ClassNode clazz = classes.get(oldEntry);
						progress.setProgress(50 + (int) (fileNum / (double) size * 50d));
						progress.setText(Translations.get("Exporting class") + " " + clazz.name);
						Saver.packFile(out, oldEntry, clazz.name + ".class",
								Saver.exportNode(clazz, clazzes, new HashMap<>(), knownCommons));
						fileNum++;
					}
					out.finish();
					out.flush();
					out.close();
					inputJarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				progress.setProgress(100);
				progress.setText(Translations.get("Finished saving!"));
				ThreadUtils.sleepSafely(500);
				progress.setVisible(false);
			}
		});
		loader.setDaemon(true);
		loader.start();

		// Displaying dialog
		progress.setModal(true);
		progress.setVisible(true);
	}
}
