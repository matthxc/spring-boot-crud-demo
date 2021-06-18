package com.luv2code.springboot.cruddemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.rest.EmployeeRestController;
import com.luv2code.springboot.cruddemo.service.EmployeeServiceImpl;

import reactor.core.publisher.Mono;

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
  public void findAllShouldReturnEmployees() {
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
  public void findByIdShouldReturnTheEmployee() {
    Optional<Employee> optionalEmployee = Optional.of(this.employees.get(0));

    Mockito
        .when(employeeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(optionalEmployee);

    webTestClient.get().uri("/api/employees/{employeeId}", 1)
        .header(HttpHeaders.ACCEPT, "application/json")
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
  public void findByIdShouldReturnNotFoundMessage() {
    Optional optional = Optional.empty();

    Mockito
        .when(employeeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(optional);

    webTestClient.get().uri("/api/employees/{employeeId}", 3)
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.message").isEqualTo("Employee not found with ID: 3")
        .jsonPath("$.timeStamp").isNumber();

    Mockito.verify(employeeRepository, times(1)).findById(3);
  }

  @Test
  public void findByIdShouldReturnBadRequest() {
    webTestClient.get().uri("/api/employees/{employeeId}", "aaaa")
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400)
        .jsonPath("$.message").value(message -> assertThat(message).asString().contains("Failed to convert value of type 'java.lang.String'"))
        .jsonPath("$.timeStamp").isNumber();
  }

  @Test
  public void addEmployeeShouldCreateANewEmployee() {
    Employee employee = this.employees.get(0);

    Employee returnedEmployee = new Employee(employee.getFirstName(), employee.getLastName(), employee.getEmail());
    returnedEmployee.setId(6);

    Mockito
        .when(employeeRepository.save(ArgumentMatchers.isA(Employee.class)))
        .thenReturn(returnedEmployee);

    webTestClient.post().uri("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(employee)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.status").isEqualTo(201)
        .jsonPath("$.message").isEqualTo("Employee added successfully")
        .jsonPath("$.data.id").isEqualTo(6)
        .jsonPath("$.data.email").isEqualTo("juan.camargo@example.com")
        .jsonPath("$.data.firstName").isEqualTo("Juan Felipe")
        .jsonPath("$.data.lastName").isEqualTo("Camargo");

    Mockito.verify(employeeRepository, times(1)).save(ArgumentMatchers.isA(Employee.class));
  }

  @Test
  public void updateEmployeeShouldReturnNotFoundWhenIDNotProvided() {
    Employee employee = new Employee();
    employee.setEmail(this.employees.get(0).getEmail());
    employee.setFirstName(this.employees.get(0).getFirstName());
    employee.setLastName(this.employees.get(0).getLastName());

    webTestClient.put().uri("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(employee)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.message").isEqualTo("The Employee ID must be set")
        .jsonPath("$.timeStamp").isNumber();
  }

  @Test
  public void shouldReturnNotFundWhenEmployeeToUpdateNotFound() {
    Optional optional = Optional.empty();

    Mockito
        .when(employeeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(optional);

    webTestClient.put().uri("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(this.employees.get(1))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.message").isEqualTo("Employee not found with ID: 2")
        .jsonPath("$.timeStamp").isNumber();

    Mockito.verify(employeeRepository, times(1)).findById(2);
  }

  @Test
  public void shouldUpdateTheEmployee() {
    Employee updatedEmployee = this.employees.get(1);
    updatedEmployee.setId(1);

    Optional<Employee> optionalEmployee = Optional.of(this.employees.get(0));

    Mockito
        .when(employeeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(optionalEmployee);

    Mockito
        .when(employeeRepository.save(ArgumentMatchers.isA(Employee.class)))
        .thenReturn(updatedEmployee);

    webTestClient.put().uri("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updatedEmployee)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo(200)
        .jsonPath("$.message").isEqualTo("Employee updated successfully")
        .jsonPath("$.data.id").isEqualTo(1)
        .jsonPath("$.data.email").isEqualTo("santiago.camargo@example.com")
        .jsonPath("$.data.firstName").isEqualTo("Santiago")
        .jsonPath("$.data.lastName").isEqualTo("Camargo Bonilla");

    Mockito.verify(employeeRepository, times(1)).findById(1);
    Mockito.verify(employeeRepository, times(1)).save(ArgumentMatchers.isA(Employee.class));
  }

  @Test
  public void shouldDeleteTheEmployee() {
    Optional<Employee> optionalEmployee = Optional.of(this.employees.get(0));

    Mockito
        .when(employeeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(optionalEmployee);

    Mockito
        .doNothing()
        .when(employeeRepository)
        .deleteById(ArgumentMatchers.anyInt());

    webTestClient.delete().uri("/api/employees/{employeeId}", 1)
        .header(HttpHeaders.ACCEPT, "application/json")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo(200)
        .jsonPath("$.message").isEqualTo("Employee with ID: 1 deleted successfully");

    Mockito.verify(employeeRepository, times(1)).findById(1);
    Mockito.verify(employeeRepository, times(1)).deleteById(1);
  }
}
