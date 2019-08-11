package me.nov.cafebabe.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import com.alee.extended.tab.DocumentData;
import com.alee.extended.tab.DocumentListener;
import com.alee.extended.tab.PaneData;
import com.alee.extended.tab.WebDocumentPane;
import com.alee.utils.TextUtils;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.translations.Translations;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Editor extends JFrame {
	private static final long serialVersionUID = 1L;
	private WebDocumentPane pane;

	public Editor() {
		this.setTitle("Editor");
		initBounds();
		this.setIconImage(Cafebabe.gui.getIconImage());
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);
		this.pane = new WebDocumentPane();
		pane.setCloseable(true);
		pane.setTabMenuEnabled(true);
		pane.setWebColoredBackground(true);
		pane.addDocumentListener(new DocumentListener<DocumentData>() {

			@Override
			public void closed(DocumentData arg0, PaneData<DocumentData> arg1, int arg2) {
				if(pane.getDocumentsCount() == 0) {
					setVisible(false);
				}
			}

			@Override
			public boolean closing(DocumentData arg0, PaneData<DocumentData> arg1, int arg2) {
				return true;
			}

			@Override
			public void opened(DocumentData arg0, PaneData<DocumentData> arg1, int arg2) {
				setTitle(arg0.getTitle());
			}

			@Override
			public void selected(DocumentData arg0, PaneData<DocumentData> arg1, int arg2) {
				setTitle(arg0.getTitle());
			}
		});
		pane.setUndecorated(false);
		this.add(pane, BorderLayout.CENTER);
		this.setJMenuBar(createMenu());

	}

	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int mainFrameWidth = (int) (screenSize.width * 0.3515625); // 675

		int mainFrameX = (int) (screenSize.width * 0.013);

		int width = (int) (screenSize.width * 0.622395833); // 1195
		int height = (int) (screenSize.height * 0.833333333); // 900

		setBounds(mainFrameX + mainFrameWidth, screenSize.height / 2 - height / 2, width, height);
	}

	private JMenuBar createMenu() {
		JMenuBar mb = new JMenuBar();
		JMenu actions = new JMenu(Translations.get("Actions"));
		JMenuItem close = new JMenuItem(Translations.get("Close all"));
		close.addActionListener(l -> {
			pane.closeAll();
		});

		actions.add(close);
		mb.add(actions);
		return mb;
	}

	public void open(Component c, String title, Icon icon, Color color) {
		if(c instanceof JScrollPane) {
			JScrollPane sp = (JScrollPane) c;
			sp.getVerticalScrollBar().setUnitIncrement(16);
		}
		pane.openDocument(new DocumentData(TextUtils.generateId(), icon, title, color, c));
	}

}
