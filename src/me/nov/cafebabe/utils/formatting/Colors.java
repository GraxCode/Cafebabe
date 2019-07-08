package me.nov.cafebabe.utils.formatting;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

public class Colors implements Opcodes {
	public static final String eclipse_keyword = "#7f0055";
	public static final String debug_grey = "#878787";
	public static final String fields = "#0000c0";
	public static final String local_vars = "#6a3e3e";
	public static final String comment = "#3f7f5f";

	public static String getColor(int type, int opcode) {
		switch (opcode) {
		case ATHROW:
		case IRETURN:
		case LRETURN:
		case FRETURN:
		case DRETURN:
		case ARETURN:
		case RETURN:
			return "#3b0700";
		case ACONST_NULL:
		case ICONST_M1:
		case ICONST_0:
		case ICONST_1:
		case ICONST_2:
		case ICONST_3:
		case ICONST_4:
		case ICONST_5:
		case LCONST_0:
		case LCONST_1:
		case FCONST_0:
		case FCONST_1:
		case FCONST_2:
		case DCONST_0:
		case DCONST_1:
			return "#617f00";
		}
		switch (type) {
		case AbstractInsnNode.FIELD_INSN:
			return "#003d7f";
		case AbstractInsnNode.METHOD_INSN:
			return "#7f3500";
		case AbstractInsnNode.INT_INSN:
		case AbstractInsnNode.LDC_INSN:
			return "#617f00";
		case AbstractInsnNode.VAR_INSN:
			return "#7f7400";
		case AbstractInsnNode.JUMP_INSN:
			return "#7b007f";
		case AbstractInsnNode.TYPE_INSN:
			return "#7f002f";
		default:
			return "#000";
		}
	}
}
