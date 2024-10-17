package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-dashboard/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Отримати всіх працівників
    @GetMapping("/all-employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    // Додати нового працівника
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        // Валідація даних працівника
        if (employee.getUsername() == null || employee.getPassword() == null || employee.getRole() == null) {
            return ResponseEntity.badRequest().body("Не всі поля заповнені.");
        }

        Employee newEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(newEmployee);
    }

    // Оновити існуючого працівника
    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long employeeId, @RequestBody Employee updatedEmployee) {
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    employee.setUsername(updatedEmployee.getUsername());
                    employee.setPassword(updatedEmployee.getPassword());
                    Employee savedEmployee = employeeRepository.save(employee);
                    return ResponseEntity.ok(savedEmployee);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Видалити працівника
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return ResponseEntity.notFound().build(); // Якщо працівник не знайдений
        }

        employeeRepository.deleteById(employeeId);
        return ResponseEntity.ok("Працівника успішно видалено");
    }
}
