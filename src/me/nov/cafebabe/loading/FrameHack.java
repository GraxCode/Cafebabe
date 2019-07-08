package me.nov.cafebabe.loading;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class FrameHack extends ClassWriter {

	public FrameHack() {
		super(COMPUTE_FRAMES);
	}

	private HashMap<Integer, String> ugly = new HashMap<>();

	public static void findCommonParents(Map<String, String> knownCommons, ClassNode cn) {
		// TODO: avoid nonsense labels
		FrameHack cw = new FrameHack();
		cn.accept(cw);
		ClassReader cr = new ClassReader(cw.toByteArray());
		ClassNode dirty = new ClassNode();
		cr.accept(dirty, ClassReader.EXPAND_FRAMES);
		for (int i = 0; i < cn.methods.size(); i++) {
			MethodNode dirtyMethod = dirty.methods.get(i);
			MethodNode realMethod = cn.methods.get(i);
			try {
				for (int j = 0; j < dirtyMethod.instructions.size(); j++) {
					AbstractInsnNode ain = dirtyMethod.instructions.get(j);
					if (ain.getType() == AbstractInsnNode.FRAME) {
						FrameNode dirtyFrame = (FrameNode) ain;
						FrameNode realFrame = findRealFrame(dirtyFrame, realMethod);
						if (realFrame == null)
							continue;
						for (int k = 0; k < dirtyFrame.stack.size(); k++) {
							Object stack = dirtyFrame.stack.get(k);
							if (stack.toString().startsWith("CAFEBABE")) {
								int hash = Integer.parseInt(stack.toString().substring(8));
								String key = cw.ugly.get(hash);
								knownCommons.put(key, realFrame.stack.get(k).toString());
							}
						}

						for (int k = 0; k < dirtyFrame.local.size(); k++) {
							Object localDirty = dirtyFrame.local.get(k);
							if (localDirty.toString().startsWith("CAFEBABE")) {
								int hash = Integer.parseInt(localDirty.toString().substring(8));
								String key = cw.ugly.get(hash);
								if (k < realFrame.local.size()) {
									knownCommons.put(key, realFrame.local.get(k).toString());
								} else {
									knownCommons.put(key, "java/lang/Object");
								}
							}
						}
					}
				}
			} catch (RuntimeException e) {
				// do nothing and continue
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<Integer> debug = Arrays.asList(AbstractInsnNode.LABEL, AbstractInsnNode.LINE);

	private static FrameNode findRealFrame(FrameNode dirtyFrame, MethodNode realMethod) {
		LabelNode dirtyLabel = null;
		AbstractInsnNode ain = dirtyFrame.getPrevious();
		while (ain.getType() != AbstractInsnNode.LABEL) {
			ain = ain.getPrevious();
		}
		dirtyLabel = (LabelNode) ain;
		int dirtyIndex = labelIndex(dirtyLabel);

		for (AbstractInsnNode realAin : realMethod.instructions.toArray()) {
			if (realAin.getType() == AbstractInsnNode.LABEL) {
				if (dirtyIndex == 0) {
					// label found
					AbstractInsnNode next = realAin.getNext();
					while (next != null && debug.contains(next.getType())) {
						next = next.getNext();
					}
					if (next instanceof FrameNode) {
						return (FrameNode) next;
					} else {
						return null;
					}
				}
				dirtyIndex--;
			}
		}
		throw new RuntimeException();
	}

	private static int labelIndex(LabelNode label) {
		int i = 0;
		AbstractInsnNode ain = label.getPrevious();
		while (ain != null) {
			if (ain.getType() == AbstractInsnNode.LABEL) {
				i++;
			}
			ain = ain.getPrevious();
		}
		return i;
	}

	@Override
	protected String getCommonSuperClass(String type1, String type2) {
		if (type1.startsWith("CAFEBABE") || type2.startsWith("CAFEBABE")) {
			return "java/lang/Object";
		}
		if (type1.equals("java/lang/Object") || type2.equals("java/lang/Object")) {
			return "java/lang/Object";
		}
		String key = (type1 + ":" + type2);
		int hash = key.hashCode();
		ugly.put(hash, key);
		return "CAFEBABE" + hash;
	}
}
