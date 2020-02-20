package com.wiltech.core.docs;

import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Open API swagger configuration.
 */
public class BaseOpenApiConfig {

	private final String basePackage;

	/**
	 * Instantiates a new Base open api config.
	 * @param basePackage the base package
	 */
	public BaseOpenApiConfig(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * Api docket.
	 * @return the docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage(basePackage))
			.build()
			.apiInfo(metaData());
	}

	private ApiInfo metaData() {
		return new ApiInfoBuilder()
			.title("Spring Security and Spring Boot Microservices")
			.description("Security in depth")
			.version("1.0")
			.contact(new Contact("Wiltech.com", "http://wiltech.com", "william.Ferraciolli@wiltech.com"))
			.license("Free to share")
			.licenseUrl("http://wiltech.com")
			.build();
	}
}
