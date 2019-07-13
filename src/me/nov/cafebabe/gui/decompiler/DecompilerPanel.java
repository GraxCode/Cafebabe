package me.nov.cafebabe.gui.decompiler;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.decompiler.CFR;

public class DecompilerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private DecompilerTextArea dp;
	private JLabel label;

	public DecompilerPanel(ClassNode cn, MethodNode mn) {
		this.dp = new DecompilerTextArea();
		dp.setText(CFR.decompile(cn, mn));
		this.label = new JLabel("CFR Decompiler");
		this.setLayout(new BorderLayout(0, 0));
		JPanel lpad = new JPanel();
		lpad.setBorder(new EmptyBorder(1, 5, 0, 1));
		lpad.setLayout(new GridLayout());
		lpad.add(label);
		JPanel rs = new JPanel();
		rs.setLayout(new GridLayout(1, 5));
		for (int i = 0; i < 4; i++)
			rs.add(new JPanel());
		//TODO add search text field
		JButton reload = new JButton("Reload");
		reload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dp.setText(CFR.decompile(cn, mn));
			}
		});
		rs.add(reload);
		lpad.add(rs);
		this.add(lpad, BorderLayout.NORTH);
		JScrollPane scp = new RTextScrollPane(dp);
		scp.getVerticalScrollBar().setUnitIncrement(16);
		this.add(scp, BorderLayout.CENTER);
	}

}
