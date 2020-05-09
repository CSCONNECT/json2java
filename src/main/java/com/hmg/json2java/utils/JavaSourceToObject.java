package com.hmg.json2java.utils;

import static javax.tools.JavaFileObject.Kind.SOURCE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaFileManager.Location;

import sun.misc.Unsafe;

public class JavaSourceToObject {

	private static Map<String, SimpleJavaFileObject> pool = new HashMap<String, SimpleJavaFileObject>();
	private static Object oLock = new Object();
	static JavaFileManager javaFileManager = null;

	private static JavaFileManager getFileManager() {
		synchronized (oLock) {
			if (javaFileManager == null) {
				javaFileManager = new ForwardingJavaFileManager(
						ToolProvider.getSystemJavaCompiler().getStandardFileManager(new DiagnosticListener() {

							@Override
							public void report(Diagnostic diagnostic) {
								// TODO Auto-generated method stub
								
							}
						}, Locale.getDefault(), Charset.defaultCharset())) {

					@Override
					public JavaFileObject getJavaFileForOutput(Location location, String className,
							JavaFileObject.Kind kind, FileObject sibling) throws IOException {
						System.out.println(" pool.get(className):" + className);
						return pool.get(className);
					}
				};
			}

			return javaFileManager;
		}
	}

	public static Object createDynamicClass(String source) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InstantiationException {

		String packageName = getStringInBetween(source, "package ", ";");
		String className = getStringInBetween(source, "public class ", " {");
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

		ToolProvider.getSystemJavaCompiler().getTask(null, getFileManager(), null, null, null, pool.values()).call();

		byte[] bytes = byteArrayOutputStream.toByteArray();
		if (bytes.length == 0) {
			System.err.println("bytes zero.");
			return null;
		}

//use the unsafe class to load in the class bytes
		Field f = Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);

		unsafe.defineAnonymousClass(Class.class, bytes, null);

		Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length, ClassLoader.getSystemClassLoader(),
				new ProtectionDomain(null, null));

		Object o = aClass.newInstance();

// Class thisClass = Class.forName(fullClassName.replace('/', '.'));
		System.out.println(o.getClass().getName() + " class generated.");

		return o;

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
