package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.services.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceEntityController {

    @Autowired
    private ServiceEntityRepository serviceEntityRepository;

    // Створення нової послуги
    @PostMapping("/create")
    public ResponseEntity<ServiceEntity> createService(@RequestBody ServiceEntity serviceEntity) {
        ServiceEntity savedService = serviceEntityRepository.save(serviceEntity);
        return ResponseEntity.ok(savedService);
    }

    // Отримання всіх послуг
    @GetMapping("/all")
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceEntityRepository.findAll();
        return ResponseEntity.ok(services);
    }

    // Отримання послуги за ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        return serviceEntityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}