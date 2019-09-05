package me.nov.cafebabe.loading;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import me.nov.cafebabe.gui.ClassTree;
import me.nov.cafebabe.utils.asm.LibClassWriter;

public class Saver {
	public static final byte[] javaMagic = { -54, -2, -70, -66 };

	public static void saveClasses(HashMap<JarEntry, ClassNode> nodes, JarFile inputFile, String outputFile)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		JarOutputStream out = new JarOutputStream(fos);
		Enumeration<JarEntry> inputEntries = inputFile.entries();
		while (inputEntries.hasMoreElements()) {
			try {
				JarEntry entry = inputEntries.nextElement();
				InputStream stream = inputFile.getInputStream(entry);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				int read = 0;
				if (entry.getSize() > 3) {
					byte[] cafebabe = new byte[4];
					stream.read(cafebabe);
					bos.write(cafebabe, 0, 4);
					if (Arrays.equals(bos.toByteArray(), javaMagic)) {
						continue;
					}
				}
				byte[] buff = new byte[1024];

				while ((read = stream.read(buff)) != -1) {
					bos.write(buff, 0, read);
				}
				byte[] data = bos.toByteArray();
				packFile(out, entry, entry.getName(), data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, ClassNode> classes = new HashMap<>();
		for (JarEntry oldEntry : nodes.keySet()) {
			ClassNode cn = nodes.get(oldEntry);
			classes.put(cn.name, cn);
		}
		for (JarEntry oldEntry : nodes.keySet()) {
			packFile(out, oldEntry, nodes.get(oldEntry).name + ".class",
					exportNode(nodes.get(oldEntry), classes, new HashMap<>(), new HashMap<>()));
		}
	}

	public static void packFile(JarOutputStream out, JarEntry oldEntry, String path, byte[] data) throws IOException {
		JarEntry newEntry = new JarEntry(path);
		newEntry.setTime(oldEntry.getTime());
		// newEntry.setMethod(oldEntry.getMethod());
		newEntry.setComment(oldEntry.getComment());
		newEntry.setExtra(oldEntry.getExtra());

		out.putNextEntry(newEntry);
		out.write(data, 0, data.length);
		out.closeEntry();
	}

	public static byte[] exportNode(ClassNode cn, Map<String, ClassNode> classes, Map<String, ClassNode> libraries,
			Map<String, String> knownCommons) {
		ClassWriter cw = ClassTree.useFrameRegeneration
				? new LibClassWriter(ClassWriter.COMPUTE_FRAMES, classes, libraries, knownCommons)
				: new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		byte[] b = cw.toByteArray();
		return b;
	}
}
