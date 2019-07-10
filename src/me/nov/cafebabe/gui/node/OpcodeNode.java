package me.nov.cafebabe.gui.node;

import me.nov.cafebabe.utils.asm.Hints;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.Html;
import me.nov.cafebabe.utils.formatting.OpcodeFormatting;

public class OpcodeNode {

	public int opcode;
	public String help;
	private String text;

	public OpcodeNode(int opcode) {
		this.opcode = opcode;
		this.help = Hints.hints[opcode];
		this.text = "<html>"
				+ Html.color(Colors.getColor(-1, opcode), Html.bold(OpcodeFormatting.getOpcodeText(opcode).toLowerCase())) + "<br><font size=2>"
				+ Html.italics(Html.color(Colors.debug_grey, this.help));
	}

	@Override
	public String toString() {
		return text;
	}
}
