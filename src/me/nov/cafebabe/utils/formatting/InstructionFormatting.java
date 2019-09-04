package me.nov.cafebabe.utils.formatting;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.nov.cafebabe.utils.asm.Descriptors;

public class InstructionFormatting {

	public static String nodeToString(MethodNode mn, AbstractInsnNode ain) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		int type = ain.getType();
		switch (type) {
		case AbstractInsnNode.LABEL:
			sb.append(Html.italics(Html.color(Colors.debug_grey, "label " + OpcodeFormatting.getLabelIndex(ain))));
			return sb.toString();
		case AbstractInsnNode.LINE:
			sb.append(Html.italics(Html.color(Colors.debug_grey, "line " + ((LineNumberNode) ain).line)));
			return sb.toString();
		}
		int opcode = ain.getOpcode();

		sb.append(
				Html.color(Colors.getColor(type, opcode), Html.bold(OpcodeFormatting.getOpcodeText(opcode).toLowerCase())));
		sb.append('\t');
		switch (type) {
		case AbstractInsnNode.VAR_INSN:
			sb.append(((VarInsnNode) ain).var);
			break;
		case AbstractInsnNode.JUMP_INSN:
			sb.append(OpcodeFormatting.getLabelIndex(((JumpInsnNode) ain).label));
			break;
		case AbstractInsnNode.TYPE_INSN:
			sb.append(Descriptors.getDisplayType(((TypeInsnNode) ain).desc));
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
			sb.append(Html.italics(Html.color(Colors.methods, new EscapedString(min.name).getEscapedText())));
			sb.append("(");
			sb.append(Descriptors.getDisplayType(min.desc.split("\\)")[0].substring(1)));
			sb.append(")");
			break;
		case AbstractInsnNode.LDC_INSN:
			LdcInsnNode ldc = (LdcInsnNode) ain;
			if (ldc.cst instanceof String) {
				sb.append(Html.color(Colors.strings, "\"" + new EscapedString(ldc.cst.toString()) + "\""));
			} else {
				sb.append(Html.color(Colors.comment, ldc.cst.getClass().getSimpleName()));
				sb.append(" ");
				sb.append(ldc.cst.toString());
			}
			break;
		case AbstractInsnNode.INT_INSN:
			sb.append(((IntInsnNode) ain).operand);
			break;
		case AbstractInsnNode.MULTIANEWARRAY_INSN:
			MultiANewArrayInsnNode mani = (MultiANewArrayInsnNode) ain;
			sb.append(Descriptors.getDisplayType(mani.desc));
			for (int i = 0; i < mani.dims; i++)
				sb.append("[]");
			break;
		case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
			InvokeDynamicInsnNode idin = (InvokeDynamicInsnNode) ain;
			sb.append(Descriptors.getDisplayType(idin.desc.split("\\)")[1]));
			sb.append(" ");
			sb.append(new EscapedString(idin.name).getEscapedText());
			sb.append("(");
			sb.append(Descriptors.getDisplayType(idin.desc.split("\\)")[0].substring(1)));
			sb.append(")");
			if (idin.bsm != null) {
				sb.append(" ");
				sb.append(handleToString(idin.bsm));
			}
			if (idin.bsmArgs != null) {
				sb.append(" [");
				sb.append(Arrays.asList(idin.bsmArgs).stream().map(l -> bsmArgToString(l)).collect(Collectors.joining(", ")));
				sb.append("]");
			}
			break;
		case AbstractInsnNode.TABLESWITCH_INSN:
			TableSwitchInsnNode tsin = (TableSwitchInsnNode) ain;
			sb.append("[");
			sb.append(tsin.min);
			sb.append(", ");
			sb.append(tsin.max);
			sb.append("] -> [");
			sb.append(tsin.labels.stream().map(l -> String.valueOf(OpcodeFormatting.getLabelIndex(l)))
					.collect(Collectors.joining(", ")));
			sb.append("], [");
			sb.append(Html.italics(String.valueOf(OpcodeFormatting.getLabelIndex(tsin.dflt))));
			sb.append("]");
			break;
		case AbstractInsnNode.LOOKUPSWITCH_INSN:
			LookupSwitchInsnNode lsin = (LookupSwitchInsnNode) ain;
			sb.append(Arrays.toString(lsin.keys.toArray()));
			sb.append(" -> [");
			sb.append(lsin.labels.stream().map(l -> String.valueOf(OpcodeFormatting.getLabelIndex(l)))
					.collect(Collectors.joining(", ")));
			sb.append("], [");
			sb.append(Html.italics(String.valueOf(OpcodeFormatting.getLabelIndex(lsin.dflt))));
			sb.append("]");
			break;
		}
		return sb.toString();
	}

	private static String bsmArgToString(Object l) {
		if (l instanceof Type) {
			return l.toString();
		}
		if (l instanceof Handle) {
			return handleToString((Handle) l);
		}
		return l.toString();
	}

	private static String handleToString(Handle bsm) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("<b><i>");
		sb.append(
				Html.color(bsm.tag < 5 ? "#44004d" : "#14004d", OpcodeFormatting.getHandleOpcodeText(bsm.tag).toLowerCase()));
		sb.append("</i></b> ");
		sb.append(Descriptors.getDisplayType(bsm.descriptor.split("\\)")[1]));
		sb.append(" ");
		sb.append(bsm.owner.replace('/', '.'));
		sb.append(".");
		sb.append(Html.italics(Html.color(Colors.methods, new EscapedString(bsm.name).getEscapedText())));
		sb.append("(");
		sb.append(Descriptors.getDisplayType(bsm.descriptor.split("\\)")[0].substring(1)));
		sb.append(")]");
		return sb.toString();
	}

}
