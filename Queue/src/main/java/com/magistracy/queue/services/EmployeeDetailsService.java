/*
package com.magistracy.queue.services;

import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service  // Додана анотація @Service
public class EmployeeDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Для шифрування паролів

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isEmpty()) {
            throw new UsernameNotFoundException("Працівник не знайдений");
        }

        Employee emp = employee.get();
        return User.builder()
                .username(emp.getUsername())
                .password(emp.getPassword())  // Вже зашифрований пароль
                .roles("EMPLOYEE")
                .build();
    }
}
*/
