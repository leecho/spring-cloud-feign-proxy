package com.github.leecho.spring.feign.sample;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 通用Swagger配置
 *
 * @author LIQIU
 * @date 2018-3-14
 **/
@Configuration
@EnableSwagger2
public class Swagger2AutoConfiguration {


	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.github.leecho"))
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Feign Proxy Demo")
				.description("Feign Clients of Demo")
				.version("2.0")
				.build();
	}
}
