package me.nov.cafebabe.setting;

import java.lang.reflect.Field;

import javax.swing.SwingUtilities;

import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.interfaces.Action;

public class Setting {
	public String id;
	public String title;
	public String description;
	public Field field;
	public boolean defaultValue;
	private Action updateAction;

	public Setting(String id, String title, String description, Field field, boolean defaultValue, Action updateAction) {
		super();
		this.id = id;
		this.title = Translations.get(title);
		this.description = Translations.get(description);
		this.field = field;
		this.defaultValue = defaultValue;
		this.updateAction = updateAction;
	}

	public void set(boolean b) {
		// TODO save settings
		try {
			field.setBoolean(null, b);
			SwingUtilities.invokeLater(() -> {
				if (updateAction != null) {
					updateAction.action();
				}
			});
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public boolean get() {
		try {
			return field.getBoolean(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

}
