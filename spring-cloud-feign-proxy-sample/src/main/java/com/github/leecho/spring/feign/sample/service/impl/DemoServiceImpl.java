package com.github.leecho.spring.feign.sample.service.impl;

import com.github.leecho.spring.feign.sample.model.Demo;
import com.github.leecho.spring.feign.sample.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liqiu
 * @create 2018/9/21 14:00
 **/
@Slf4j
@Primary
@Service
public class DemoServiceImpl implements DemoService {

	@Override
	public Demo create(Demo demo) {
		log.info("Create executed : " + demo);
		return demo;
	}

	@Override
	public Demo update(Demo demo) {
		log.info("Update execute :" + demo);
		return demo;
	}

	@Override
	public Demo delete(String id) {
		log.info("Delete execute : " + id);
		return Demo.builder().name("demo-" + id).data("data-" + id).build();
	}

	@Override
	public Demo get(String id, String header) {
		Demo demo = Demo.builder()
				.name(header)
				.data(header).build();
		System.out.println("Get execute : " + id + "," + header);
		return demo;
	}

	@Override
	public List<Demo> list() {
		System.out.println("List execute");
		List<Demo> demos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Demo demo = Demo.builder()
					.name("demo-" + i)
					.data("data" + i).build();
			demos.add(demo);
		}
		return demos;
	}

	@Override
	public String upload(MultipartFile file) {
		return file.getOriginalFilename();
	}
}
