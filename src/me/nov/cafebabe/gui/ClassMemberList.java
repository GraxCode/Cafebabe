package me.nov.cafebabe.gui;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.gui.node.MethodListNode;
import me.nov.cafebabe.gui.smalleditor.MethodEditorPanel;
import me.nov.cafebabe.gui.ui.MethodListCellRenderer;

public class ClassMemberList extends JTree {
	private static final long serialVersionUID = 1L;

	private DefaultTreeModel model;
	private MethodListNode root;

	public ClassMemberList() {
		MethodEditorPanel editor = new MethodEditorPanel(this);
		this.setRootVisible(false);
		this.setShowsRootHandles(false);
		this.setFocusable(false);
		this.setCellRenderer(new MethodListCellRenderer());
		this.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				MethodListNode node = (MethodListNode) getLastSelectedPathComponent();
				if (node != null && node.getMethod() != null) {
					Cafebabe.gui.smallEditorPanel.setViewportView(editor);
					editor.editMethod(node.getMethod());
				}
			}
		});
		this.model = new DefaultTreeModel(root = new MethodListNode(null, null));
		this.setModel(model);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	public void setMethods(ClassNode cn) {
		root.removeAllChildren();
		for (MethodNode mn : cn.methods) {
			root.add(new MethodListNode(cn, mn));
		}
		this.model.reload();
		this.repaint();
	}
}
