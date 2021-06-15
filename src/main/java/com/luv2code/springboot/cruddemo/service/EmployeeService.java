package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.entity.Employee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    public Flux<Employee> findAll();

    public Mono<Employee> findById(int theId);

    public Mono<Employee> save(Employee theEmployee);

    public Mono<Void> deleteById(int theId);
}
