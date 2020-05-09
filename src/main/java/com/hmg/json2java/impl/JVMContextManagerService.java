package com.hmg.json2java.impl;

import org.springframework.stereotype.Service;

import com.hmg.json2java.service.IJVMContextManagerService;
import com.hmg.json2java.utils.JsonToJava;

@Service
public class JVMContextManagerService implements IJVMContextManagerService {

	@Override
	public boolean addJSONClassToRuntime(String jsonObject) {
		try {
			Object o = JsonToJava.jsonToJavaObject(jsonObject);
			return o != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
