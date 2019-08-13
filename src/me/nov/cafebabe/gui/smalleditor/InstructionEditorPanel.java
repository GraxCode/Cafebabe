package me.nov.cafebabe.gui.smalleditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.gui.editor.list.InstructionList;
import me.nov.cafebabe.gui.opchooser.OpcodeChooserDialog;
import me.nov.cafebabe.utils.asm.Code;
import me.nov.cafebabe.utils.asm.Descriptors;
import me.nov.cafebabe.utils.asm.OpcodeLink;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.formatting.Html;
import me.nov.cafebabe.utils.formatting.OpcodeFormatting;
import me.nov.cafebabe.utils.ui.Listeners;
import me.nov.cafebabe.utils.ui.WebLaF;

public class InstructionEditorPanel extends JPanel implements Opcodes {
	private static final long serialVersionUID = 1L;

	public InstructionEditorPanel(InstructionList instructionList, ClassNode cn, MethodNode mn, AbstractInsnNode ain) {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
		this.setFocusable(false);
		WebLabel opcode = new WebLabel(WebLabel.LEFT);
		opcode.setText("<html>\u00A0" + Html.color(Colors.getColor(ain.getType(), ain.getOpcode()),
				Html.bold(OpcodeFormatting.getOpcodeText(ain.getOpcode()).toLowerCase())));
		Listeners.addMouseReleasedListener(opcode, () -> {
			int newOp = new OpcodeChooserDialog(ain).getOpcode();
			if (newOp != ain.getOpcode()) {
				if (OpcodeLink.getOpcodeNode(newOp).getName().equals(ain.getClass().getName())) {
					opcode.setText("<html>\u00A0" + Html.color(Colors.getColor(ain.getType(), newOp),
							Html.bold(OpcodeFormatting.getOpcodeText(newOp).toLowerCase())));
					ain.setOpcode(newOp);
					instructionList.repaint();
				} else {
					mn.instructions.set(ain, OpcodeLink.constructNode(newOp, cn, Code.getLabelByIndex(mn.instructions, 0)));
					instructionList.refresh(mn);
				}
			}
		}, false);

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
			gbc.weightx = 1;
			this.add(createEditor(instructionList, ain, name, f), gbc);
		}

		int gridy = gbc.gridy;
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, (int) panel.getPreferredSize().getHeight()));
		this.add(panel, new GridBagConstraints(1, gridy + 1, 4, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(Box.createGlue(), new GridBagConstraints(0, gridy + 2, 4, 1, 0, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	private static final int maxWidth = 200;

	private Component createEditor(InstructionList il, AbstractInsnNode ain, String name, Field f) {
		try {
			Object value = f.get(ain);
			int type = ain.getType();
			if (type == AbstractInsnNode.LDC_INSN) {
				return createLdcEditor(il, (LdcInsnNode) ain, f);
			}
			if (name.equals("desc")) {
				if (type == AbstractInsnNode.METHOD_INSN) {
					return createInOutDescEditor(il, ain, f, value);
				}
				return createSingleDescEditor(il, ain, f, value);
			}
			if (f.getType() == LabelNode.class) {

				int labels = Code.getLabelCount(il.mn.instructions);
				if (labels == 0) {
					WebSpinner ws = new WebSpinner();
					ws.setValue(-1);
					ws.setEnabled(false);
					return ws;
				}
				WebSpinner ws = new WebSpinner(
						new SpinnerNumberModel(OpcodeFormatting.getLabelIndex((AbstractInsnNode) value), 0, labels - 1, 1));
				ws.addChangeListener(l -> {
					setField(f, ain, Code.getLabelByIndex(il.mn.instructions, (int) ws.getValue()));
					il.repaint();
				});
				return ws;
			}
			if (f.getType() == String.class) {
				WebTextField wtf = new WebTextField();
				wtf.setMaximumWidth(maxWidth);
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

	private Component createLdcEditor(InstructionList il, LdcInsnNode ain, Field f) {
		String[] items = { "String", "Integer", "Float", "Long", "Double", "Type", "Handle", "ConstantDynamic" };

		WebComboBox wcb = new WebComboBox(items);
		wcb.setSelectedItem(ain.cst.getClass().getSimpleName());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(wcb, BorderLayout.WEST);
		WebTextField field = new WebTextField(ain.cst.toString());
		field.setMaximumWidth(maxWidth / 2);
		field.setInputPrompt("only String supported yet..");
		wcb.setEnabled(false);
		field.setEnabled(ain.cst.getClass().getSimpleName().equals("String"));
		Listeners.addChangeListener(field, c -> {
			setField(f, ain, field.getText().trim());
			il.repaint();
		});
		panel.add(field, BorderLayout.CENTER);
		return panel;
	}

	/*
	 * if (cst instanceof Integer) { // ... } else if (cst instanceof Float) { // ... } else if (cst instanceof Long) { // ... } else if (cst instanceof Double) { // ... } else if (cst instanceof String) { // ... } else if (cst instanceof Type) { int sort = ((Type) cst).getSort(); if (sort == Type.OBJECT) { // ... } else if (sort == Type.ARRAY) { // ... } else if (sort == Type.METHOD) { // ... } else { // throw an exception } } else if (cst instanceof Handle) { // ... } else if (cst instanceof ConstantDynamic) { // ... } else { // throw an exception }
	 */
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
		argsField.setMaximumWidth(maxWidth);
		Listeners.addChangeListener(argsField, c -> {
			setField(f, ain, Descriptors.displayTypeToDesc(argsField.getText().trim()));
			il.repaint();
		});
		return argsField;
	}

	private JPanel createInOutDescEditor(InstructionList il, AbstractInsnNode ain, Field f, Object value) {
		String[] descArgs = ((String) value).split("\\)");
		WebTextField argsField = new WebTextField(Descriptors.getDisplayTypeEditable(descArgs[0].substring(1)));
		argsField.setMaximumWidth(maxWidth / 2);
		WebTextField retField = new WebTextField(Descriptors.getDisplayTypeEditable(descArgs[1]));
		retField.setMinimumWidth(maxWidth / 2);
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
