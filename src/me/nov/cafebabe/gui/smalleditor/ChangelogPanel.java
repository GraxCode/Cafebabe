package me.nov.cafebabe.gui.smalleditor;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

import me.nov.cafebabe.utils.io.Scanning;

public class ChangelogPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public ChangelogPanel() {
		this.setContentType("text/html");
		this.setEditable(false);
		this.setText(Scanning.readInputStream(ChangelogPanel.class.getResourceAsStream("/resources/changelog.txt")));
		this.setFocusable(false);
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}
}
