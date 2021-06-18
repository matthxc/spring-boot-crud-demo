package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {
    Logger logger = LoggerFactory.getLogger(EmployeeRestController.class);

    private EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService) {
        this.employeeService = theEmployeeService;
    }

    @GetMapping("/employees")
    public Mono<ResponseEntity> findAll() {
        return this.employeeService.findAll().collectList().map(employees -> new ResponseEntity<>(new DataResponse<>(HttpStatus.OK.value(), employees), HttpStatus.OK));
    }

    @GetMapping("/employees/{employeeId}")
    public Mono<ResponseEntity> findById(@PathVariable int employeeId) {
        return this.employeeService.findById(employeeId).map(employee -> new ResponseEntity(new DataResponse<>(HttpStatus.OK.value(), employee), HttpStatus.OK));
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity> addEmployee(@RequestBody Employee theEmployee) {
        // Set it to 0 to insert a new record
        theEmployee.setId(0);

        return this.employeeService.save(theEmployee).map(employee -> new ResponseEntity<>(new DataResponse<>(HttpStatus.CREATED.value(), "Employee added successfully", employee), HttpStatus.CREATED));
    }

    @PutMapping("/employees")
    public Mono<ResponseEntity> updateEmployee(@RequestBody Employee theEmployee) {
        return this.checkEmployeeExistence(theEmployee.getId())
            .flatMap(employee -> this.employeeService.save(theEmployee))
            .map(employee ->
            new ResponseEntity<>(
                new DataResponse<>(HttpStatus.OK.value(),
                    "Employee updated successfully", employee),
                HttpStatus.OK
            )
        );
    }

    @DeleteMapping("/employees/{employeeId}")
    public Mono<ResponseEntity> deleteEmployee(@PathVariable int employeeId) {
        return this.checkEmployeeExistence(employeeId)
            .delayUntil(employee -> this.employeeService.deleteById(employeeId))
            .map((v) ->
                new ResponseEntity<>(
                new ApiResponse(HttpStatus.OK.value(),
                    "Employee with ID: " + employeeId + " deleted successfully"),
                HttpStatus.OK
            )
        );
    }

    private Mono<Employee> checkEmployeeExistence(int employeeId) {
        if (employeeId == 0) {
            return Mono.error(new NotFoundException("The Employee ID must be set"));
        }

        return this.employeeService.findById(employeeId);
    }
}
