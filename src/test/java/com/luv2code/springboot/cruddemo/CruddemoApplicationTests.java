package com.luv2code.springboot.cruddemo;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.luv2code.springboot.cruddemo.rest.EmployeeRestController;

@SpringBootTest
class CruddemoApplicationTests {

	@Autowired
	private EmployeeRestController employeeRestController;

	@Test
	void contextLoads() throws Exception {
		assertThat(employeeRestController).isNotNull();
	}

}
