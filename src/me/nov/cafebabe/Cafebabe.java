package me.nov.cafebabe;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.MenuBarStyle;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.rootpane.WebFrame;

import me.nov.cafebabe.gui.ClassMemberList;
import me.nov.cafebabe.gui.ClassTree;
import me.nov.cafebabe.gui.HelpBar;
import me.nov.cafebabe.gui.InnerSplitPane;
import me.nov.cafebabe.gui.OuterSplitPane;
import me.nov.cafebabe.gui.editor.Editor;
import me.nov.cafebabe.gui.smalleditor.ChangelogPanel;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;

public class Cafebabe extends WebFrame {
	private static final long serialVersionUID = 1L;
	private static final String title = "Cafebabe Editor Lite";
	private static final String version = "0.0.3";
	public static Cafebabe gui;

	private ClassTree tree;
	private ClassMemberList methods;
	public JScrollPane smallEditorPanel;
	private Editor editorFrame;

	public Cafebabe() {
		gui = this;
		setTitle(title + " " + version);
		initBounds();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		setLayout(new BorderLayout());
		this.setJMenuBar(initMenuBar());
		this.smallEditorPanel = new JScrollPane(new ChangelogPanel());
		this.methods = new ClassMemberList();
		this.tree = new ClassTree(methods);
		this.add(new OuterSplitPane(new JScrollPane(tree), new InnerSplitPane(new JScrollPane(methods), smallEditorPanel)),
				BorderLayout.CENTER);
		HelpBar hb = new HelpBar();
		this.add(hb, BorderLayout.SOUTH);

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent me = (MouseEvent) e;
					if (me.getComponent() instanceof JComponent) {
						JComponent jc = (JComponent) me.getComponent();
						String ttt = jc.getToolTipText();
						if (ttt != null && ttt.trim().length() > 0) {
							hb.setText(jc.getToolTipText());
						} else {
							hb.resetText();
						}
					}
				}
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK);
		this.setRound(5);
		this.setShadeWidth(20);
		this.setShowResizeCorner(false);
		this.setIconImage(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/resources/icon.png")));
	}

	private JMenuBar initMenuBar() {
		WebMenuBar bar = new WebMenuBar();
		bar.setUndecorated(false);
		bar.setMenuBarStyle(MenuBarStyle.attached);

		WebMenu file = new WebMenu("File");
		WebMenuItem load = new WebMenuItem("Open");
		load.addActionListener(l -> {
			JFileChooser jfc = new JFileChooser(new File(System.getProperty("user.home") + File.separator + "Desktop"));
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(new FileNameExtensionFilter("Java Package (*.jar)", "jar"));
			int result = jfc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File input = jfc.getSelectedFile();
				tree.onJarLoad(-1, input);
			}
		});
		WebMenuItem save = new WebMenuItem("Save");
		save.addActionListener(l -> {
			if (tree.inputFile == null)
				return;
			JFileChooser jfc = new JFileChooser(tree.inputFile.getParentFile());
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setSelectedFile(tree.inputFile);
			jfc.setDialogTitle("Save");
			jfc.setFileFilter(new FileNameExtensionFilter("Java Package (*.jar)", "jar"));
			int result = jfc.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File output = jfc.getSelectedFile();
				tree.saveJar(output);
			}
		});
		file.add(load);
		file.add(save);
		bar.add(file);
		return bar;
	}

	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.3515625); // 675
		int height = (int) (screenSize.height * 0.833333333); // 900

		int x = (int) (screenSize.width * 0.013); // 25
		setBounds(x, screenSize.height / 2 - height / 2, width, height);
	}

	public void openEditor(Component c, String title, Color color, Icon icon) {
		if (editorFrame == null) {
			editorFrame = new Editor();
		}
		if (!editorFrame.isShowing()) {
			editorFrame.setVisible(true);
		}
		editorFrame.open(c, title, icon, color);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		WebLookAndFeel.install();
		WebLookAndFeel.setDecorateFrames(true);
		new Cafebabe();
		gui.setVisible(true);
	}
}
