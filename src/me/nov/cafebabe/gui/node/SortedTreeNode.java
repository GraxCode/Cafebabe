package me.nov.cafebabe.gui.node;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.utils.EscapedString;

public class SortedTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	private ClassNode cn;
	private EscapedString text;

	public SortedTreeNode(ClassNode cn) {
		this.cn = cn;
		setClassName();
	}

	public SortedTreeNode(String path) {
		this.cn = null;
		this.text = new EscapedString(path);
	}

	private void setClassName() {
		if (cn != null) {
			String[] split = cn.name.split("/");
			this.text = new EscapedString(split[split.length - 1]);
		}
	}

	public ClassNode getClazz() {
		return cn;
	}

	public void setClazz(ClassNode c) {
		this.cn = c;
	}

	@SuppressWarnings("unchecked")
	public void sort() {
		if (children != null)
			Collections.sort(children, comparator());
	}

	private Comparator<SortedTreeNode> comparator() {
		return new Comparator<SortedTreeNode>() {
			@Override
			public int compare(SortedTreeNode node1, SortedTreeNode node2) {
				boolean leaf1 = node1.cn != null;
				boolean leaf2 = node2.cn != null;

				if (leaf1 && !leaf2) {
					return 1;
				}
				if (!leaf1 && leaf2) {
					return -1;
				}
				return node1.getText().compareTo(node2.getText());
			}
		};
	}

	public String getText() {
		return text.getText();
	}

	@Override
	public String toString() {
		return text.getEscapedText();
	}
}