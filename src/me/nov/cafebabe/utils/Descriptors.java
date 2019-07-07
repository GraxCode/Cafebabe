package me.nov.cafebabe.utils;

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
					result += "<font color=\"#7f0055\"><b>boolean</b></font>";
				} else if (chr == 'B') {
					result += "<font color=\"#7f0055\"><b>byte</b></font>";
				} else if (chr == 'C') {
					result += "<font color=\"#7f0055\"><b>char</b></font>";
				} else if (chr == 'S') {
					result += "<font color=\"#7f0055\"><b>short</b></font>";
				} else if (chr == 'I') {
					result += "<font color=\"#7f0055\"><b>int</b></font>";
				} else if (chr == 'J') {
					result += "<font color=\"#7f0055\"><b>long</b></font>";
				} else if (chr == 'F') {
					result += "<font color=\"#7f0055\"><b>float</b></font>";
				} else if (chr == 'D') {
					result += "<font color=\"#7f0055\"><b>double</b></font>";
				} else if (chr == 'V') {
					result += "<font color=\"#7f0055\"><b>void</b></font>";
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
		String text = "<font color=\"#7f0055\"><b>";
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
