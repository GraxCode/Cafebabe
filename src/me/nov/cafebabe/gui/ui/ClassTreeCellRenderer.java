package me.nov.cafebabe.gui.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.gui.node.SortedTreeClassNode;
import me.nov.cafebabe.utils.asm.Access;
import me.nov.cafebabe.utils.ui.Images;

public class ClassTreeCellRenderer extends DefaultTreeCellRenderer implements Opcodes {
	private static final long serialVersionUID = 1L;

	private ImageIcon pack, clazz, enu, itf;

	public ClassTreeCellRenderer() {
		this.pack = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/tree/package.png")));
		this.clazz = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/classtype/class.png")));
		this.enu = Images.combine(clazz, new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/classtype/enum.png"))));
		this.itf = Images.combine(clazz, new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/classtype/interface.png"))));
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node instanceof SortedTreeClassNode) {
			SortedTreeClassNode stn = (SortedTreeClassNode) node;
			ClassNode cn = stn.getClazz();
			if (cn != null) {
				if (Access.isInterface(cn.access)) {
					this.setIcon(this.itf);
				} else if (Access.isEnum(cn.access)) {
					this.setIcon(this.enu);
				} else {
					this.setIcon(this.clazz);
				}
			} else {
				this.setIcon(this.pack);
			}
		}
		return this;
	}

	@Override
	public Font getFont() {
		return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	}
}
