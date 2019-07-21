package me.nov.cafebabe.translations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.smalleditor.ChangelogPanel;
import me.nov.cafebabe.utils.io.Scanning;
import me.nov.cafebabe.utils.web.URLReader;

public class Translations {
	public static final HashMap<Integer, String> translations = new HashMap<>();
	public static final String language = System.getProperty("user.language");

	public static boolean translate = false;

	private static File translationsFile;

	public static String get(String i) {
		if (!translate) {
			return i;
		}
		if (translations.containsKey(i.hashCode())) {
			return translations.get(i.hashCode());
		}

		translations.put(i.hashCode(), i);
		return i;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private static String translateGoogle(String i) {
		String translation;
		try {
			translation = translate(i);
			if (translation == null || translation.trim().length() < 3) {
				return i;
			}
			translation = translation.substring(1, translation.length() - 1);
			translations.put(i.hashCode(), translation);
			return translation;
		} catch (Exception e) {
			return i;
		}
	}

	@Deprecated
	private static String translate(String i) throws IOException {
		Matcher m = Pattern.compile("\"(.*?)\"")
				.matcher(URLReader.getURLContent("https://translate.googleapis.com/translate_a/single?client=gtx&sl=eng&tl="
						+ language + "&dt=t&q=" + URLEncoder.encode(i, "UTF-8")));
		return m.find() ? m.group() : i;
	}

	public static void saveTranslations() {
		Properties properties = new Properties();
		for (Integer key : translations.keySet()) {
			properties.setProperty(String.valueOf(key), translations.get(key));
		}
		try {
			properties.store(new FileOutputStream(translationsFile), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadTranslations() {
		String fileName = language + ".translation";
		translationsFile = new File(new File(Cafebabe.folder, "translations"), fileName);
		// TODO warn about old translation?
		Properties properties = new Properties();
		if (translationsFile.exists()) {
			try {
				properties.load(new FileInputStream(translationsFile));

				for (Object key : properties.keySet()) {
					translations.put(Integer.valueOf(String.valueOf(key)), String.valueOf(properties.get(key)));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Files.write(translationsFile.toPath(),
						Scanning
								.readInputStream(
										ChangelogPanel.class.getResourceAsStream("/resources/default_translations/" + fileName))
								.getBytes());
				loadTranslations();
			} catch (Exception e) {
				try {
					translationsFile.getParentFile().mkdirs();
					translationsFile.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	static {
		loadTranslations();
	}

}
