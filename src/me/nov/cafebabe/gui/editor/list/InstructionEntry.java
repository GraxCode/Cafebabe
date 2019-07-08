package me.nov.cafebabe.gui.editor.list;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.nov.cafebabe.utils.asm.Descriptors;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.EscapedString;
import me.nov.cafebabe.utils.formatting.Html;
import me.nov.cafebabe.utils.formatting.OpUtils;

public class InstructionEntry {
	public MethodNode mn;
	public AbstractInsnNode ain;

	public InstructionEntry(MethodNode mn, AbstractInsnNode ain) {
		super();
		this.mn = mn;
		this.ain = ain;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		int type = ain.getType();
		switch (type) {
		case AbstractInsnNode.LABEL:
			sb.append(Html.italics(Html.color(Colors.debug_grey, "label " + OpUtils.getLabelIndex(ain))));
			return sb.toString();
		case AbstractInsnNode.LINE:
			sb.append(Html.italics(Html.color(Colors.debug_grey, "line " + ((LineNumberNode) ain).line)));
			return sb.toString();
		}
		int opcode = ain.getOpcode();

		sb.append(Html.color(Colors.getColor(type, opcode), Html.bold(OpUtils.getOpcodeText(opcode).toLowerCase())));
		sb.append('\t');
		switch (type) {
		case AbstractInsnNode.VAR_INSN:
			sb.append(((VarInsnNode) ain).var);
			break;
		case AbstractInsnNode.JUMP_INSN:
			sb.append(OpUtils.getLabelIndex(((JumpInsnNode) ain).label));
			break;
		case AbstractInsnNode.TYPE_INSN:
			sb.append(new EscapedString(((TypeInsnNode) ain).desc.replace('/', '.')));
			break;
		case AbstractInsnNode.FIELD_INSN:
			FieldInsnNode fin = (FieldInsnNode) ain;
			sb.append(Descriptors.getDisplayType(fin.desc));
			sb.append(" ");
			sb.append(new EscapedString(fin.owner.replace('/', '.')));
			sb.append(".");
			sb.append(Html.italics(Html.color(Colors.fields, fin.name)));
			break;
		case AbstractInsnNode.METHOD_INSN:
			MethodInsnNode min = (MethodInsnNode) ain;
			sb.append(Descriptors.getDisplayType(min.desc.split("\\)")[1]));
			sb.append(" ");
			sb.append(new EscapedString(min.owner.replace('/', '.')));
			sb.append(".");
			sb.append(Html.italics(Html.color(Colors.fields, new EscapedString(min.name).getEscapedText())));
			sb.append("(");
			sb.append(Descriptors.getDisplayType(min.desc.split("\\)")[0].substring(1)));
			sb.append(")");
			break;
		case AbstractInsnNode.LDC_INSN:
			LdcInsnNode ldc = (LdcInsnNode) ain;
			if (ldc.cst instanceof String) {
				sb.append(Html.color(Colors.fields, "\"" + new EscapedString(ldc.cst.toString()) + "\""));
			} else {
				sb.append(Html.color(Colors.comment, ldc.cst.getClass().getSimpleName()));
				sb.append(" ");
				sb.append(ldc.cst.toString());
			}

		}
		return sb.toString();
	}

}
