package me.nov.cafebabe.utils.ui;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import org.objectweb.asm.tree.AbstractInsnNode;

import me.nov.cafebabe.gui.node.InstructionNode;

public class LazyListModel<E> extends AbstractListModel<E> {
	private static final long serialVersionUID = 1L;

	private ArrayList<E> list;

	public LazyListModel() {
		this.list = new ArrayList<E>();
	}

	public void addElement(E e) {
		list.add(e);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	protected void fireIntervalAdded(Object source, int index0, int index1) {
	}

	@Override
	public E getElementAt(int index) {
		return list.get(index);
	}

	public int indexOf(AbstractInsnNode ain) {
		for (int i = 0; i < list.size(); i++) {
			InstructionNode ie = (InstructionNode) list.get(i);
			if (ie.ain.equals(ain)) {
				return i;
			}
		}
		return -1;
	}
}
