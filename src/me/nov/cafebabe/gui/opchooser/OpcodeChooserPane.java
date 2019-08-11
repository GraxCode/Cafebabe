package me.nov.cafebabe.gui.opchooser;

import java.util.ArrayList;

import javax.swing.JScrollPane;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import com.alee.laf.tabbedpane.WebTabbedPane;

import me.nov.cafebabe.gui.smalleditor.list.OpList;
import me.nov.cafebabe.translations.Translations;

public class OpcodeChooserPane extends WebTabbedPane implements Opcodes {
	private static final long serialVersionUID = 1L;
	private ArrayList<OpList> lists = new ArrayList<>();

	public OpcodeChooserPane(OpcodeChooserDialog chooser, AbstractInsnNode ain) {
		this.setTabPlacement(WebTabbedPane.LEFT);
		this.addTab(Translations.get("Variables"), new JScrollPane(
				addList(new OpList(chooser, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE, ILOAD, LLOAD, FLOAD, DLOAD, ALOAD))));
		this.addTab(Translations.get("Types"),
				new JScrollPane(addList(new OpList(chooser, NEW, CHECKCAST, INSTANCEOF, NEWARRAY, ANEWARRAY, MULTIANEWARRAY))));
		this.addTab(Translations.get("Methods"), new JScrollPane(
				addList(new OpList(chooser, INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE, INVOKEDYNAMIC))));
		this.addTab(Translations.get("Fields"), new JScrollPane(addList(new OpList(chooser, GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD))));
		this.addTab(Translations.get("Jumps"), new JScrollPane(addList(new OpList(chooser, GOTO, IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
				IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, IFNULL, IFNONNULL))));
		this.addTab(Translations.get("End Nodes"),
				new JScrollPane(addList(new OpList(chooser, ATHROW, RETURN, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN))));
		this.addTab(Translations.get("Calculation"),
				new JScrollPane(addList(new OpList(chooser, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL,
						DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR,
						LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR))));
		this.addTab(Translations.get("Comparison"), new JScrollPane(addList(new OpList(chooser, LCMP, FCMPL, FCMPG, DCMPL, DCMPG))));
		this.addTab(Translations.get("Conversion"), new JScrollPane(
				addList(new OpList(chooser, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S))));
		this.addTab("Stack",
				new JScrollPane(addList(new OpList(chooser, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP))));
		this.addTab(Translations.get("Constants"),
				new JScrollPane(
						addList(new OpList(chooser, LDC, BIPUSH, SIPUSH, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2,
								ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1))));
		this.addTab(Translations.get("Arrays"), new JScrollPane(addList(new OpList(chooser, IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD,
				CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE))));
		this.addTab(Translations.get("Other"), new JScrollPane(
				addList(new OpList(chooser, TABLESWITCH, LOOKUPSWITCH, MONITORENTER, MONITOREXIT, IINC, NOP, JSR, RET))));
		this.refresh();
	}

	private OpList addList(OpList opList) {
		lists.add(opList);
		return opList;
	}

	public void refresh() {
		for (int i = 0; i < lists.size(); i++) {
			OpList ol = lists.get(i);
			if(ol.refresh()) {
				setSelectedIndex(i);
			}
		}
	}

}
