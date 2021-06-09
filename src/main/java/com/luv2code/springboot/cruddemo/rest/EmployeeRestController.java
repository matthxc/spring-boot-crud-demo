package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService) {
        this.employeeService = theEmployeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<DataResponse> findAll() {
        return new ResponseEntity<>(new DataResponse<>(HttpStatus.OK.value(), this.employeeService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<DataResponse> findById(@PathVariable int employeeId) {
        return new ResponseEntity<>(new DataResponse<>(HttpStatus.OK.value(), this.employeeService.findById(employeeId)), HttpStatus.OK);
    }

    @PostMapping("/employees")
    public ResponseEntity<DataResponse> addEmployee(@RequestBody Employee theEmployee) {
        // Set it to 0 to insert a new record
        theEmployee.setId(0);

        this.employeeService.save(theEmployee);

        return new ResponseEntity<>(new DataResponse<>(HttpStatus.OK.value(), "Employee added successfully", theEmployee), HttpStatus.OK);
    }

    @PutMapping("/employees")
    public ResponseEntity<DataResponse> updateEmployee(@RequestBody Employee theEmployee) {
        this.checkEmployeeExistence(theEmployee.getId());

        this.employeeService.save(theEmployee);

        return new ResponseEntity<>(new DataResponse<>(HttpStatus.OK.value(), "Employee updated successfully", theEmployee), HttpStatus.OK);
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable int employeeId) {
        this.checkEmployeeExistence(employeeId);

        this.employeeService.deleteById(employeeId);

        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Employee with ID: " + employeeId + " deleted successfully"), HttpStatus.OK);
    }

    private void checkEmployeeExistence(Integer employeeId) {
        if (employeeId == null || employeeId == 0) {
            throw new NotFoundException("The Employee ID must be set");
        }

        this.employeeService.findById(employeeId);
    }
}
