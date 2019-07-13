package me.nov.cafebabe.gui.node;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.utils.formatting.InstructionFormatting;

public class InstructionNode {
	public MethodNode mn;
	public AbstractInsnNode ain;

	public InstructionNode(MethodNode mn, AbstractInsnNode ain) {
		super();
		this.mn = mn;
		this.ain = ain;
	}

	@Override
	public String toString() {
		return InstructionFormatting.nodeToString(mn, ain);
	}

}
