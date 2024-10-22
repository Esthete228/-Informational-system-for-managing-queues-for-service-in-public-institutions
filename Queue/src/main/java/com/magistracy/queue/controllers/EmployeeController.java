package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.EmployeeRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import com.magistracy.queue.services.EmployeeService;
import com.magistracy.queue.services.WorkplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-dashboard/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    private final WorkplaceService workplaceService;

    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, EmployeeService employeeService, WorkplaceService workplaceService, WorkplaceRepository workplaceRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.workplaceService = workplaceService;
        this.workplaceRepository = workplaceRepository;
    }

    // Отримати всіх працівників
    @GetMapping("/all-employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<Employee> addEmployee(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String role = (String) request.get("role");
        Long workplaceId = Long.valueOf((String) request.get("workplaceId"));

        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Робоче місце не знайдено"));

        Employee newEmployee = new Employee();
        newEmployee.setUsername(username);
        newEmployee.setPassword(password);
        newEmployee.setRole(role);
        newEmployee.setWorkplace(workplace); // Прив'язуємо працівника до робочого місця

        employeeRepository.save(newEmployee);
        return ResponseEntity.ok(newEmployee);
    }

    @PutMapping("/update-employee/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long employeeId, @RequestBody Map<String, Object> request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Працівника не знайдено"));

        employee.setUsername((String) request.get("username"));
        employee.setPassword((String) request.get("password"));
        employee.setRole((String) request.get("role"));

        Long workplaceId = Long.valueOf((String) request.get("workplaceId"));
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Робоче місце не знайдено"));

        employee.setWorkplace(workplace); // Оновлюємо робоче місце працівника

        employeeRepository.save(employee);
        return ResponseEntity.ok(employee);
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
