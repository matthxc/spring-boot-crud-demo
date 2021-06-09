package com.luv2code.springboot.cruddemo.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.luv2code.springboot.cruddemo.entity.Employee;

@Repository
public class EmployeeDAOJpaImpl implements EmployeeDAO {
  private EntityManager entityManager;

  @Autowired
  public EmployeeDAOJpaImpl(EntityManager theEntityManager) {
    this.entityManager = theEntityManager;
  }

  @Override
  public List<Employee> findAll() {
    Query theQuery = entityManager.createQuery("from Employee");

    return theQuery.getResultList();
  }

  @Override
  public Employee findById(int theId) {
    return entityManager.find(Employee.class, theId);
  }

  @Override
  public void save(Employee theEmployee) {
    Employee dbEmployee = entityManager.merge(theEmployee);

    theEmployee.setId(dbEmployee.getId());
  }

  @Override
  public void deleteById(int theId) {
    Query theQuery = entityManager.createQuery("delete from Employee where id=:employee");

    theQuery.setParameter("employee", theId);

    theQuery.executeUpdate();
  }
}
