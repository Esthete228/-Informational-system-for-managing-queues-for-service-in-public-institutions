package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.EmployeeRepository;
import com.magistracy.queue.services.EmployeeService;
import com.magistracy.queue.services.WorkplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-dashboard/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    private final WorkplaceService workplaceService;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, EmployeeService employeeService, WorkplaceService workplaceService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.workplaceService = workplaceService;
    }

    // Отримати всіх працівників
    @GetMapping("/all-employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        try {
            Employee savedEmployee = employeeService.save(employee);
            return ResponseEntity.ok(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update-employee/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long employeeId, @RequestBody Employee employee) {
        Employee existingEmployee = employeeService.findById(employeeId);
        existingEmployee.setUsername(employee.getUsername());
        existingEmployee.setPassword(employee.getPassword());
        existingEmployee.setRole(employee.getRole());
        employeeService.save(existingEmployee);
        return ResponseEntity.ok(existingEmployee);
    }

    // Видалити працівника
    @DeleteMapping("/delete-employee/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return ResponseEntity.notFound().build(); // Якщо працівник не знайдений
        }

        employeeRepository.deleteById(employeeId);
        return ResponseEntity.ok("Працівника успішно видалено");
    }
}
