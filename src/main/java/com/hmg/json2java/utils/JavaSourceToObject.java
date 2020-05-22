package com.hmg.json2java.utils;

import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import sun.misc.Unsafe;

public class JavaSourceToObject {

	private static Map<String, SimpleJavaFileObject> pool = new HashMap<String, SimpleJavaFileObject>();
	private static Map<String, Class> classPool = new HashMap<String, Class>();
	public static ClassLoader dynamicClassLoader = new DynamicClassLoader(classPool);
	private final static String USER_DIR = System.getProperty("user.dir");
	private static Object oLock = new Object();
	static JavaFileManager javaFileManager = null;

	private static void checkDynamicsJar() {
		try {
			File dynamicsJar = new File(USER_DIR + "/dynamics.jar");
			if (!dynamicsJar.exists()) {				
				execShellCommand("echo SLM > empty.txt");				
				execShellCommand("jar -cfv dynamics.jar empty.txt");
			}
		} catch (Exception e) {
			System.err.println("Error while creating dynamics.jar");
			e.printStackTrace();
		}
	}

	private static void execShellCommand(String command) {
		try {
			System.out.println("Running> "+command);
			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
			Process process;
			if (isWindows) {
				process = Runtime.getRuntime().exec(String.format("cmd.exe /c %s", command));
				readProcessOutput(process);
			} else {
				process = Runtime.getRuntime().exec(String.format("sh -c %s", command));
				readProcessOutput(process);
			}
		} catch (Exception e) {
			System.err.println("error while executing shell command {" + command + "} dtl:" + e.getMessage());
		}
	}

	private static void readProcessOutput(Process p) throws IOException {
		InputStream is = p.getInputStream();
		InputStreamReader isReader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isReader);

		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
		is.close();
	}

	@SuppressWarnings("unchecked")
	private static JavaFileManager getFileManager() {
		synchronized (oLock) {
			if (javaFileManager == null) {
				try {
					javaFileManager = new ForwardingJavaFileManager(
							ToolProvider.getSystemJavaCompiler().getStandardFileManager(new DiagnosticListener() {

								@Override
								public void report(Diagnostic diagnostic) {
									System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
											diagnostic.getSource());

								}
							}, Locale.getDefault(), Charset.defaultCharset())) {

						@Override
						public JavaFileObject getJavaFileForOutput(Location location, String className,
								JavaFileObject.Kind kind, FileObject sibling) throws IOException {
							System.out.println(" pool.get(className):" + className);
							return pool.get(className);
						}

					};
				} catch (

				Exception e) {
					System.err.println("Be sure that using a JDK, not JRE ! detail:" + e.getMessage());
					throw e;
				}
			}

			return javaFileManager;
		}
	}

	public static Object createDynamicClass(String source)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			ClassNotFoundException, InstantiationException, IOException {

		String packageName = getStringInBetween(source, "package ", ";");
		String className = getStringInBetween(source, "public class ", " implements");
		String fullClassName = (packageName + "." + className).replace('.', '/');
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		SimpleJavaFileObject simpleJavaFileObject = new SimpleJavaFileObject(URI.create(fullClassName + ".java"),
				SOURCE) {

			@Override
			public CharSequence getCharContent(boolean ignoreEncodingErrors) {
				return source;
			}

			@Override
			public OutputStream openOutputStream() throws IOException {

				return byteArrayOutputStream;
			}
		};

		pool.put((packageName + "." + className), simpleJavaFileObject);

//		// requires adding an interface beforehand 
//		// https://stackoverflow.com/a/21544850/413032 
//        List<String> optionList = new ArrayList<String>();
//        optionList.add("-classpath");
//        optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/DynamicClasses.jar");

		CompilationTask inMemoryCompilationTask = ToolProvider.getSystemJavaCompiler().getTask(null, getFileManager(),
				null, null, null, pool.values());

		List<String> options = Arrays.asList("-d", USER_DIR);
		CompilationTask fileWriterCompilationTask = ToolProvider.getSystemJavaCompiler().getTask(null, null, null,
				options, null, pool.values());

		if (!inMemoryCompilationTask.call()) {
			System.err.println("inMemoryCompilationTask Task Err!");
		}

		if (!fileWriterCompilationTask.call()) {
			System.err.println("fileWriterCompilationTask Task Err!");
		}

		addClassIntoDynamicJarPackage(fullClassName);

		byte[] bytes = byteArrayOutputStream.toByteArray();
		if (bytes.length == 0) {
			System.err.println("bytes zero.");
			return null;
		}

//use the unsafe class to load in the class bytes
		Field f = Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);

		// unsafe.defineAnonymousClass(Class.class, bytes, null);
		Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length, dynamicClassLoader,
				new ProtectionDomain(null, null));

		Object o = aClass.newInstance();
		classPool.put(fullClassName.replace('/', '.'), o.getClass());

// Class thisClass = Class.forName(fullClassName.replace('/', '.'));
		System.out.println(o.getClass().getName() + " class generated.");
		return o;

	}

	private static void addClassIntoDynamicJarPackage(String fullClassName) {
			checkDynamicsJar();
			execShellCommand("jar -ufv dynamics.jar " + fullClassName+".class");
	}

	public static String getStringInBetween(String exp, String before, String after) {
		Pattern ptrnBtw = Pattern.compile(Pattern.quote(before) + "(.+?)" + Pattern.quote(after));
		Matcher matcher = ptrnBtw.matcher(exp);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
}
