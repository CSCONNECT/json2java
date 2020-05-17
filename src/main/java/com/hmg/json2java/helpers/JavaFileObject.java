package com.hmg.json2java.helpers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaFileObject extends SimpleJavaFileObject {
	 final ByteArrayOutputStream os = 
	            new ByteArrayOutputStream();
	        JavaFileObject(String name, JavaFileObject.Kind kind) {
	            super(URI.create(
	                "string:///" 
	              + name.replace('.', '/') 
	              + kind.extension), 
	                kind);
	        }
	        byte[] getBytes() {
	            return os.toByteArray();
	        }
	        @Override
	        public OutputStream openOutputStream() {
	            return os;
	        }
}
