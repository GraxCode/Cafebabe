package me.nov.cafebabe.gui.editor.list;

import java.awt.Font;

import javax.swing.JList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.utils.ui.LazyListModel;

public class InstructionList extends JList<InstructionEntry> {
	private static final long serialVersionUID = 1L;
	public MethodNode mn;

	public InstructionList(MethodNode mn) {
		this.mn = mn;
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		this.refresh(mn);
	}

	public void refresh(MethodNode mn) {
		LazyListModel<InstructionEntry> llm = new LazyListModel<InstructionEntry>();
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() != AbstractInsnNode.FRAME)
				llm.addElement(new InstructionEntry(mn, ain));
		}
		this.setModel(llm);
		this.repaint();
	}

}
