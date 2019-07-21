package me.nov.cafebabe;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
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
import me.nov.cafebabe.gui.preferences.PreferencesDialog;
import me.nov.cafebabe.gui.smalleditor.ChangelogPanel;
import me.nov.cafebabe.gui.translations.TranslationEditor;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;
import me.nov.cafebabe.setting.Settings;
import me.nov.cafebabe.translations.Translations;

public class Cafebabe extends WebFrame {
	private static final long serialVersionUID = 1L;
	public static final String title = "Cafebabe Editor Lite";
	public static final String version = "0.0.6";
	public static Cafebabe gui;
	public static File folder;

	private ClassTree tree;
	private ClassMemberList methods;
	public JScrollPane smallEditorPanel;
	private Editor editorFrame;

	public Cafebabe() {
		gui = this;
		setTitle(title + " " + version);
		initBounds();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				if (JOptionPane.showConfirmDialog(Cafebabe.this, Translations.get("Do you really want to exit?"),
						Translations.get("Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					Runtime.getRuntime().exit(0);
				}
			}
		});
		this.setAlwaysOnTop(true);
		setLayout(new BorderLayout());
		this.setJMenuBar(initMenuBar());
		this.smallEditorPanel = new JScrollPane(new ChangelogPanel());
		this.smallEditorPanel.getVerticalScrollBar().setUnitIncrement(16);
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

		WebMenu file = new WebMenu(Translations.get("File"));
		WebMenuItem load = new WebMenuItem(Translations.get("Open jar file"));
		load.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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
		WebMenuItem save = new WebMenuItem(Translations.get("Save jar file"));
		save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		save.addActionListener(l -> {
			if (tree.inputFile == null)
				return;
			JFileChooser jfc = new JFileChooser(tree.inputFile.getParentFile());
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setSelectedFile(tree.inputFile);
			jfc.setDialogTitle(Translations.get("Save jar file"));
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
		WebMenu preferences = new WebMenu(Translations.get("Preferences"));
		WebMenuItem editPrefs = new WebMenuItem(Translations.get("Edit preferences..."));
		editPrefs.addActionListener(l -> {
			new PreferencesDialog();
		});
		preferences.add(editPrefs);
		WebMenuItem tranlations = new WebMenuItem(Translations.get("Translation editor..."));
		tranlations.addActionListener(l -> {
			new TranslationEditor();
		});
		preferences.add(tranlations);
		bar.add(preferences);
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
		try {
			folder = new File(System.getProperty("user.home"), ".cafebabe");
			if (!folder.exists()) {
				folder.mkdir();
			}
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			WebLookAndFeel.install();
			WebLookAndFeel.setDecorateFrames(true);
			WebLookAndFeel.setDecorateDialogs(true);
			System.setProperty("file.encoding", "UTF-8");
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
			Settings.loadSettings();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		new Cafebabe();
		gui.setVisible(true);
	}
}
