package me.nov.cafebabe.gui.node;

import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.nov.cafebabe.translations.Translations;
import me.nov.cafebabe.utils.asm.Descriptors;
import me.nov.cafebabe.utils.formatting.EscapedString;

public class MethodListNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	private MethodNode mn;
	private ClassNode cn;

	private String text;

	public MethodListNode(ClassNode cn, MethodNode mn) {
		super();
		this.cn = cn;
		this.mn = mn;
		if (mn != null) {
			initText();
		}
	}

	private void initText() {
		this.text = "<html>";
		// unnecessary because the icon shows the access
		// this.text += Descriptors.getDisplayAccess(mn.access);
		String[] descSplit = mn.desc.split("\\)");
		switch (mn.name) {
		case "<clinit>":
			this.text += "<font color=\"#757575\"><i>" + Translations.get("class initializer method") + "</i></font>";
			break;
		case "<init>":
			this.text += " <font color=\"#757575\"><i>";
			this.text += new EscapedString(Descriptors.lastSlash(cn.name)).getEscapedText();
			this.text += "</i></font>(";
			this.text += Descriptors.getDisplayType(descSplit[0].substring(1));
			this.text += ")";
			break;
		default:
			this.text += Descriptors.getDisplayType(descSplit[1]);
			this.text += " ";
			this.text += new EscapedString(mn.name).getEscapedText();
			this.text += "(";
			this.text += Descriptors.getDisplayType(descSplit[0].substring(1));
			this.text += ")";
		}
	}

	public ClassNode getClazz() {
		return cn;
	}

	public void setClazz(ClassNode cn) {
		this.cn = cn;
	}

	public MethodNode getMethod() {
		return mn;
	}

	public void setMethod(MethodNode mn) {
		this.mn = mn;
	}

	@Override
	public String toString() {
		return text;
	}

}
