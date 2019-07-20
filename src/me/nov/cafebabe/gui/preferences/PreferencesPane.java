package me.nov.cafebabe.gui.preferences;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.objectweb.asm.Opcodes;

import com.alee.laf.tabbedpane.WebTabbedPane;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.preferences.list.SettingList;
import me.nov.cafebabe.setting.Setting;
import me.nov.cafebabe.translations.Translations;

public class PreferencesPane extends WebTabbedPane implements Opcodes {
	private static final long serialVersionUID = 1L;

	public PreferencesPane() throws Exception {
		this.setTabPlacement(WebTabbedPane.LEFT);
		this.addTab(Translations.get("General Settings"),
				new JScrollPane(
						new SettingList(new Setting("auto_translate", "Translate", "Automatic translation via Google Translate API",
								Translations.class.getDeclaredField("translate"), true, () -> {
									if (JOptionPane.showConfirmDialog(Cafebabe.gui,
											Translations.get("Do you want to restart now? Everything unsaved will be lost!"),
											Translations.get("Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										Cafebabe.gui.dispose();
										try {
											Cafebabe.main(new String[0]);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}))));
		this.addTab(Translations.get("About"), new JScrollPane(new AboutPanel()));
	}
}