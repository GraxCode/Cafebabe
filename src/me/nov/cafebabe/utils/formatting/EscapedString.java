package me.nov.cafebabe.utils.formatting;

public class EscapedString {
	private String text;
	private String escapedText;

	public EscapedString(String text) {
		super();
		this.text = text;
		if (text.length() > 127) {
			text = text.substring(0, 124) + "...";
		}
		this.escapedText = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEscapedText() {
		return escapedText;
	}

	public void setEscapedText(String escapedText) {
		this.escapedText = escapedText;
	}

	@Override
	public boolean equals(Object obj) {
		return text.equals(obj);
	}

	@Override
	public String toString() {
		return escapedText;
	}

}
