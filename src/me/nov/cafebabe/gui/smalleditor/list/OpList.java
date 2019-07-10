package me.nov.cafebabe.gui.smalleditor.list;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JOptionPane;

import org.objectweb.asm.tree.AbstractInsnNode;

import me.nov.cafebabe.gui.node.OpcodeNode;
import me.nov.cafebabe.utils.asm.OpcodeLink;
import me.nov.cafebabe.utils.ui.LazyListModel;

public class OpList extends JList<OpcodeNode> {
	private static final long serialVersionUID = 1L;
	private int[] opcodes;
	private AbstractInsnNode ain;

	public OpList(int... opcodes) {
		this.opcodes = opcodes;
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		LazyListModel<OpcodeNode> llm = new LazyListModel<OpcodeNode>();
		for (int opcode : opcodes) {
			llm.addElement(new OpcodeNode(opcode));
		}
		this.setModel(llm);
		this.repaint();
		for (MouseListener ml : this.getMouseListeners())
			this.removeMouseListener(ml);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					int index = locationToIndex(evt.getPoint());
					OpcodeNode on = llm.getElementAt(index);

					// if opcode is of same type else confirm change
					boolean sameType = OpcodeLink.getOpcodeNode(on.opcode).getName()
							.equals(OpcodeLink.getOpcodeNode(ain.getOpcode()).getName());
					if (sameType || JOptionPane.showConfirmDialog(OpList.this,
							"Do you really want to change the type of this instruction?\nThis will erase all data from this node.",
							"Changing instruction type", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						if (sameType) {
							ain.setOpcode(on.opcode);
						} else {
							// TODO
							throw new IllegalArgumentException("unimplemented");
						}
						setSelectedIndex(index);
					}
				}
			}
		});
	}

	public void setNode(AbstractInsnNode ain) {
		this.ain = ain;
		int i = 0;
		boolean set = false;
		for (int opcode : opcodes) {
			if (ain.getOpcode() == opcode) {
				this.setSelectedIndex(i);
				set = true;
				break;
			}
			i++;
		}
		if (!set) {
			this.clearSelection();
		}
	}
}
