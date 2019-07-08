package me.nov.cafebabe.utils.formatting;

public class Html {
	public static String color(String color, String text) {
		return "<font color=\"" + color + "\">" + text + "</font>";
	}

	public static String bold(String text) {
		return "<b>" + text + "</b>";
	}

	public static String italics(String text) {
		return "<i>" + text + "</i>";
	}
}
