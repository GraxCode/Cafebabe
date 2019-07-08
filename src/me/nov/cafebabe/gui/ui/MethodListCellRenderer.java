package me.nov.cafebabe.gui.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.gui.node.MethodListNode;
import me.nov.cafebabe.utils.asm.Access;

public class MethodListCellRenderer extends DefaultTreeCellRenderer implements Opcodes {
	private static final long serialVersionUID = 1L;

	public static ImageIcon pri, pro, pub, def, constr; // method access
	public static ImageIcon abs, fin, nat, stat, syn, synth; // general access

	private HashMap<Integer, ImageIcon> methodIcons = new HashMap<>();

	static {
		MethodListCellRenderer.pri = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/method/private.png")));
		MethodListCellRenderer.pro = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/method/protected.png")));
		MethodListCellRenderer.pub = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/resources/method/public.png")));
		MethodListCellRenderer.def = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/method/default.png")));

		MethodListCellRenderer.abs = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/access/abstract.png")));
		MethodListCellRenderer.fin = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/resources/access/final.png")));
		MethodListCellRenderer.nat = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/resources/access/native.png")));
		MethodListCellRenderer.stat = new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(MethodListCellRenderer.class.getResource("/resources/access/static.png")));
		MethodListCellRenderer.syn = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/access/synchronized.png")));
		MethodListCellRenderer.synth = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/access/synthetic.png")));
		MethodListCellRenderer.constr = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(MethodListCellRenderer.class.getResource("/resources/access/constructor.png")));
	}

	public MethodListCellRenderer() {
		methodIcons.put(ACC_PUBLIC, pub);
		methodIcons.put(ACC_PROTECTED, pro);
		methodIcons.put(ACC_PRIVATE, pri);
		methodIcons.put(0, def);
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node instanceof MethodListNode) {
			MethodListNode mln = (MethodListNode) value;
			MethodNode mn = mln.getMethod();
			if (mn != null) {
				ImageIcon icon;
				// use eclipse standard for {...}
				// if (mn.name.equals("<clinit>")) {
				// if (methodIcons.containsKey(ACC_PRIVATE | ACC_STATIC)) {
				// icon = methodIcons.get(ACC_PRIVATE | ACC_STATIC);
				// }
				// icon = generateIcon(ACC_PRIVATE | ACC_STATIC, mn.name);
				// } else
				if (methodIcons.containsKey(mn.access) && !mn.name.equals("<init>")) {
					icon = methodIcons.get(mn.access);
				} else {
					icon = generateIcon(mn.access, mn.name);
				}
				this.setIcon(icon);
				this.setText(node.toString());
			}
		}
		return this;
	}

	@Override
	public Font getFont() {
		return new Font("Arial", Font.PLAIN, 12);
	}

	private ImageIcon generateIcon(int access, String name) {
		ImageIcon template = null;
		if (Access.isPublic(access)) {
			template = pub;
		} else if (Access.isPrivate(access)) {
			template = pri;
		} else if (Access.isProtected(access)) {
			template = pro;
		} else {
			template = def;
		}
		if (name.equals("<init>")) {
			template = combineAccess(template, constr, true);
			// don't save
			return template;
		}
		if (Access.isAbstract(access)) {
			template = combineAccess(template, abs, true);
		} else {
			boolean scndRight = true;
			if (Access.isFinal(access)) {
				template = combineAccess(template, fin, true);
				scndRight = false;
			} else if (Access.isNative(access)) { // do not allow triples
				template = combineAccess(template, nat, true);
				scndRight = false;
			}
			if (Access.isStatic(access)) {
				template = combineAccess(template, stat, scndRight);
			} else if (Access.isSynchronized(access)) {
				template = combineAccess(template, syn, scndRight);
			} else if (Access.isSynthetic(access)) {
				template = combineAccess(template, synth, scndRight);
			}
		}
		// TODO allow more access to be on one picture
		methodIcons.put(access, template);
		return template;
	}

	public ImageIcon combineAccess(ImageIcon icon1, ImageIcon icon2, boolean right) {
		Image img1 = icon1.getImage();
		Image img2 = icon2.getImage();

		int w = icon1.getIconWidth();
		int h = icon1.getIconHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.drawImage(img1, 0, 0, null);
		g2.drawImage(img2, right ? w / 4 : w / -4, h / -4, null);
		g2.dispose();

		return new ImageIcon(image);
	}

}
