package me.nov.cafebabe.translations;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nov.cafebabe.utils.web.URLReader;

public class Translations {
	private static final HashMap<Integer, String> translations = new HashMap<>();
	private static final String language = System.getProperty("user.language");

	public static boolean translate = false;

	public static String get(String i) {
		if (!translate) {
			return i;
		}
		if (translations.containsKey(i.hashCode())) {
			return translations.get(i.hashCode());
		}
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

	private static String translate(String i) throws IOException {
		Matcher m = Pattern.compile("\"(.*?)\"")
				.matcher(URLReader.getURLContent("https://translate.googleapis.com/translate_a/single?client=gtx&sl=eng&tl="
						+ language + "&dt=t&q=" + URLEncoder.encode(i, "UTF-8")));
		return m.find() ? m.group() : i;
	}
}
