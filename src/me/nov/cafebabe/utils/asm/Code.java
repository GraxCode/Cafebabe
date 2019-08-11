package me.nov.cafebabe.utils.asm;

import java.util.HashMap;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
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

	public static int getLabelCount(InsnList instructions) {
		int i = 0;
		for (AbstractInsnNode ain : instructions.toArray()) {
			if (ain instanceof LabelNode) {
				i++;
			}
		}
		return i;
	}

	public static LabelNode getLabelByIndex(InsnList instructions, int index) {
		int i = 0;
		for (AbstractInsnNode ain : instructions.toArray()) {
			if (ain instanceof LabelNode) {
				if(i == index) {
					return (LabelNode) ain;
				}
				i++;
			}
		}
		return null;
	}

}
