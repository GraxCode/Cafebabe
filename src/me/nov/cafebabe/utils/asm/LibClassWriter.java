package me.nov.cafebabe.utils.asm;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.loading.Loader;
import me.nov.cafebabe.translations.Translations;

public class LibClassWriter extends ClassWriter {

	private Map<String, ClassNode> classes;
	private ParentUtils util;
	private Map<String, String> knownCommons;

	public LibClassWriter(int flags, Map<String, ClassNode> classes, Map<String, ClassNode> libraries,
			Map<String, String> knownCommons) {
		super(flags);
		this.classes = new HashMap<>(classes);
		this.util = new ParentUtils(classes);
		if (libraries != null) {
			classes.putAll(libraries);
		}
		this.knownCommons = knownCommons;
	}

	@Override
	protected String getCommonSuperClass(String type1, String type2) {
		if (knownCommons.containsKey(type1 + ":" + type2)) {
			return knownCommons.get(type1 + ":" + type2);
		}
		if (type1.equals("java/lang/Object") || type2.equals("java/lang/Object")) {
			return "java/lang/Object";
		}
		try {
			ClassNode cn1 = get(type1);
			ClassNode cn2 = get(type2);
			if (cn1 == null || cn2 == null) {
				System.err.println((cn1 == null ? (cn2 == null ? (type1 + " and " + type2) : type1) : type2)
						+ " not found. Check your classpath!");
				try {
					return super.getCommonSuperClass(type1, type2);
				} catch (Exception e) {
				}
				return "java/lang/Object";
			}
			ClassNode common = findCommonParent(cn1, cn2);
			if (common == null) {
				try {
					System.err.println("Couldn't get common superclass of the classes " + type1 + " " + type2);
					return super.getCommonSuperClass(type1, type2);
				} catch (Exception e) {
				}
				return "java/lang/Object";
			}
			return common.name;
		} catch (Exception e) {
			System.err
					.println("Couldn't find out common superclass of the classes " + type1 + " " + type2 + ", asking user!");
			JFrame dummy = new JFrame();
			dummy.setVisible(true);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			dummy.setLocation(screenSize.width / 2, screenSize.height / 2);
			dummy.setAlwaysOnTop(true);
			String superclass = JOptionPane.showInputDialog(dummy, Translations.get("What is the common super class of") + " "
					+ type1 + " " + Translations.get("and") + " " + type2, "java/lang/Object");
			dummy.dispose();
			if (superclass == null || superclass.isEmpty()) {
				return "java/lang/Object";
			}
			knownCommons.put(type1 + ":" + type2, superclass.trim());
			return superclass.trim();
		}
	}

	public ClassNode findCommonParent(ClassNode cn1, ClassNode cn2) {
		if (cn1.name.equals(cn2.name)) {
			return cn1;
		}
		if (util.isAssignableFrom(cn1, cn2)) {
			return cn1;
		}
		if (util.isAssignableFrom(cn2, cn1)) {
			return cn2;
		}
		if (Access.isInterface(cn1.access) || Access.isInterface(cn2.access) || cn1.superName == null
				|| cn1.superName == null) {
			return get("java/lang/Object");
		} else {
			do {
				cn1 = get(cn1.superName);
			} while (cn1.superName != null && !util.isAssignableFrom(cn1, cn2));
			return cn1;
		}
	}

	private ClassNode get(String name) {
		if (classes.containsKey(name)) {
			return classes.get(name);
		}
		try {
			ClassNode cn = Loader.loadLocalClass(name);
			if (cn != null) {
				classes.put(name, cn);
				return cn;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException(name + " not found in your classpath");
	}
}
