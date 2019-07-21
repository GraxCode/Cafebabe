package me.nov.cafebabe.gui.translations;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.table.WebTable;

import me.nov.cafebabe.Cafebabe;
import me.nov.cafebabe.translations.Translations;

public class TranslationEditor extends WebDialog {
	private static final long serialVersionUID = 1L;

	public TranslationEditor() {
		super(Cafebabe.gui, true);
		this.setRound(5);
		this.setShadeWidth(20);
		this.setShowResizeCorner(false);
		this.initBounds();
		this.setTitle(Translations.get("Translation editor"));
		this.setIconImage(Cafebabe.gui.getIconImage());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		WebTable table = new WebTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column > 0;
			}
		};
		DefaultTableModel model = new DefaultTableModel(new Object[] { "Hash code", "Translation" }, 0);
		for (int hashCode : Translations.translations.keySet()) {
			String translation = Translations.translations.get(hashCode);
			model.addRow(new Object[] { hashCode, translation });
		}
		table.setColumnSelectionAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setModel(model);
		model.addTableModelListener(l -> {
			for (int row = 0; row < model.getRowCount(); row++)
				Translations.translations.put((Integer) model.getValueAt(row, 0), (String) model.getValueAt(row, 1));
			Translations.saveTranslations();
		});
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pane.add(new WebScrollPane(table), BorderLayout.CENTER);
		pane.add(
				new JLabel(
						"<html>Please open an issue on the official github-repo and send in your tranlations!<br>Translations are stored in <u>%userprofile%/.cafebabe/translations/</u><br>Make sure you don't miss out any translations.<br>Translations are added to the table when they were shown at least once!"),
				BorderLayout.NORTH);
		this.setContentPane(pane);
		setLocationRelativeTo(getParent());
		this.setVisible(true);
	}

	private void initBounds() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.25);
		int height = (int) (screenSize.height * 0.5);
		setBounds(screenSize.width / 2 - width / 2, screenSize.height / 2 - height / 2, width, height);
	}
}
