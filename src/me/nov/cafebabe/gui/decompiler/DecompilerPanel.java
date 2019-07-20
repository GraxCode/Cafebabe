package me.nov.cafebabe.gui.decompiler;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.alee.laf.text.WebTextField;

import me.nov.cafebabe.decompiler.CFR;
import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.formatting.Colors;

public class DecompilerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private DecompilerTextArea dp;
	private JLabel label;

	private int searchIndex = -1;
	private String lastSearchText = null;

	public DecompilerPanel(ClassNode cn, MethodNode mn) {
		this.dp = new DecompilerTextArea();
		dp.setText(CFR.decompile(cn, mn));
		this.label = new JLabel("CFR Decompiler 0.145");
		this.setLayout(new BorderLayout(0, 0));
		JPanel lpad = new JPanel();
		lpad.setBorder(new EmptyBorder(1, 5, 0, 1));
		lpad.setLayout(new GridLayout());
		lpad.add(label);
		JPanel rs = new JPanel();
		rs.setLayout(new GridLayout(1, 5));
		for (int i = 0; i < 3; i++)
			rs.add(new JPanel());
		WebTextField search = new WebTextField();
		search.setInputPrompt(Translations.get("Search..."));
		search.addActionListener(l -> {
			try {
				String text = search.getText();
				if (text.isEmpty()) {
					dp.getHighlighter().removeAllHighlights();
					return;
				}
				String searchText = text.toLowerCase();
				if (!Objects.equals(searchText, lastSearchText)) {
					searchIndex = -1;
					lastSearchText = searchText;
				}
				String[] split = dp.getText().split("\\r?\\n");
				System.out.println(searchText);
				int firstIndex = -1;
				boolean first = false;
				Label: {
					for (int i = 0; i < split.length; i++) {
						String line = split[i];
						if (line.toLowerCase().contains(searchText)) {
							if (i > searchIndex) {
								dp.setCaretPosition(dp.getDocument().getDefaultRootElement().getElement(i).getStartOffset());
								searchIndex = i;
								break Label;
							} else if (!first) {
								firstIndex = i;
								first = true;
							}
						}
					}
					if (first) {
						// go back to first line
						dp.setCaretPosition(dp.getDocument().getDefaultRootElement().getElement(firstIndex).getStartOffset());
						searchIndex = firstIndex;
					}
				}
				hightlightText(searchText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		rs.add(search);
		JButton reload = new JButton(Translations.get("Reload"));
		reload.addActionListener(l -> {
			dp.setText(CFR.decompile(cn, mn));
		});
		rs.add(reload);
		lpad.add(rs);
		this.add(lpad, BorderLayout.NORTH);
		JScrollPane scp = new RTextScrollPane(dp);
		scp.getVerticalScrollBar().setUnitIncrement(16);
		this.add(scp, BorderLayout.CENTER);
	}

	private void hightlightText(String searchText) throws BadLocationException {
		Highlighter highlighter = dp.getHighlighter();
		highlighter.removeAllHighlights();
		Document document = dp.getDocument();
		String text = document.getText(0, document.getLength()).toLowerCase();
		int pos = text.indexOf(searchText);
		while (pos >= 0) {
			highlighter.addHighlight(pos, pos + searchText.length(),
					(Highlighter.HighlightPainter) new DefaultHighlighter.DefaultHighlightPainter(Colors.highlightColor));
			pos = text.indexOf(searchText, pos + searchText.length());
		}
	}
}
