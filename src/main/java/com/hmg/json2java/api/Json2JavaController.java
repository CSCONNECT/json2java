package com.hmg.json2java.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hmg.json2java.service.IJVMContextManagerService;

@RestController
@RequestMapping("/json2java")
public class Json2JavaController {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	IJVMContextManagerService jvmCtxManager;
	
	@RequestMapping(value = "/json", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Boolean> addRuleIntoContext(
			@RequestBody String requestedJsonClass)
	{
		System.out.println("/json2java generating...");
		LOG.debug("Generating dynamic java into JVM: {}.");
		return new ResponseEntity<Boolean>(jvmCtxManager.addJSONClassToRuntime(requestedJsonClass), HttpStatus.OK);
	}
}
