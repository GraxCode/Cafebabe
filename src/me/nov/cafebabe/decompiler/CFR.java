package me.nov.cafebabe.decompiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;
import org.benf.cfr.reader.apiunreleased.ClassFileSource2;
import org.benf.cfr.reader.apiunreleased.JarContent;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class CFR {

	public static final HashMap<String, String> options = new HashMap<>();

	public static boolean stringBuilders;
	public static boolean stringSwitches;
	public static boolean tryWith;
	public static boolean lambdas;
	public static boolean finallies;
	public static boolean hideLongStrings;
	public static boolean hideUTF8;
	public static boolean removeSynthetic;
	public static boolean commentMonitors;
	public static boolean topsort;
	public static boolean ignoreExcpetions;
	static {
		options.put("aexagg", "false");
		options.put("allowcorrecting", "true");
		options.put("arrayiter", "true");
		options.put("caseinsensitivefs", "false");
		options.put("clobber", "false");
		options.put("collectioniter", "true");
		options.put("commentmonitors", "false");
		options.put("decodeenumswitch", "true");
		options.put("decodefinally", String.valueOf(finallies));
		options.put("decodelambdas", String.valueOf(lambdas));
		options.put("decodestringswitch", String.valueOf(stringSwitches));
		options.put("dumpclasspath", "false");
		options.put("eclipse", "true");
		options.put("elidescala", "false");
		options.put("forcecondpropagate", "false");
		options.put("forceexceptionprune", "false");
		options.put("forcereturningifs", "false");
		options.put("forcetopsort", String.valueOf(topsort));
		options.put("forcetopsortaggress", String.valueOf(topsort));
		options.put("forloopaggcapture", "false");
		options.put("hidebridgemethods", "true");
		options.put("hidelangimports", "true");
		options.put("hidelongstrings", String.valueOf(hideLongStrings));
		options.put("hideutf", String.valueOf(hideUTF8));
		options.put("ignoreexceptionsalways", String.valueOf(ignoreExcpetions));
		options.put("innerclasses", "true");
		options.put("j14classobj", "false");
		options.put("labelledblocks", "true");
		options.put("lenient", "false");
		options.put("liftconstructorinit", "true");
		options.put("override", "true");
		options.put("pullcodecase", "false");
		options.put("recover", "true");
		options.put("recovertypeclash", "false");
		options.put("recovertypehints", "false");
		options.put("relinkconststring", "true");
		options.put("removebadgenerics", "true");
		options.put("removeboilerplate", "true");
		options.put("removedeadmethods", "true");
		options.put("removeinnerclasssynthetics", String.valueOf(removeSynthetic));
		options.put("rename", "false");
		options.put("renamedupmembers", "false");
		options.put("renameenumidents", "false");
		options.put("renameillegalidents", "false");
		options.put("showinferrable", "false");
		options.put("showversion", "false");
		options.put("silent", "false");
		options.put("stringbuffer", String.valueOf(stringBuilders));
		options.put("stringbuilder", String.valueOf(stringBuilders));
		options.put("sugarasserts", "true");
		options.put("sugarboxing", "true");
		options.put("sugarenums", "true");
		options.put("tidymonitors", "true");
		options.put("commentmonitors", String.valueOf(commentMonitors));
		options.put("tryresources", String.valueOf(tryWith));
		options.put("usenametable", "true");
	}

	private static String decompiled;

	public static String decompile(ClassNode cn, MethodNode mn) {
		try {
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); // ignore frames
			cn.accept(cw);
			byte[] b = cw.toByteArray();
			decompiled = null;
			OutputSinkFactory mySink = new OutputSinkFactory() {
				@Override
				public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
					if (sinkType == SinkType.JAVA && collection.contains(SinkClass.DECOMPILED)) {
						return Arrays.asList(SinkClass.DECOMPILED, SinkClass.STRING);
					} else {
						return Collections.singletonList(SinkClass.STRING);
					}
				}

				Consumer<SinkReturns.Decompiled> dumpDecompiled = d -> {
					decompiled = d.getJava();
					if(mn == null) {
						decompiled = decompiled.substring(31); //remove watermark
					}
				};

				@Override
				public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
					if (sinkType == SinkType.JAVA && sinkClass == SinkClass.DECOMPILED) {
						return x -> dumpDecompiled.accept((SinkReturns.Decompiled) x);
					}
					return ignore -> {
					};
				}
			};
			options.put("analyseas", "CLASS");
			if (mn != null) {
				options.put("methodname", mn.name);
			} else {
				options.remove("methodname");
			}
			ClassFileSource2 cfs2 = new ClassFileSource2() {
				@Override
				public void informAnalysisRelativePathDetail(String a, String b) {
				}

				@Override
				public String getPossiblyRenamedPath(String path) {
					return path;
				}

				@Override
				public Pair<byte[], String> getClassFileContent(String path) throws IOException {
					String name = path.substring(0, path.length() - 6);
					if (name.equals(cn.name)) {
						return Pair.make(b, name);
					}
					ClassNode dummy = new ClassNode();
					dummy.name = name;
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
					dummy.accept(cw);
					return Pair.make(cw.toByteArray(), name); // cfr loads unnecessary classes
				}

				@Override
				public Collection<String> addJar(String arg0) {
					throw new RuntimeException();
				}

				@Override
				public JarContent addJarContent(String arg0) {
					return null;
				}
			};
			CfrDriver cfrDriver = new CfrDriver.Builder().withClassFileSource(cfs2).withOutputSink(mySink)
					.withOptions(options).build();
			cfrDriver.analyse(Arrays.asList(cn.name));
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		return decompiled;
	}
}
