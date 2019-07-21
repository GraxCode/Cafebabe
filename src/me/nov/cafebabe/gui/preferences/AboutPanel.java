package me.nov.cafebabe.gui.preferences;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.utils.io.Scanning;

public class AboutPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public AboutPanel() {
		this.setContentType("text/html");
		this.setEditable(false);
		String license = Scanning.readInputStream(AboutPanel.class.getResourceAsStream("/resources/license.txt")).replace("\n", "<br>");
		this.setText(String.format(Scanning.readInputStream(AboutPanel.class.getResourceAsStream("/resources/about.txt")), Cafebabe.title, Cafebabe.version) + license);
		this.setFocusable(false);
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}
}
