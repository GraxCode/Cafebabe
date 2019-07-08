package me.nov.cafebabe.utils.asm;

import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.Html;

public class Descriptors {

	public static String getDisplayType(String rawType) {
		String result = "";
		String tmpArg = "";
		String argSuffix = "";
		boolean isFullyQualified = false;
		for (char chr : rawType.toCharArray()) {
			if (isFullyQualified) {
				if (chr == ';') {
					String[] spl = tmpArg.split("/");
					result += spl[spl.length - 1] + argSuffix + ", ";
					argSuffix = "";
					isFullyQualified = false;
					tmpArg = "";
				} else {
					tmpArg += chr;
				}
			} else if (chr == '[') {
				argSuffix += "[]";
			} else if (chr == 'L') {
				isFullyQualified = true;
			} else {
				if (chr == 'Z') {
					result += Html.color(Colors.eclipse_keyword, "boolean");
				} else if (chr == 'B') {
					result += Html.color(Colors.eclipse_keyword, "byte");
				} else if (chr == 'C') {
					result += Html.color(Colors.eclipse_keyword, "char");
				} else if (chr == 'S') {
					result += Html.color(Colors.eclipse_keyword, "short");
				} else if (chr == 'I') {
					result += Html.color(Colors.eclipse_keyword, "int");
				} else if (chr == 'J') {
					result += Html.color(Colors.eclipse_keyword, "long");
				} else if (chr == 'F') {
					result += Html.color(Colors.eclipse_keyword, "float");
				} else if (chr == 'D') {
					result += Html.color(Colors.eclipse_keyword, "double");
				} else if (chr == 'V') {
					result += Html.color(Colors.eclipse_keyword, "void");
				} else {
					isFullyQualified = true;
					continue;
				}

				result += argSuffix;
				argSuffix = "";
				result += ", ";
			}
		}

		if (tmpArg.length() != 0) {
			String[] spl = tmpArg.split("/");
			result += spl[spl.length - 1] + argSuffix + ", ";
		}

		if (result.length() >= 2) {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}

	public static String getDisplayTypeEditable(String rawType) {
		String result = "";
		String tmpArg = "";
		String argSuffix = "";
		boolean isFullyQualified = false;
		for (char chr : rawType.toCharArray()) {
			if (isFullyQualified) {
				if (chr == ';') {
					result += tmpArg + argSuffix + ", ";
					argSuffix = "";
					isFullyQualified = false;
					tmpArg = "";
				} else {
					tmpArg += chr;
				}
			} else if (chr == '[') {
				argSuffix += "[]";
			} else if (chr == 'L') {
				isFullyQualified = true;
			} else {
				if (chr == 'Z') {
					result += "boolean";
				} else if (chr == 'B') {
					result += "byte";
				} else if (chr == 'C') {
					result += "char";
				} else if (chr == 'S') {
					result += "short";
				} else if (chr == 'I') {
					result += "int";
				} else if (chr == 'J') {
					result += "long";
				} else if (chr == 'F') {
					result += "float";
				} else if (chr == 'D') {
					result += "double";
				} else if (chr == 'V') {
					result += "void";
				} else {
					isFullyQualified = true;
					continue;
				}

				result += argSuffix;
				argSuffix = "";
				result += ", ";
			}
		}

		if (tmpArg.length() != 0) {
			result += tmpArg + argSuffix + ", ";
		}

		if (result.length() >= 2) {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}

	public static String displayTypeToDesc(String dtype) {

		String prefix = "";
		while (dtype.endsWith("[]")) {
			prefix += "[";
			dtype = dtype.substring(0, dtype.length() - 2);
		}

		// primitives
		switch (dtype) {
		case "boolean":
			return prefix + "Z";
		case "byte":
			return prefix + "B";
		case "char":
			return prefix + "C";
		case "short":
			return prefix + "S";
		case "int":
			return prefix + "I";
		case "long":
			return prefix + "J";
		case "float":
			return prefix + "F";
		case "double":
			return prefix + "D";
		case "void":
			return prefix + "V";
		}
		return prefix + "L" + dtype + ";";

	}

	public static String getDisplayAccess(int access) {
		String text = "<font color=\"" + Colors.eclipse_keyword + "\"><b>";
		if ((access & 1) != 0) {
			text = text + "public ";
		}

		if ((access & 2) != 0) {
			text = text + "private ";
		}

		if ((access & 4) != 0) {
			text = text + "protected ";
		}

		if ((access & 8) != 0) {
			text = text + "static ";
		}

		if ((access & 16) != 0) {
			text = text + "final ";
		}

		if ((access & 1024) != 0) {
			text = text + "abstract ";
		}
		text += "</b></font>";
		return text;
	}

	public static String lastSlash(String text) {
		String[] split = text.split("/");
		return split[split.length - 1];
	}
}
