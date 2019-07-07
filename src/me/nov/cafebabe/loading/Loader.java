package me.nov.cafebabe.loading;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class Loader {
	public static final byte[] javaMagic = { -54, -2, -70, -66 };

	public static HashMap<JarEntry, ClassNode> loadClasses(JarFile jf) {
		HashMap<JarEntry, ClassNode> classes = new HashMap<>();
		Enumeration<JarEntry> entries = jf.entries();

		while (entries.hasMoreElements()) {
			try {
				JarEntry entry = entries.nextElement();
				if (entry.getSize() < 3) {
					continue;
				}
				InputStream stream = jf.getInputStream(entry);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				int read = 0;
				byte[] cafebabe = new byte[4];
				stream.read(cafebabe);
				bos.write(cafebabe, 0, 4);
				if (Arrays.equals(bos.toByteArray(), javaMagic)) {
					byte[] buff = new byte[1024];

					while ((read = stream.read(buff)) != -1) {
						bos.write(buff, 0, read);
					}
					byte[] data = bos.toByteArray();
					classes.put(entry, convertToASM(data));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return classes;
	}

	public static ClassNode convertToASM(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		return cn;
	}

	public static ClassNode loadLocalClass(String type) throws IOException {
		return convertToASM(loadFromClasspath(type));
	}

	public static byte[] loadFromClasspath(String type) throws IOException {
		if (type == null) {
			return null;
		}
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(type + ".class");
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n;
		while ((n = is.read(buffer)) > 0) {
			baos.write(buffer, 0, n);
		}
		return baos.toByteArray();
	}
}
