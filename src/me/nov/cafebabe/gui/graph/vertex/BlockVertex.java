package me.nov.cafebabe.gui.graph.vertex;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.analysis.blocks.Block;
import me.nov.cafebabe.utils.formatting.InstructionFormatting;

public class BlockVertex {
	public MethodNode mn;
	public ArrayList<AbstractInsnNode> nodes;

	public LabelNode label;
	public int listIndex;

	public Block block;
	public ArrayList<BlockVertex> inputBlocks = new ArrayList<>();

	public BlockVertex(MethodNode mn, Block block, ArrayList<AbstractInsnNode> nodes, LabelNode label, int listIndex) {
		super();
		this.mn = mn;
		this.block = block;
		this.nodes = nodes;
		this.label = label;
		this.listIndex = listIndex;
	}

	public void addInput(BlockVertex v) {
		if (!inputBlocks.contains(v)) {
			this.inputBlocks.add(v);
		}
	}

	private String text = null;

	@Override
	public String toString() {
		if (text == null) {
			StringBuilder sb = new StringBuilder();
			for (AbstractInsnNode ain : nodes) {
				if(ain.getType() == AbstractInsnNode.FRAME) {
					continue;
				}
				sb.append(InstructionFormatting.nodeToString(mn, ain));
				sb.append("\n");
			}
			text = sb.toString();
		}
		return text;
	}
}