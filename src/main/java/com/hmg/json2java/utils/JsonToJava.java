package com.hmg.json2java.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToJava {

	public static Object jsonToJavaObject(String json) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, URISyntaxException{
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(json, Map.class);

		DynamicJavaSourceMap javaSrcMap = readJsontoJava(map, "");
		
		return instantiateDynamicClassesMap(javaSrcMap);
	}
	
	public static Object instantiateDynamicClassesMap(DynamicJavaSourceMap javaSrcMap) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, URISyntaxException, SecurityException, IllegalArgumentException, IOException {
		Object result=null;
		if (javaSrcMap.dependentClasses.size() > 0) {
			for (DynamicJavaSourceMap dynamicSource : javaSrcMap.dependentClasses) {
				instantiateDynamicClassesMap(dynamicSource);
			}
			result=JavaSourceToObject.createDynamicClass(javaSrcMap.getJavaSrc());
		}else {
			result=JavaSourceToObject.createDynamicClass(javaSrcMap.getJavaSrc());
		}		
		return result;
	}

	public static DynamicJavaSourceMap readJsontoJava(java.util.Map<String, Object> map, String tab) {
		DynamicJavaSourceMap result = new DynamicJavaSourceMap();
		StringBuilder source = new StringBuilder();
		String classFullName = map.get("className").toString();
		String classShortName = getShortClassNameFromFullName(classFullName);
		String packageName = getPackageNameFromFullName(classFullName);
		source.append("package " + packageName + ";\r\r\n");
		source.append("import java.io.Serializable;\r\n");
		source.append("\r\n");
		source.append("public class " + classShortName + " implements Serializable  {\r\n");
		source.append("\r\n");
		source.append("private static final long serialVersionUID = 1L;\r\n");
		source.append("\r\n");
		ArrayList<DynamicJavaSourceMap> dependentClasses = new ArrayList<DynamicJavaSourceMap>();
		
		//Define Fields
		for (String key : map.keySet()) {
			Object value = map.get(key);
			String className = value.getClass().getName();
			if (className.equals(String.class.getName())) {
				source.append(tab + "private String " + key + ";\r\n");
			} else if (className.contentEquals(LinkedHashMap.class.getName())) {
				String fName = ((LinkedHashMap) value).get("className").toString();
				String sName = getShortClassNameFromFullName(fName);
				sName = fC(sName);
				source.append(tab + "private " + fName + " " + key + ";\r\n");
				dependentClasses.add(readJsontoJava((LinkedHashMap) value, ""));
			} else if (className.contentEquals(ArrayList.class.getName())) {
				source.append("java.util.ArrayList<java.lang.Object> " + key
						+ " = new java.util.ArrayList<java.lang.Object> ();\r\n");
			} else if (className.contentEquals(java.lang.Integer.class.getName())) {
				source.append("java.lang.Integer " + key+ ";\r\n");
			}  else if (className.contentEquals(java.lang.Long.class.getName())) {
				source.append("java.lang.Long " + key+ ";\r\n");
			}  else if (className.contentEquals(java.lang.Double.class.getName())) {
				source.append("java.lang.Double " + key+ ";\r\n");
			} else if (className.contentEquals(java.lang.Boolean.class.getName())) {
				source.append("java.lang.Boolean " + key+ ";\r\n");
			} 
			
			
						
			else {
				System.err.println("!!! Unrecognized Class Type:" + className);
			}
		}
		source.append("\r\n");
		
		//Define Getters and Setters
		for (String key : map.keySet()) {
			Object value = map.get(key);
			String className = value.getClass().getName();

			if (className.contentEquals(LinkedHashMap.class.getName())) {
				String fName = ((LinkedHashMap) value).get("className").toString();
				source.append(
						" public " + fName + " get" + fC(key) + "() {\r\n" + "  return " + key + ";\r\n" + " }\r\n");
				source.append(" public void set" + fC(key) + "(" + fName + " " + key + ") {\r\n" + "  this." + key
						+ " = " + key + ";\r\n" + " }\r\n\r\n");
			} else {
				source.append(" public " + className + " get" + fC(key) + "() {\r\n" + "  return " + key + ";\r\n"
						+ " }\r\n");
				source.append(" public void set" + fC(key) + "(" + className + " " + key + ") {\r\n" + "  this." + key
						+ " = " + key + ";\r\n" + " }\r\n\r\n");
			}
		}
		source.append(tab + "}\r\n");

		result.setJavaSrc(source.toString());
		result.dependentClasses = dependentClasses;
		return result;
	}

	public static String getShortClassNameFromFullName(String fullName) {
		String classShortName = fullName.substring(fullName.lastIndexOf(".") + 1);
		return classShortName;
	}

	public static String getPackageNameFromFullName(String fullName) {
		String packageName = fullName.substring(0, fullName.lastIndexOf("."));
		return packageName;
	}

	/***
	 * First LetterCapital
	 * @param exp
	 * @return
	 */
	public static String fC(String exp) {
		if (exp != null && exp.length() > 0) {
			return exp.substring(0, 1).toUpperCase() + exp.substring(1, exp.length());
		}
		return exp;
	}
}
