package com.sinensia.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertThat(restTemplate.getForObject("/add?a=1&b=2", String.class))
				.isEqualTo("3");
	}

	@Test
	void canAddZero(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/add?a=0&b=2", String.class))
				.isEqualTo("2");
	}

	@Test
	void canAddNegative(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/add?a=1&b=-2", String.class))
				.isEqualTo("-1");
	}

	@Test
	void canAddNULL(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/add?a=&b=2", String.class))
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

	@Test
	void canAddFraction(@Autowired TestRestTemplate restTemplate){
		assertThat(restTemplate.getForObject("/add?a=1&b=2.5", String.class)).isEqualTo("3.5");
	}

	@Autowired TestRestTemplate restTemplate;
	@ParameterizedTest
	@ValueSource(strings={"Javier","Javier+Arturo","Rodriguez","Javier%20Arturo"})
	void helloParamNames( String name){
		assertThat(restTemplate.getForObject("/hello?name="+name, String.class))
				.isEqualTo("Hello "+name+"!");
	}

	@DisplayName("test multiple input values")
	@ParameterizedTest(name="[{index}] ({arguments}) \"{0}\" -> \"{1}\" ")
	@CsvSource({
			"a, Hello a!",
			"b, Hello b!",
			",  Hello null!",
			"'',   Hello World!",
			" , Hello null!",
			"first+last, Hello first last!",
			"first%20last, Hello first last!"
	})
	void helloParamNamesCsv(String name, String expected){
		assertThat(restTemplate.getForObject("/hello?name="+name, String.class))
				.isEqualTo(expected);
	}


	@Nested
	@DisplayName(value="Application tests")
	class AppTests {

		@Autowired
		private DemoProjectApplication app;


		@DisplayName("multiple additions")
		@ParameterizedTest(name = "[{index}] {0} + {1} = {2}")
		@CsvSource({
				"1,2,3",
				"1,1,2",
				"1.0, 1.0, 2",
				"1, -2, -1",
				"1.5, 2, 3.5",
				"'', 2, 2",
				"null,2,2",
				"hola, 2, ",
				"1.5,1.5, 3"
		})
		void canAddCsvParameterized(String a, String b, String expected) {
			assertThat(restTemplate.getForObject("/add?a=" + a + "&b=" + b, String.class)).isEqualTo(expected);
		}

		@Test
		void canAddExceptionJsonString() {
			assertThat(restTemplate.getForObject("/add?a=string&b=1", String.class).indexOf("Bad request")).isGreaterThan(-1);
		}

		@Test
		void canAddFloat() {
			assertThat(restTemplate.getForObject("/add?a=1.5&b=2", Float.class)).isEqualTo(3.5F);
		}

		@Test
		void canAddFloatException() {
			Exception thrown = assertThrows(RestClientException.class, () -> {
				restTemplate.getForObject("/add?a=hola&b=2", Float.class);
			});
		}

		@Test
		void appCanAddReturnsInteger() {
			assertThat(app.add(1f, 2f)).isEqualTo(3);
		}

		@Test
		void appCanAddReturnsFloat() {
			assertThat(app.add(1f, 2f)).isEqualTo(3f);
		}

		@Test
		void appCanAddNull() {
			Exception thrown = assertThrows(NullPointerException.class, () -> {
				Float ret = (Float) app.add(null, 2f);
			});
			assertTrue(thrown.toString().contains("NullPointerException"));
			//alternatively check thrown.getMessage().contains("");

		}
	}


}
