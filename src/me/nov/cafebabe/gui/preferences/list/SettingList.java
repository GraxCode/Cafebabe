package me.nov.cafebabe.gui.preferences.list;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ListSelectionModel;

import com.alee.extended.list.CheckBoxCellData;
import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;

import me.nov.cafebabe.gui.node.SettingNode;
import me.nov.cafebabe.setting.Setting;

public class SettingList extends WebCheckBoxList {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public SettingList(Setting... settings) {
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		CheckBoxListModel clm = new CheckBoxListModel();
		for (Setting setting : settings) {
			clm.addCheckBoxElement(new SettingNode(setting), setting.get());
		}
		this.setModel(clm);
		this.repaint();
		for (MouseListener ml : this.getMouseListeners())
			this.removeMouseListener(ml);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					CheckBoxCellData checkbox = (CheckBoxCellData) getModel().getElementAt(index);
					SettingNode sn = (SettingNode) checkbox.getUserObject();
					checkbox.invertSelection();
					repaint();
					sn.setUserSelected(checkbox.isSelected());
				}
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}
}
