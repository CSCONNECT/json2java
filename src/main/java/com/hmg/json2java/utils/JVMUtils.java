package com.hmg.json2java.utils;

import java.lang.reflect.Constructor;

public class JVMUtils {
	
	public static boolean isClassInJVMContext(String fullClassName) {
		Class<?> clazz;
		Object o = null;
		try {
			clazz = Class.forName(fullClassName);
			Constructor<?> ctor = clazz.getConstructor();
			o = ctor.newInstance(); // tries to create a new instance, this will be collected by GC.
			return Boolean.TRUE; // creation is successful so class definition is in JVM environment
		} catch (Exception e) {
			e.printStackTrace();
			if (!(e instanceof ClassNotFoundException))
				System.err.println("This is another exception : " + e.getMessage());
			return Boolean.FALSE; // class is not in environment or there is another issue to get instance
		} finally {
			o = null;
			clazz = null;
		}
	}

}
