package com.hmg.json2java.utils;

import java.util.ArrayList;

public class DynamicJavaSourceMap {
	private String javaSrc;
	
	ArrayList<DynamicJavaSourceMap> dependentClasses = new ArrayList<DynamicJavaSourceMap>();

	public String getJavaSrc() {
		return javaSrc;
	}

	public void setJavaSrc(String javaSrc) {
		this.javaSrc = javaSrc;
	}
}
