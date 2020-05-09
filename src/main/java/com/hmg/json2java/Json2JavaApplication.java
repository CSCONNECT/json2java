package com.hmg.json2java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hmg.json2java.*","com.hmg.json2java.service.*","com.hmg.json2java.impl.*", "com.hmg.json2java.tests.*","com.hmg.json2java.api.*" })
@EnableWebMvc
@Configuration
@EnableCaching
@EnableAutoConfiguration
public class Json2JavaApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(Json2JavaApplication.class, args);
	}
}
