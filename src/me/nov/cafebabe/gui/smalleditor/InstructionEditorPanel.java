package me.nov.cafebabe.gui.smalleditor;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.alee.extended.panel.WebAccordion;
import com.alee.extended.panel.WebAccordionStyle;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;

import me.nov.cafebabe.gui.editor.list.InstructionList;
import me.nov.cafebabe.gui.smalleditor.list.OpList;

public class InstructionEditorPanel extends JPanel implements Opcodes {
	private static final long serialVersionUID = 1L;
	
	private static WebAccordion accordion = null;
	private static ArrayList<OpList> opLists = new ArrayList<>();

	public InstructionEditorPanel(InstructionList instructionList, MethodNode mn, AbstractInsnNode ain) {
		this.setLayout(new BorderLayout());
		this.setFocusable(false);
		WebTabbedPane tabbedPane = new WebTabbedPane();
		tabbedPane.setTabbedPaneStyle(TabbedPaneStyle.attached);
		tabbedPane.setTabPlacement(WebTabbedPane.TOP);
		tabbedPane.addTab("Opcode", getOpcodeChooser(ain));
		this.add(tabbedPane, BorderLayout.CENTER);
	}

	private WebAccordion getOpcodeChooser(AbstractInsnNode ain) {
		if (accordion == null) {
			//ugly but the only way to prevent lag every time
			WebAccordion accordion = new WebAccordion(WebAccordionStyle.accordionStyle);
			accordion.setFocusable(false);
			accordion.setMultiplySelectionAllowed(true);
			addOpList(accordion, "Variable",
					new OpList(ISTORE, LSTORE, FSTORE, DSTORE, ASTORE, ILOAD, LLOAD, FLOAD, DLOAD, ALOAD));
			addOpList(accordion, "Type", new OpList(NEW, CHECKCAST, INSTANCEOF, NEWARRAY, ANEWARRAY, MULTIANEWARRAY));
			addOpList(accordion, "Method",
					new OpList(INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE, INVOKEDYNAMIC));
			addOpList(accordion, "Field", new OpList(GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD));
			addOpList(accordion, "Jump", new OpList(GOTO, IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
					IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, IFNULL, IFNONNULL));

			addOpList(accordion, "End Nodes", new OpList(ATHROW, RETURN, IRETURN, LRETURN, FRETURN, DRETURN, ARETURN));
			addOpList(accordion, "Calculation & Comparison",
					new OpList(IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV,
							IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR,
							LOR, IXOR, LXOR, LCMP, FCMPL, FCMPG, DCMPL, DCMPG));
			addOpList(accordion, "Conversion",
					new OpList(I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S));
			addOpList(accordion, "Stack", new OpList(POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP));
			addOpList(accordion, "Constants",
					new OpList(LDC, BIPUSH, SIPUSH, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4,
							ICONST_5, LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1));
			addOpList(accordion, "Arrays", new OpList(IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE,
					LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE));
			addOpList(accordion, "Other",
					new OpList(TABLESWITCH, LOOKUPSWITCH, MONITORENTER, MONITOREXIT, IINC, NOP, JSR, RET));
			for (WebCollapsiblePane wcp : accordion.getPanes()) {
				wcp.collapse();
			}
			InstructionEditorPanel.accordion = accordion;
		}
		for (OpList ol : opLists) {
			ol.setNode(ain);
		}
		return accordion;
	}

	private void addOpList(WebAccordion acc, String title, OpList opList) {
		opLists.add(opList);
		acc.addPane(title, new JScrollPane(opList));
	}

}
