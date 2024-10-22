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
    private final WorkplaceRepository workplaceRepository;
    private final ServiceManagementService serviceManagementService;

    public ServiceController(ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository, ServiceManagementService serviceManagementService) {
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping("/all-services") // This should match your fetch call
    public List<ServiceEntity> getAllServices() {
        return serviceEntityRepository.findAll();
    }

    @PostMapping("/add-service")
    public ResponseEntity<ServiceEntity> addService(@RequestBody Map<String, Object> payload) {
        System.out.println("Received payload: " + payload);
        String serviceName = (String) payload.get("serviceName");
        String serviceDescription = (String) payload.get("serviceDescription");

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceName(serviceName);
        serviceEntity.setServiceDescription(serviceDescription);

        ServiceEntity savedService = serviceEntityRepository.save(serviceEntity);
        return ResponseEntity.ok(savedService);
    }

    @PutMapping("/update-service/{serviceId}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long serviceId, @RequestBody ServiceEntity service) {
        // Знаходимо існуючу послугу
        ServiceEntity existingService = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Оновлюємо поля послуги
        existingService.setServiceName(service.getServiceName());
        existingService.setServiceDescription(service.getServiceDescription());

        // Зберігаємо оновлену послугу
        ServiceEntity updatedService = serviceEntityRepository.save(existingService);

        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceEntityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
