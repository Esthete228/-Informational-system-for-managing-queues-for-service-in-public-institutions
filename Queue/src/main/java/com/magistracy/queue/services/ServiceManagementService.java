package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceManagementService {

    private final ServiceEntityRepository serviceEntityRepository;

    public ServiceManagementService(ServiceEntityRepository serviceEntityRepository) {
        this.serviceEntityRepository = serviceEntityRepository;
    }

    public List<ServiceEntity> getAllServices() {
        return serviceEntityRepository.findAll();
    }

    public ServiceEntity addService(ServiceEntity service) {
        return serviceEntityRepository.save(service);
    }

    public ServiceEntity updateService(Long id, ServiceEntity service) {
        ServiceEntity existingService = serviceEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Послуга не знайдена"));
        existingService.setServiceName(service.getServiceName());
        existingService.setServiceDescription(service.getServiceDescription());
        existingService.setAssignedWorkplaces(service.getAssignedWorkplaces());
        return serviceEntityRepository.save(existingService);
    }

    public void deleteService(Long id) {
        serviceEntityRepository.deleteById(id);
    }
}
