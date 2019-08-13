package me.nov.cafebabe.gui.editor.list;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.node.InstructionNode;
import me.nov.cafebabe.gui.smalleditor.InstructionEditorPanel;
import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.asm.Code;
import me.nov.cafebabe.utils.ui.LazyListModel;

public class InstructionList extends JList<InstructionNode> {
	private static final long serialVersionUID = 1L;
	public static InstructionNode copiedItem;
	public MethodNode mn;
	public AdressList addressList;

	public InstructionList(ClassNode cn, MethodNode mn) {
		this.mn = mn;
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		this.setFocusable(false);
		this.refresh(mn);
		this.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				InstructionNode in = getSelectedValue();
				if (!arg0.getValueIsAdjusting() && in != null) {
					Cafebabe.gui.smallEditorPanel.setViewportView(new InstructionEditorPanel(InstructionList.this, cn, mn, in.ain));
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					showPopupMenu();
				}
			}
		});
	}

	protected void showPopupMenu() {
		int selections[] = this.getSelectedIndices();
		if (selections.length == 1) {
			InstructionNode selection = this.getModel().getElementAt(selections[0]);
			JPopupMenu menu = new JPopupMenu();
			JMenuItem add = new JMenuItem(Translations.get("Insert instruction"));
			add.addActionListener(l -> {
				mn.instructions.insert(selection.ain, new InsnNode(Opcodes.NOP));
				this.refresh(mn);
				this.setSelectedIndex(selections[0] + 1);
			});
			menu.add(add);
			JMenuItem addLabel = new JMenuItem(Translations.get("Insert label"));
			addLabel.addActionListener(l -> {
				mn.instructions.insert(selection.ain, new LabelNode());
				this.refresh(mn);
				this.setSelectedIndex(selections[0] + 1);
			});
			menu.add(addLabel);
			JMenuItem remove = new JMenuItem(Translations.get("Remove"));
			remove.addActionListener(l -> {
				mn.instructions.remove(selection.ain);
				this.refresh(mn);
			});
			menu.add(remove);
			JMenuItem dupe = new JMenuItem(Translations.get("Clone"));
			dupe.addActionListener(l -> {
				mn.instructions.insert(selection.ain, Code.cloneNode(selection.ain));
				this.refresh(mn);
				this.setSelectedIndex(selections[0]);
			});
			menu.add(dupe);
			menu.add(new JSeparator());
			AbstractInsnNode prev = selection.ain.getPrevious();
			JMenuItem up = new JMenuItem(Translations.get("Move up"));
			up.addActionListener(l -> {
				mn.instructions.remove(prev);
				mn.instructions.insert(selection.ain, prev);
				this.refresh(mn);
				this.setSelectedIndex(selections[0] - 1);
			});
			up.setEnabled(prev != null);
			menu.add(up);
			AbstractInsnNode next = selection.ain.getNext();
			JMenuItem down = new JMenuItem(Translations.get("Move down"));
			down.addActionListener(l -> {
				mn.instructions.remove(next);
				mn.instructions.insertBefore(selection.ain, next);
				this.refresh(mn);
				this.setSelectedIndex(selections[0] + 1);
			});
			menu.add(down);
			down.setEnabled(next != null);
			menu.add(new JSeparator());
			JMenuItem copy = new JMenuItem(Translations.get("Copy"));
			copy.addActionListener(l -> {
				// TODO maybe copy to clipboard to allow transfer between different applications
				copiedItem = selection;
			});
			menu.add(copy);
			JMenuItem paste = new JMenuItem(Translations.get("Paste"));
			paste.addActionListener(l -> {
				mn.instructions.insert(selection.ain, Code.cloneNode(copiedItem.ain));
				this.refresh(mn);
				this.setSelectedIndex(selections[0]);
			});
			paste.setEnabled(copiedItem != null);
			menu.add(paste);
			menu.add(new JSeparator());
			JMenuItem copyText = new JMenuItem(Translations.get("Copy text"));
			copyText.addActionListener(l -> {
				StringSelection stringSelection = new StringSelection(selection.toString().replaceAll("<[^>]*>", ""));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
			});
			menu.add(copyText);
			menu.show(Cafebabe.gui.editorFrame, (int) Cafebabe.gui.editorFrame.getMousePosition().getX(),
					(int) Cafebabe.gui.editorFrame.getMousePosition().getY());
		} else if (selections.length > 1) {
			return;
		}
	}

	public void refresh(MethodNode mn) {
		LazyListModel<InstructionNode> llm = new LazyListModel<InstructionNode>();
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() != AbstractInsnNode.FRAME)
				llm.addElement(new InstructionNode(mn, ain));
		}
		this.setModel(llm);
		this.repaint();
		if (addressList != null) {
			addressList.repaint();
		}
	}

}
