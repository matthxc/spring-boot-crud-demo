package com.luv2code.springboot.cruddemo.graphql;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.rest.NotFoundException;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Service
public class RootGraphqlQueryResolver implements GraphQLQueryResolver {
  private EmployeeRepository employeeRepository;

  Logger logger = LoggerFactory.getLogger(getClass().getName());

  @Autowired
  public RootGraphqlQueryResolver(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public List<Employee> findAll() {
    return employeeRepository.findAll();
  }

  public Employee findById(int theId) {
    Optional<Employee> result = employeeRepository.findById(theId);

    if (result.isPresent()) {
      return result.get();
    }

    throw new NotFoundException("Employee not found with ID: " + theId);
  }
}
