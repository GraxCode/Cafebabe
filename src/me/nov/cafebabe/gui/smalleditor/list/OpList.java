package me.nov.cafebabe.gui.smalleditor.list;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import me.nov.cafebabe.gui.node.OpcodeNode;
import me.nov.cafebabe.gui.opchooser.OpcodeChooserDialog;
import me.nov.cafebabe.utils.ui.LazyListModel;

public class OpList extends JList<OpcodeNode> {
	private static final long serialVersionUID = 1L;
	private int[] opcodes = {};
	private OpcodeChooserDialog chooser;

	public OpList(OpcodeChooserDialog chooser, int... opcodes) {
		this.chooser = chooser;
		this.opcodes = opcodes;
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		LazyListModel<OpcodeNode> llm = new LazyListModel<OpcodeNode>();
		for (int opcode : opcodes) {
			llm.addElement(new OpcodeNode(opcode));
		}
		this.setModel(llm);
		this.refresh();
		this.repaint();
		for (MouseListener ml : this.getMouseListeners())
			this.removeMouseListener(ml);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() >= 1) {
					int index = locationToIndex(evt.getPoint());
					OpcodeNode on = llm.getElementAt(index);
					chooser.setOpcode(on.opcode);
					setSelectedIndex(index);
					chooser.refresh();
				}
			}
		});

	}

	public void refresh() {
		clearSelection();
		int i = 0;
		if (opcodes != null)
			for (int opcode : opcodes) {
				if (chooser.getOpcode() == opcode) {
					setSelectedIndex(i);
				}
				i++;
			}
		super.repaint();
	}
}
