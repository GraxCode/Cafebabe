package me.nov.cafebabe.utils.asm;

import java.util.HashMap;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class Code {
	public static AbstractInsnNode cloneNode(AbstractInsnNode ain) {
		if (ain instanceof LabelNode) {
			return new LabelNode();
		} else if (ain instanceof JumpInsnNode) {
			return new JumpInsnNode(ain.getOpcode(), ((JumpInsnNode) ain).label);
		} else {
			return ain.clone(new HashMap<>());
		}
	}

}
