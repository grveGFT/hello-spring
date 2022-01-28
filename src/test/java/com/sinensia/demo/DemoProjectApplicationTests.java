package com.sinensia.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestParam;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class DemoProjectApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void rootTest(@Autowired TestRestTemplate restTemplate) {
		assertThat(restTemplate.getForObject("/", String.class))
				.isEqualTo("Hola ke ase");
	}

	@Test
	void helloVoidTest(@Autowired TestRestTemplate restTemplate) {
		assertThat(restTemplate.getForObject("/hello", String.class))
				.isEqualTo("Hello World!");
	}

	@Test
	void helloNameTest(@Autowired TestRestTemplate restTemplate) {
//				   @RequestParam(value = "name") String name) {
//		assertThat(restTemplate.getForObject("/"))
//		assertThat(restTemplate.getForObject("/hello", String.class)).isEqualTo("Hello World!");
		assertThat(restTemplate.getForObject("/hello?name=Gaudir", String.class))
				.isEqualTo("Hello Gaudir!");
	}

	@Test
	void canAdd(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/Add?a=1&b=2", String.class))
				.isEqualTo("3");
	}

	@Test
	void canAddZero(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/Add?a=0&b=2", String.class))
				.isEqualTo("2");
	}

	@Test
	void canAddNegative(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/Add?a=1&b=-2", String.class))
				.isEqualTo("-1");
	}

	@Test
	void canAddNULL(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/Add?a=&b=2", String.class))
				.isEqualTo("2");
	}

	@Test
	void helloNames(@Autowired TestRestTemplate restTemplate){
		String[] arr = {"Javier","Rodriguez","Javier%20Arturo"};
		for (String name: arr) {
			assertThat(restTemplate.getForObject("/hello?name="+name, String.class))
					.isEqualTo("Hello "+name+"!");
		}
	}

	@Autowired TestRestTemplate restTemplate;
	@ParameterizedTest
	@ValueSource(strings={"Javier","Javier+Arturo","Rodriguez","Javier%20Arturo"})
	void helloParamNames( String name){
		assertThat(restTemplate.getForObject("/hello?name="+name, String.class))
				.isEqualTo("Hello "+name+"!");
	}


}
