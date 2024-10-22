package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.services.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    public ServiceController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    // Отримати всі послуги
    @GetMapping("/all-services")
    public List<ServiceEntity> getAllServices() {
        return serviceManagementService.getAllServices();
    }

    // Додати нову послугу
    @PostMapping("/add-service")
    public ServiceEntity addService(@RequestBody ServiceEntity serviceEntity) {
        return serviceManagementService.addService(serviceEntity);
    }

    // Оновити існуючу послугу
    @PutMapping("/update-service/{id}")
    public ServiceEntity updateService(@PathVariable Long id, @RequestBody ServiceEntity serviceEntity) {
        return serviceManagementService.updateService(id, serviceEntity);
    }

    // Видалити послугу
    @DeleteMapping("/delete-service/{id}")
    public void deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
    }
}