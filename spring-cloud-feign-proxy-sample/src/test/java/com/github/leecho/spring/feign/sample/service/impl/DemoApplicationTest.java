package com.github.leecho.spring.feign.sample.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leecho.spring.feign.sample.model.Demo;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

import static org.junit.Assert.*;

/**
 * @author LIQIU
 * created on 2018-11-6
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DemoApplicationTest {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void create() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/demo")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(new ObjectMapper().writeValueAsString(Demo.builder().name("create").data("data").build()))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void update() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/demo")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(new ObjectMapper().writeValueAsString(Demo.builder().name("update").data("data").build()))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void delete() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/demo/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void get() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/demo/{id}", "1")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.header("header", "header-value")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void list() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/demos")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void upload() throws Exception {
		mvc.perform(MockMvcRequestBuilders.multipart("/demo/upload").file(new MockMultipartFile("file", "test.txt", ",multipart/form-data", "upload test".getBytes()))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());

	}
}