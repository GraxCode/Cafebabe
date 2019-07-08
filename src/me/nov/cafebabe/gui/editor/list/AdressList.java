package me.nov.cafebabe.gui.editor.list;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;

public class AdressList extends JList<String> {
	private static final long serialVersionUID = 1L;

	public AdressList(InstructionList il) {
		super(new DefaultListModel<String>());
		this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		this.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSelectionInterval(int index0, int index1) {
				super.setSelectionInterval(-1, -1);
			}
		});
		this.setPrototypeCellValue("0000");
		this.setEnabled(false);
		this.setModel(new DefaultListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize() {
				return il.getModel().getSize();
			}

			@Override
			protected void fireIntervalAdded(Object source, int index0, int index1) {
			}

			@Override
			public String getElementAt(int index) {
				String hex = String.valueOf(index);
				return "0000".substring(hex.length()) + hex;
			}
		});
	}
}
