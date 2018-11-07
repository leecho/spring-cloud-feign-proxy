package com.github.leecho.spring.feign.sample.service;

import com.github.leecho.spring.feign.sample.model.Demo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author liqiu
 * @create 2018/9/21 13:52
 **/
@Api(tags = "DemoService", description = "Demo Feign Client")
@FeignClient("demo-service")
public interface DemoService {

	/**
	 * create demo
	 *
	 * @param demo
	 * @return
	 */
	@ApiOperation(value = "Create demo")
	@PostMapping(value = "/demo")
	Demo create(@RequestBody @Valid @ApiParam Demo demo);

	/**
	 * update demo
	 *
	 * @param demo
	 * @return
	 */
	@PutMapping(value = "/demo")
	Demo update(@RequestBody @Valid Demo demo);

	/**
	 * delete demo
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/demo/{id}")
	Demo delete(@PathVariable(name = "id") String id);

	/**
	 * list demo
	 *
	 * @param id
	 * @param headerValue test header value
	 * @return
	 */
	@GetMapping(value = "/demo/{id}")
	Demo get(@PathVariable(name = "id") String id, @RequestHeader(name = "header") String headerValue);

	/**
	 * list demos
	 *
	 * @return
	 */
	@GetMapping(value = "/demos")
	List<Demo> list();

	/**
	 * upload file
	 *
	 * @param file
	 * @return
	 */
	@PostMapping(value = "/demo/upload")
	String upload(@RequestPart(name = "file") MultipartFile file);
}
