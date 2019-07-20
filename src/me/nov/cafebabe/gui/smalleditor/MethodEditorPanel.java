package me.nov.cafebabe.gui.smalleditor;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.ClassMemberList;
import me.nov.cafebabe.gui.decompiler.DecompilerPanel;
import me.nov.cafebabe.gui.editor.InstructionPanel;
import me.nov.cafebabe.gui.graph.CFGPanel;
import me.nov.cafebabe.gui.node.MethodListNode;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;
import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.asm.Descriptors;
import me.nov.cafebabe.utils.formatting.Colors;
import me.nov.cafebabe.utils.ui.Listeners;
import me.nov.cafebabe.utils.ui.WebLaF;

public class MethodEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private MethodNode method;
	private WebTextField name;
	private WebButtonGroup access;
	private WebTextField arguments;
	private WebTextField returns;
	private WebTextField signature;
	private WebTextField exceptions;
	private WebCheckBox hasSignature;

	public MethodEditorPanel(ClassMemberList methodList) {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
		this.setFocusable(false);
		name = new WebTextField(20);
		Listeners.addChangeListener(name, l -> {
			String trim = name.getText().trim();
			if (!trim.isEmpty())
				method.name = trim;
		});
		JLabel nameLabel = new JLabel(Translations.get("Name:"));
		nameLabel.setDisplayedMnemonic('N');
		nameLabel.setLabelFor(name);

		access = accessToggleGroup();
		Listeners.addMouseReleasedListener(access, () -> {
			int acc = method.access; // do not lose old access
			for (Component c : access.getComponents()) {
				WebToggleButton tb = (WebToggleButton) c;
				try {
					int accessInt = Opcodes.class.getField("ACC_" + tb.getToolTipText().toUpperCase()).getInt(null);
					if (tb.isSelected()) {
						acc |= accessInt;
					} else {
						acc &= ~accessInt;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			method.access = acc;
		}, true);
		JLabel accessLabel = new JLabel(Translations.get("Access:"));
		accessLabel.setDisplayedMnemonic('A');
		accessLabel.setLabelFor(access);

		arguments = new WebTextField(20);
		Listeners.addChangeListener(arguments, l -> {
			editDesc(arguments.getText(), returns.getText());
		});
		JLabel argsLabel = new JLabel(Translations.get("Arguments:"));
		argsLabel.setDisplayedMnemonic('A');
		argsLabel.setLabelFor(arguments);

		returns = new WebTextField(20);
		Listeners.addChangeListener(returns, l -> {
			editDesc(arguments.getText(), returns.getText());
		});
		JLabel returnLabel = new JLabel(Translations.get("Returns:"));
		returnLabel.setDisplayedMnemonic('R');
		returnLabel.setLabelFor(returns);

		signature = new WebTextField(20);
		signature.setMargin(0, 2, 0, 0);
		hasSignature = new WebCheckBox();
		hasSignature.setCursor(Cursor.getDefaultCursor());
		hasSignature.setSelected(false);
		hasSignature.setFocusable(false);
		signature.setLeadingComponent(hasSignature);
		Listeners.addChangeListener(signature, l -> {
			if (hasSignature.isSelected()) {
				method.signature = signature.getText().trim();
			} else {
				method.signature = null;
			}
		});
		hasSignature.addActionListener(l -> {
			if (hasSignature.isSelected()) {
				method.signature = signature.getText().trim();
			} else {
				method.signature = null;
			}
		});
		JLabel signatureLabel = new JLabel(Translations.get("Signature:"));
		signatureLabel.setDisplayedMnemonic('S');
		signatureLabel.setLabelFor(returns);

		exceptions = new WebTextField(20);
		Listeners.addChangeListener(exceptions, l -> {
			String excp = exceptions.getText();
			excp = excp.replace(" ", ""); // spaces don't matter
			String[] excpts = excp.split(",");

			method.exceptions = new ArrayList<>();
			for (String exc : excpts) {
				method.exceptions.add(exc);
			}
		});
		JLabel excLabel = new JLabel(Translations.get("Throws:"));
		excLabel.setDisplayedMnemonic('T');
		excLabel.setLabelFor(exceptions);

		JButton edit = new JButton(Translations.get("Edit Code"));
		edit.addActionListener(l -> {
			MethodListNode mln = (MethodListNode) methodList.getLastSelectedPathComponent();
			Icon icon = ((JLabel) methodList.getCellRenderer().getTreeCellRendererComponent(methodList, mln, false, false,
					true, 0, false)).getIcon();
			Cafebabe.gui.openEditor(new JScrollPane(new InstructionPanel(mln.getMethod())),
					mln.getClazz().name + "." + mln.getMethod().name, Colors.methodTabColor, icon);
		});
		edit.setPreferredSize(new Dimension(100, (int) edit.getPreferredSize().getHeight()));
		JButton decompile = new JButton(Translations.get("Decompile"));
		decompile.addActionListener(l -> {
			MethodListNode mln = (MethodListNode) methodList.getLastSelectedPathComponent();
			Icon icon = ((JLabel) methodList.getCellRenderer().getTreeCellRendererComponent(methodList, mln, false, false,
					true, 0, false)).getIcon();
			Cafebabe.gui.openEditor(new DecompilerPanel(mln.getClazz(), mln.getMethod()),
					mln.getClazz().name + "." + mln.getMethod().name, Colors.decompilerTabColor, icon);
		});
		decompile.setPreferredSize(new Dimension(100, (int) decompile.getPreferredSize().getHeight()));
		JButton graph = new JButton(Translations.get("Create Graph"));
		graph.addActionListener(l -> {
			MethodListNode mln = (MethodListNode) methodList.getLastSelectedPathComponent();
			Icon icon = ((JLabel) methodList.getCellRenderer().getTreeCellRendererComponent(methodList, mln, false, false,
					true, 0, false)).getIcon();
			Cafebabe.gui.openEditor(new CFGPanel(mln.getMethod()),
					"Graph of " + mln.getClazz().name + "." + mln.getMethod().name, Colors.graphTabColor, icon);
		});
		graph.setPreferredSize(new Dimension(100, (int) decompile.getPreferredSize().getHeight()));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 6, 0, 0);
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;

		this.add(WebLaF.createInfoLabel(nameLabel,
				"<html>For constructors use \"&lt;init&gt;\",<br>for initialization method use \"&lt;clinit&gt;\""), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(name, gbc);

		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(accessLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(access, gbc);
		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);
		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(argsLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		this.add(arguments, gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(returnLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(returns, gbc);

		gbc.gridy++;
		this.add(WebLaF.createSeparator(), gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(signatureLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(signature, gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		this.add(WebLaF.createInfoLabel(excLabel, Translations.get("Separated by a comma")), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		this.add(exceptions, gbc);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc.gridwidth = 1;
		gbc.gridy = 0;

		buttonPanel.add(edit, gbc);
		buttonPanel.add(decompile, gbc);
		buttonPanel.add(graph, gbc);
		this.add(buttonPanel, new GridBagConstraints(1, 9, 4, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));

		this.add(Box.createGlue(), new GridBagConstraints(0, 10, 4, 1, 0, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	private void editDesc(String args, String ret) {
		args = args.replace(" ", ""); // spaces don't matter
		String[] arg = args.split(",");
		method.desc = "(";
		for (int i = 0; i < arg.length; i++)
			method.desc += Descriptors.displayTypeToDesc(arg[i]);
		method.desc += ")";
		method.desc += Descriptors.displayTypeToDesc(ret.trim());
	}

	private WebButtonGroup accessToggleGroup() {
		// do not change tooltips
		WebToggleButton private_ = new WebToggleButton(MethodListCellRenderer.pri);
		private_.setToolTipText("private");
		WebToggleButton public_ = new WebToggleButton(MethodListCellRenderer.pub);
		public_.setToolTipText("public");
		WebToggleButton protected_ = new WebToggleButton(MethodListCellRenderer.pro);
		protected_.setToolTipText("protected");
		WebToggleButton static_ = new WebToggleButton(MethodListCellRenderer.stat);
		static_.setToolTipText("static");
		WebToggleButton final_ = new WebToggleButton(MethodListCellRenderer.fin);
		final_.setToolTipText("final");
		WebToggleButton abstract_ = new WebToggleButton(MethodListCellRenderer.abs);
		abstract_.setToolTipText("abstract");
		WebToggleButton native_ = new WebToggleButton(MethodListCellRenderer.nat);
		native_.setToolTipText("native");
		WebToggleButton synchronized_ = new WebToggleButton(MethodListCellRenderer.syn);
		synchronized_.setToolTipText("synchronized");
		WebToggleButton synthetic_ = new WebToggleButton(MethodListCellRenderer.synth);
		synthetic_.setToolTipText("synthetic");

		private_.addActionListener(l -> {
			public_.setSelected(false);
			protected_.setSelected(false);
		});
		public_.addActionListener(l -> {
			protected_.setSelected(false);
			private_.setSelected(false);
		});
		protected_.addActionListener(l -> {
			public_.setSelected(false);
			private_.setSelected(false);
		});
		WebButtonGroup accessGroup = new WebButtonGroup(false, private_, public_, protected_, static_, final_, abstract_,
				native_, synchronized_, synthetic_);
		accessGroup.setButtonsDrawFocus(false);

		return accessGroup;
	}

	public void editMethod(MethodNode method) {
		this.method = method;
		this.name.setText(method.name);
		for (Field f : Opcodes.class.getDeclaredFields()) {
			try {
				if (f.getName().startsWith("ACC_")) {
					int acc = f.getInt(null);
					String accName = f.getName().substring(4).toLowerCase();
					for (Component c : access.getComponents()) {
						WebToggleButton tb = (WebToggleButton) c;
						if (tb.getToolTipText().equals(accName)) {
							tb.setSelected((method.access & acc) != 0);
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.arguments.setText(Descriptors.getDisplayTypeEditable(method.desc.split("\\)")[0].substring(1)));
		this.returns.setText(Descriptors.getDisplayTypeEditable(method.desc.split("\\)")[1]));
		if (method.signature != null) {
			hasSignature.setSelected(true);
			this.signature.setText(method.signature);
		} else {
			hasSignature.setSelected(false);
		}
		this.exceptions.setText(String.join(", ", method.exceptions));
	}

}
