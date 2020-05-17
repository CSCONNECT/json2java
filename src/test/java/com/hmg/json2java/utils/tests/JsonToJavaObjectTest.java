package com.hmg.json2java.utils.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hmg.json2java.utils.JsonToJava;
import com.hmg.json2java.utils.ObjectFileWriter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonToJavaObjectTest {

	private final String DYNAMIC_CLASS_NAME="a.b.c.DynaClass";

	@Test
	public void JSON2JAVA()
			throws JsonParseException, JsonMappingException, ClassNotFoundException, IllegalAccessException,
			InstantiationException, NoSuchFieldException, IOException, URISyntaxException, JSONException {
		String json = "{\r\n" + 
				"  \"type\":\"object\",\r\n" + 
				"  \"className\":\""+DYNAMIC_CLASS_NAME+"\",\r\n" + 
				"  \"properties\": {\r\n" +
				"  \"className\":\"a.b.c.NestedClass\",\r\n" + 
				"    \"foo\": {\r\n" +
				"      \"className\":\"a.b.c.NestedClassFoo\",\r\n" + 
				"      \"var1\": \"string\"\r\n" + 
				"    },\r\n" + 
				"    \"bar\": {\r\n" + 
				"      \"className\":\"a.b.c.NestedClassBar\",\r\n" + 
				"      \"var2\": \"integer\"\r\n" + 
				"    },\r\n" + 
				"    \"baz\": {\r\n" + 
				"  	   \"className\":\"a.b.c.NestedClassBaz\",\r\n" + 
				"      \"var3\": \"boolean\"\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}";

		Object o = JsonToJava.jsonToJavaObject(json);

		assertTrue(o.getClass().getName().equals(DYNAMIC_CLASS_NAME));
		

	}

}
