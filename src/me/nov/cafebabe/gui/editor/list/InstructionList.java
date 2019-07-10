package me.nov.cafebabe.gui.editor.list;

import java.awt.Font;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.node.InstructionNode;
import me.nov.cafebabe.gui.smalleditor.InstructionEditorPanel;
import me.nov.cafebabe.utils.ui.LazyListModel;

public class InstructionList extends JList<InstructionNode> {
	private static final long serialVersionUID = 1L;
	public MethodNode mn;

	public InstructionList(MethodNode mn) {
		this.mn = mn;
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		this.refresh(mn);
		this.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent arg0) {
          if (!arg0.getValueIsAdjusting()) {
          	InstructionNode in = getSelectedValue();
          	Cafebabe.gui.smallEditorPanel.setViewportView(new InstructionEditorPanel(InstructionList.this, mn, in.ain));
          }
      }
  });
	}

	public void refresh(MethodNode mn) {
		LazyListModel<InstructionNode> llm = new LazyListModel<InstructionNode>();
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() != AbstractInsnNode.FRAME)
				llm.addElement(new InstructionNode(mn, ain));
		}
		this.setModel(llm);
		this.repaint();
	}

}
