package me.nov.cafebabe.gui.editor.list;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.plaf.ListUI;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import com.alee.laf.list.WebListUI;

import me.nov.cafebabe.gui.node.InstructionNode;
import me.nov.cafebabe.utils.ui.LazyListModel;

public class AdressList extends JList<String> {
	private static final long serialVersionUID = 1L;
	private InstructionList il;

	public AdressList(InstructionList il) {
		super(new DefaultListModel<String>());
		this.il = il;
		this.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
		this.setFocusable(false);
		this.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSelectionInterval(int index0, int index1) {
				super.setSelectionInterval(-1, -1);
			}
		});
		this.setPrototypeCellValue("0000");
		this.setEnabled(true);
		this.setFocusable(false);
		this.setModel(new DefaultListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize() {
				return il.getModel().getSize();
			}

			@Override
			protected void fireIntervalAdded(Object source, int index0, int index1) {
			}

			@Override
			public String getElementAt(int index) {
				String hex = String.valueOf(index);
				return "0000".substring(hex.length()) + hex; // "<html><b>"
			}
		});
		ListUI ui = this.getUI();
		if (ui instanceof WebListUI) {
			WebListUI wlui = (WebListUI) ui;
			wlui.setHighlightRolloverCell(false);
			wlui.setDecorateSelection(false);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		double size = il.getModel().getSize();
		if (size > 1000) {
			super.paintComponent(g);
			return;
		}
		super.paintComponent(g);
		HashMap<Integer, Integer> mvout = new HashMap<>();
		for (AbstractInsnNode ain : il.mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.JUMP_INSN) {
				int moveOut = 0;
				int from = ((LazyListModel<InstructionNode>) il.getModel()).indexOf(ain);
				int to = ((LazyListModel<InstructionNode>) il.getModel()).indexOf(((JumpInsnNode) ain).label);
				for (int j = from; j < to; j++) {
					moveOut = Math.max(mvout.getOrDefault(j, 0) + 4, moveOut);
				}
				for (int j = from; j < to; j++) {
					mvout.put(j, moveOut);
				}
				int start = (from * getFixedCellHeight() + getFixedCellHeight() / 2);
				int end = (to * getFixedCellHeight() + getFixedCellHeight() / 2);
				Color c = Color.getHSBColor(getHue(moveOut), 1, 0.6f);
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 127));
				g.drawLine(getWidth() - moveOut, start, getWidth() - moveOut, end);
				g.drawLine(getWidth() - moveOut, start, getWidth(), start);
				g.drawLine(getWidth() - moveOut, end, getWidth(), end);
			}
		}
	}

	private float getHue(int moveOut) {
		float hue = moveOut / (float) (getWidth() / 2) + 0.5f;
		return hue % 1.0f;
	}
}
