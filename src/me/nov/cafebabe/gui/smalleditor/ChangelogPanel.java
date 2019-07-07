package me.nov.cafebabe.gui.smalleditor;

import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("resource")
public class ChangelogPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public ChangelogPanel() {
		this.setContentType("text/html");
		this.setEditable(false);
		this.setText(readInputStream(ChangelogPanel.class.getResourceAsStream("/resources/changelog.txt")));
		this.setFocusable(false);
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}

	private String readInputStream(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}
}
