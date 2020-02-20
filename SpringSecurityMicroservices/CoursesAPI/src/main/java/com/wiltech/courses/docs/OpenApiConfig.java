package com.wiltech.courses.docs;

import org.springframework.context.annotation.Configuration;

import com.wiltech.core.docs.BaseOpenApiConfig;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class OpenApiConfig extends BaseOpenApiConfig {

	/**
	 * Instantiates a new Base open api config. Passes the packages where the endpoints are.
	 */
	public OpenApiConfig() {
		super("com.wiltech.courses.courses");
	}
}
