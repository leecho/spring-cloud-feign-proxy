package com.github.leecho.spring.feign.sample.service.impl;

import com.github.leecho.spring.cloud.feign.EnableFeignProxies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableFeignProxies(basePackages = "com.github.leecho")
@ComponentScan("com.github.leecho")
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
