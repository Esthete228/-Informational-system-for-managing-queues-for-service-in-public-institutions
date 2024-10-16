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
public class ServiceController {

    private final ServiceEntityRepository serviceEntityRepository;

    private final ServiceManagementService serviceManagementService;

    public ServiceController(ServiceManagementService serviceManagementService, ServiceEntityRepository serviceEntityRepository) {
        this.serviceManagementService = serviceManagementService;
        this.serviceEntityRepository = serviceEntityRepository;

    }

    @GetMapping("/all-services")
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceEntityRepository.findAll();
        System.out.println("Available services: " + services); // Додайте це для перевірки
        return ResponseEntity.ok(services);
    }

    // Додати нову послугу
    @PostMapping("/add")
    public ResponseEntity<ServiceEntity> addService(@RequestBody ServiceEntity service) {
        ServiceEntity newService = serviceManagementService.addService(service);
        return ResponseEntity.ok(newService);
    }

    // Оновити існуючу послугу
    @PutMapping("/update/{serviceId}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long serviceId, @RequestBody ServiceEntity updatedService) {
        ServiceEntity updated = serviceManagementService.updateService(serviceId, updatedService);
        return ResponseEntity.ok(updated);
    }

    // Видалити послугу
    @DeleteMapping("/delete/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable Long serviceId) {
        serviceManagementService.deleteService(serviceId);
        return ResponseEntity.ok("Послугу успішно видалено");
    }
}
