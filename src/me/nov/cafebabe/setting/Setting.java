package me.nov.cafebabe.setting;

import java.lang.reflect.Field;

import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.interfaces.BooleanAction;

public class Setting {
	public String id;
	public String title;
	public String description;
	public Field field;
	public boolean defaultValue;
	private BooleanAction updateAction;

	public Setting(String id, String title, String description, Field field, boolean defaultValue,
			BooleanAction updateAction) {
		super();
		this.id = id;
		this.title = Translations.get(title);
		this.description = Translations.get(description);
		this.field = field;
		this.defaultValue = defaultValue;
		this.updateAction = updateAction;
	}

	public void setInitial(boolean b) {
		try {
			field.setBoolean(null, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void set(boolean b) {
		try {
			field.setBoolean(null, b);
			Settings.saveProperties();
			new Thread(() -> {
				try {
					// call later to update ui
					Thread.sleep(500);
					if (updateAction != null) {
						updateAction.action(b);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean get() {
		try {
			return field.getBoolean(null);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

}
