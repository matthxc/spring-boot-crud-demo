package com.luv2code.springboot.cruddemo;

import static org.mockito.Mockito.times;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

  private List<Employee> employees;

  @BeforeEach
  public void setupTests() {
    Employee employee1 = new Employee();
    employee1.setId(1);
    employee1.setEmail("juan.camargo@example.com");
    employee1.setFirstName("Juan Felipe");
    employee1.setLastName("Camargo");

    Employee employee2 = new Employee();
    employee2.setId(2);
    employee2.setEmail("santiago.camargo@example.com");
    employee2.setFirstName("Santiago");
    employee2.setLastName("Camargo Bonilla");

    this.employees = new ArrayList<>();
    this.employees.add(employee1);
    this.employees.add(employee2);
  }

  @Test
  public void findAllShouldReturnEmployees() throws Exception {
    Mockito
        .when(employeeRepository.findAll())
        .thenReturn(this.employees);

    webTestClient.get().uri("/api/employees")
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.data.length()").isEqualTo(2)
        .jsonPath("$.status").isEqualTo(200)
        .jsonPath("$.message").isEmpty()
        .jsonPath("$.data[0].id").isEqualTo(1)
        .jsonPath("$.data[0].email").isEqualTo("juan.camargo@example.com")
        .jsonPath("$.data[0].firstName").isEqualTo("Juan Felipe")
        .jsonPath("$.data[0].lastName").isEqualTo("Camargo");

    Mockito.verify(employeeRepository, times(1)).findAll();
  }

  @Test
  public void findByIdShouldReturnTheEmployee() throws Exception {
    Optional<Employee> optionalEmployee = Optional.of(this.employees.get(0));

    Mockito
        .when(employeeRepository.findById(1))
        .thenReturn(optionalEmployee);

    webTestClient.get().uri("/api/employees/{employeeId}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo(200)
        .jsonPath("$.message").isEmpty()
        .jsonPath("$.data.id").isEqualTo(1)
        .jsonPath("$.data.email").isEqualTo("juan.camargo@example.com")
        .jsonPath("$.data.firstName").isEqualTo("Juan Felipe")
        .jsonPath("$.data.lastName").isEqualTo("Camargo");

    Mockito.verify(employeeRepository, times(1)).findById(1);
  }

  @Test
  public void findByIdShouldReturnNotFoundMessage() throws Exception {
    Optional optional = Optional.empty();

    Mockito
        .when(employeeRepository.findById(3))
        .thenReturn(optional);

    webTestClient.get().uri("/api/employees/{employeeId}", 3)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.message").isEqualTo("Employee not found with ID: 3")
        .jsonPath("$.timeStamp").isNumber();

    Mockito.verify(employeeRepository, times(1)).findById(3);
  }
}
