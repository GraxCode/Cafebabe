package me.nov.cafebabe.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.alee.laf.rootpane.WebDialog;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.translations.Translations;

public class PreferencesDialog extends WebDialog {
	private static final long serialVersionUID = 1L;

	public PreferencesDialog() {
		super(Cafebabe.gui, true);
		this.setRound(5);
		this.setShadeWidth(20);
		this.setShowResizeCorner(false);
		this.initBounds();
		this.setTitle(Translations.get("Preferences"));
		this.setIconImage(Cafebabe.gui.getIconImage());
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		try {
			this.add(new PreferencesPane());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JPanel buttons = new JPanel(new FlowLayout(4));
		this.add(buttons, BorderLayout.SOUTH);

		JButton close = new JButton(Translations.get("Close"));
		buttons.add(close);
		close.addActionListener(e -> {
			setVisible(false);
		});
		setLocationRelativeTo(getParent());
		this.setVisible(true);
	}

	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.25);
		int height = (int) (screenSize.height * 0.5);

		setBounds(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2, width, height);
	}
}
