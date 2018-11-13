## Introduction
This project can automatically expose FeignClients to Rest without MVC controller.
Use CGLIB dynamic proxy service interface to RestController.

## Feature
- Auto export feign without MVC controller
- Can specify the package path for scanning.
- Support Swagger
  
### Get Started

### Create Interface as Feign Client
~~~
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

~~~

### Create Implementor

~~~
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

~~~

### Enable Feign Proxy for Application
~~~
@EnableFeignProxies(basePackages = "com.github.leecho")
@ComponentScan("com.github.leecho")
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
~~~

### Run Test Case 
~~~
mvn test
~~~

### Console Output

~~~
2018-11-13 18:42:12.027  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demo/{id}],methods=[GET]}" onto public final com.github.leecho.spring.feign.sample.model.Demo com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.get(java.lang.String,java.lang.String)
2018-11-13 18:42:12.027  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demo],methods=[PUT]}" onto public final com.github.leecho.spring.feign.sample.model.Demo com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.update(com.github.leecho.spring.feign.sample.model.Demo)
2018-11-13 18:42:12.028  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demo/{id}],methods=[DELETE]}" onto public final com.github.leecho.spring.feign.sample.model.Demo com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.delete(java.lang.String)
2018-11-13 18:42:12.028  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demo],methods=[POST]}" onto public final com.github.leecho.spring.feign.sample.model.Demo com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.create(com.github.leecho.spring.feign.sample.model.Demo)
2018-11-13 18:42:12.028  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demos],methods=[GET]}" onto public final java.util.List com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.list()
2018-11-13 18:42:12.029  INFO 2124 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/demo/upload],methods=[POST]}" onto public final java.lang.String com.github.leecho.spring.feign.sample.service.DemoService$$EnhancerByCGLIB$$bf7785b6.upload(org.springframework.web.multipart.MultipartFile)
~~~
  
## Issues
- @PathVariable @HeaderValue @CookieValue must use attr "name" for special
- The real implementor of service interface must use @Primary to avoid bean conflicts