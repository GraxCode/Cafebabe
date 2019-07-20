package me.nov.cafebabe.gui.preferences;

import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JEditorPane;
import javax.swing.border.EmptyBorder;

import me.nov.cafebabe.Cafebabe;

@SuppressWarnings("resource")
public class AboutPanel extends JEditorPane {
	private static final long serialVersionUID = 1L;

	public AboutPanel() {
		this.setContentType("text/html");
		this.setEditable(false);
		String license = readInputStream(AboutPanel.class.getResourceAsStream("/resources/license.txt")).replace("\n", "<br>");
		this.setText(String.format(readInputStream(AboutPanel.class.getResourceAsStream("/resources/about.txt")), Cafebabe.title, Cafebabe.version) + license);
		this.setFocusable(false);
		this.setOpaque(true);
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setBorder(new EmptyBorder(16, 16, 16, 16));
	}

	private String readInputStream(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}
}
