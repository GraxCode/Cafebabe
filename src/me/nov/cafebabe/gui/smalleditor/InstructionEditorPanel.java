package me.nov.cafebabe.gui.smalleditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.gui.editor.list.InstructionList;
import me.nov.cafebabe.gui.opchooser.OpcodeChooserDialog;
import me.nov.cafebabe.utils.asm.OpcodeLink;
import me.nov.cafebabe.utils.formatting.OpcodeFormatting;
import me.nov.cafebabe.utils.ui.WebLaF;

public class InstructionEditorPanel extends JPanel implements Opcodes {
	private static final long serialVersionUID = 1L;

	public InstructionEditorPanel(InstructionList instructionList, MethodNode mn, AbstractInsnNode ain) {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
		this.setFocusable(false);
		WebTextField opcode = new WebTextField(20);
		opcode.setText(OpcodeFormatting.getOpcodeText(ain.getOpcode()).toLowerCase());
		opcode.setEditable(false);
		opcode.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				int newOp = new OpcodeChooserDialog(ain).getOpcode();
				if (newOp != ain.getOpcode()) {
					opcode.setText(OpcodeFormatting.getOpcodeText(newOp));
					if(OpcodeLink.getOpcodeNode(newOp).getName().equals(ain.getClass().getName())) {
						ain.setOpcode(newOp);
					} else {
						throw new RuntimeException("unimplemented");
					}
				}
			}
		});
		JLabel opcodeLabel = new JLabel("Opcode:");
		opcodeLabel.setDisplayedMnemonic('O');
		opcodeLabel.setLabelFor(opcode);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 6, 0, 0);
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;

		this.add(opcodeLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(opcode, gbc);

		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

	}
}
