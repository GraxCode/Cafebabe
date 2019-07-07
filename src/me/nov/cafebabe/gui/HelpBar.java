package me.nov.cafebabe.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.alee.extended.statusbar.WebMemoryBar;

import me.nov.cafebabe.Cafebabe;

public class HelpBar extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel label;

	public HelpBar() {
		Border b = BorderFactory.createLoweredSoftBevelBorder();
		this.setBorder(b);
		this.setLayout(new BorderLayout());
		label = new JLabel();
		this.resetText();
		this.add(label, BorderLayout.WEST);
		WebMemoryBar mb = new WebMemoryBar();
		mb.setShowMaximumMemory(false);
		mb.setFocusable(false);
		this.add(mb, BorderLayout.EAST);
	}

	public void setText(String text) {
		label.setText(" " + text);
	}

	public void resetText() {
		label.setText(" " + Cafebabe.gui.getTitle());
	}
}
