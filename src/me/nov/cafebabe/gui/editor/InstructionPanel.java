package me.nov.cafebabe.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.gui.editor.list.AdressList;
import me.nov.cafebabe.gui.editor.list.InstructionList;

public class InstructionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public InstructionPanel(MethodNode mn) {
		this.setLayout(new BorderLayout());
		InstructionList il = new InstructionList(mn);
		this.add(il, BorderLayout.CENTER);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
		p.add(new AdressList(il), BorderLayout.CENTER);
		this.add(p, BorderLayout.WEST);
	}
}
