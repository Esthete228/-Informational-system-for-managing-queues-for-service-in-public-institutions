package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.User;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-dashboard")
public class AdminController {

    @Autowired
    private ServiceEntityRepository serviceEntityRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private UserRepository userRepository;

    // Додавання нової послуги
    @PostMapping("/add-service")
    public ResponseEntity<ServiceEntity> addService(@RequestBody ServiceEntity service) {
        ServiceEntity newService = serviceEntityRepository.save(service);
        return ResponseEntity.ok(newService);
    }

    // Видалення послуги
    @DeleteMapping("/delete-service/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable Long serviceId) {
        serviceEntityRepository.deleteById(serviceId);
        return ResponseEntity.ok("Послугу успішно видалено");
    }

    // Моніторинг черг (перегляд усіх записів)
    @GetMapping("/queues")
    public ResponseEntity<List<Queue>> getAllQueues() {
        List<Queue> allQueues = queueRepository.findAll();
        return ResponseEntity.ok(allQueues);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        user.setRole("user");
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}