package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.ServiceWorkplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import com.magistracy.queue.services.ServiceManagementService;
import com.magistracy.queue.services.ServiceWorkplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceManagementService serviceManagementService;
    private final ServiceWorkplaceService serviceWorkplaceService;

    public ServiceController(ServiceManagementService serviceManagementService, ServiceWorkplaceService serviceWorkplaceService) {
        this.serviceManagementService = serviceManagementService;
        this.serviceWorkplaceService = serviceWorkplaceService;
    }

    // Отримати всі послуги
    @GetMapping("/all-services")
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceManagementService.getAllServices();
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

        ServiceEntity savedService = serviceManagementService.addService(service);
        return ResponseEntity.ok(savedService);
    }

    // Оновлення послуги
    @PutMapping("/update-service/{serviceId}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long serviceId, @RequestBody Map<String, Object> payload) {
        String serviceName = payload.get("serviceName").toString();
        String serviceDescription = payload.get("serviceDescription").toString();

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setId(serviceId);
        serviceEntity.setServiceName(serviceName);
        serviceEntity.setServiceDescription(serviceDescription);

        ServiceEntity updatedService = serviceManagementService.updateService(serviceId, serviceEntity);
        return ResponseEntity.ok(updatedService);
    }

    // Видалення послуги
    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
