package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Додайте необхідні методи запитів тут
    Optional<Employee> findByUsername(String username);

    Employee removeEmployeeByUsername(Employee employee);

    Optional<Employee> findByUsernameAndPassword(String username, String password);
}
