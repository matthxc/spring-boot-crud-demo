package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.rest.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Flux<Employee> findAll() {
        return Flux.fromIterable(this.employeeRepository.findAll());
    }

    @Override
    public Mono<Employee> findById(int theId) {
        return Mono.just(employeeRepository.findById(theId)).map(employee -> {
            if(employee.isPresent()) {
                return employee.get();
            }

            throw new NotFoundException("Employee not found with ID: " + theId);
        });
    }

    @Override
    public Mono<Employee> save(Employee theEmployee) {
        return Mono.create(employeeMonoSink -> {
            Employee employee = employeeRepository.save(theEmployee);
            employeeMonoSink.success(employee);
        });
    }

    @Override
    public Mono<Void> deleteById(int theId) {
        return Mono.create(voidMonoSink -> {
            employeeRepository.deleteById(theId);
            voidMonoSink.success();
        });
    }
}
