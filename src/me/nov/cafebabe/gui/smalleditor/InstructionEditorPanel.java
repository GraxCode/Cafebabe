package me.nov.cafebabe.gui.smalleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.gui.editor.list.InstructionList;
import me.nov.cafebabe.gui.opchooser.OpcodeChooserDialog;
import me.nov.cafebabe.utils.asm.Descriptors;
import me.nov.cafebabe.utils.asm.OpcodeLink;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.Html;
import me.nov.cafebabe.utils.formatting.OpcodeFormatting;
import me.nov.cafebabe.utils.ui.Listeners;
import me.nov.cafebabe.utils.ui.WebLaF;

public class InstructionEditorPanel extends JPanel implements Opcodes {
	private static final long serialVersionUID = 1L;

	public InstructionEditorPanel(InstructionList instructionList, MethodNode mn, AbstractInsnNode ain) {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
		this.setFocusable(false);
		WebLabel opcode = new WebLabel(WebLabel.CENTER);
		opcode.setText("<html>" + Html.color(Colors.getColor(ain.getType(), ain.getOpcode()),
				Html.bold(OpcodeFormatting.getOpcodeText(ain.getOpcode()).toLowerCase())));
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
					if (OpcodeLink.getOpcodeNode(newOp).getName().equals(ain.getClass().getName())) {
						opcode.setText("<html>" + Html.color(Colors.getColor(ain.getType(), newOp),
								Html.bold(OpcodeFormatting.getOpcodeText(newOp).toLowerCase())));
						ain.setOpcode(newOp);
						instructionList.repaint();
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
		gbc.weightx = 1;
		this.add(opcodeLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(opcode, gbc);

		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);
		for (Field f : ain.getClass().getDeclaredFields()) {
			if (!Modifier.isPublic(f.getModifiers()))
				continue;
			f.setAccessible(true);
			gbc.gridy++;
			gbc.gridwidth = 1;
			String name = f.getName();
			this.add(new JLabel(name.substring(0, 1).toUpperCase() + name.substring(1) + ":"), gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			this.add(createEditor(instructionList, ain, name, f), gbc);
		}
		this.add(new JPanel(), new GridBagConstraints(1, 10, 4, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(Box.createGlue(), new GridBagConstraints(0, 11, 4, 1, 0, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	private Component createEditor(InstructionList il, AbstractInsnNode ain, String name, Field f) {
		try {
			Object value = f.get(ain);
			int type = ain.getType();
			if (name.equals("desc")) {
				if (type == AbstractInsnNode.METHOD_INSN) {
					return createInOutDescEditor(il, ain, f, value);
				}
				return createSingleDescEditor(il, ain, f, value);
			}
			if (f.getType() == String.class) {
				WebTextField wtf = new WebTextField();
				wtf.setText(value != null ? String.valueOf(value) : "");
				Listeners.addChangeListener(wtf, l -> {
					String text = wtf.getText().trim();
					setField(f, ain, text.isEmpty() ? null : text);
					il.repaint();
				});
				return wtf;
			}
			if (f.getType() == boolean.class) {
				WebCheckBox wcb = new WebCheckBox((boolean) value);
				wcb.addChangeListener(l -> {
					setField(f, ain, wcb.isSelected());
					il.repaint();
				});
				return wcb;
			}
			if (f.getType() == int.class) {
				WebSpinner ws = new WebSpinner();
				ws.setValue(value);
				ws.addChangeListener(l -> {
					setField(f, ain, ws.getValue());
					il.repaint();
				});
				return ws;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JLabel("<html><i>not editable yet...");
	}

	private void setField(Field f, AbstractInsnNode ain, Object object) {
		// to avoid try catch blocks all the time..
		try {
			f.set(ain, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WebTextField createSingleDescEditor(InstructionList il, AbstractInsnNode ain, Field f, Object value) {
		WebTextField argsField = new WebTextField(Descriptors.getDisplayTypeEditable(((String) value)));

		Listeners.addChangeListener(argsField, c -> {
			setField(f, ain, Descriptors.displayTypeToDesc(argsField.getText().trim()));
			il.repaint();
		});
		return argsField;
	}

	private JPanel createInOutDescEditor(InstructionList il, AbstractInsnNode ain, Field f, Object value) {
		String[] descArgs = ((String) value).split("\\)");
		WebTextField argsField = new WebTextField(Descriptors.getDisplayTypeEditable(descArgs[0].substring(1)));
		WebTextField retField = new WebTextField(Descriptors.getDisplayTypeEditable(descArgs[1]));
		retField.setMinimumWidth(20);
		ChangeListener l = (c -> {
			StringBuilder desc = new StringBuilder();
			String args = argsField.getText().replace(" ", ""); // spaces don't matter
			String[] arg = args.split(",");
			desc.append("(");
			for (int i = 0; i < arg.length; i++)
				desc.append(Descriptors.displayTypeToDesc(arg[i]));
			desc.append(")");
			desc.append(Descriptors.displayTypeToDesc(retField.getText().trim()));
			setField(f, ain, desc.toString());
			il.repaint();
		});

		Listeners.addChangeListener(argsField, l);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(argsField, BorderLayout.CENTER);
		panel.add(retField, BorderLayout.EAST);
		return panel;
	}
}
