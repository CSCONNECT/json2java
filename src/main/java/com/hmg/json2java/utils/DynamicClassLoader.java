package com.hmg.json2java.utils;

import java.security.ProtectionDomain;
import java.util.Map;

import sun.misc.Unsafe;

public class DynamicClassLoader extends ClassLoader {

	private Map<String, Class> classPool;
	public DynamicClassLoader(Map<String, Class> classPool) {
		this.classPool=classPool;
	}

    @Override
    public Class findClass(String name) throws ClassNotFoundException {

        @SuppressWarnings("rawtypes")
		Class clazz=this.classPool.get(name);
        if(clazz!=null) {
        	return clazz;
        }else {
        	return super.findClass(name);
        }
        
    }
}
