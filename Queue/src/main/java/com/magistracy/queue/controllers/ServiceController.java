package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import com.magistracy.queue.services.ServiceManagementService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceEntityRepository serviceEntityRepository;

    public ServiceController(ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository, ServiceManagementService serviceManagementService) {
        this.serviceEntityRepository = serviceEntityRepository;
    }

    // Отримати всі послуги
    @GetMapping("/all-services")
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceEntityRepository.findAll();
        return ResponseEntity.ok(services);
    }

    // Додавання нової послуги
    @PostMapping("/add-service")
    public ResponseEntity<ServiceEntity> addService(@RequestBody Map<String, Object> payload) {
        String serviceName = payload.get("serviceName").toString();
        String serviceDescription = payload.get("serviceDescription").toString();

        ServiceEntity service = new ServiceEntity();
        service.setServiceName(serviceName);
        service.setServiceDescription(serviceDescription);

        ServiceEntity savedService = serviceEntityRepository.save(service);
        return ResponseEntity.ok(savedService);
    }

    // Оновлення послуги
    @PutMapping("/update-service/{serviceId}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long serviceId, @RequestBody Map<String, Object> payload) {
        ServiceEntity existingService = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        existingService.setServiceName(payload.get("serviceName").toString());
        existingService.setServiceDescription(payload.get("serviceDescription").toString());

        ServiceEntity updatedService = serviceEntityRepository.save(existingService);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceEntityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
