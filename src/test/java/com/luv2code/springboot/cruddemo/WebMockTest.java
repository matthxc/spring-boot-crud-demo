package com.luv2code.springboot.cruddemo;

import static org.mockito.Mockito.times;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.rest.EmployeeRestController;
import com.luv2code.springboot.cruddemo.service.EmployeeServiceImpl;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeRestController.class)
@Import(EmployeeServiceImpl.class)
public class WebMockTest {
  @MockBean
  EmployeeRepository employeeRepository;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void findAllShouldReturnEmployees() throws Exception {
    Employee employee = new Employee();
    employee.setId(1);
    employee.setEmail("juan9505@gmail.com");
    employee.setFirstName("Juan Felipe");
    employee.setLastName("Camargo");

    List<Employee> employees = new ArrayList<>();
    employees.add(employee);

    Mockito
        .when(employeeRepository.findAll())
        .thenReturn(employees);

    webTestClient.get().uri("/api/employees")
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo(200)
        .jsonPath("$.message").isEmpty()
        .jsonPath("$.data[0].id").isEqualTo(1)
        .jsonPath("$.data[0].email").isEqualTo("juan9505@gmail.com")
        .jsonPath("$.data[0].firstName").isEqualTo("Juan Felipe")
        .jsonPath("$.data[0].lastName").isEqualTo("Camargo");

    Mockito.verify(employeeRepository, times(1)).findAll();
  }
}
