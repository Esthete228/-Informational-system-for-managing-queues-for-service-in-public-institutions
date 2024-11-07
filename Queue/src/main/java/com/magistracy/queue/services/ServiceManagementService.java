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

    public ServiceEntity addService(ServiceEntity serviceEntity) {
        return serviceEntityRepository.save(serviceEntity);
    }

    public ServiceEntity updateService(Long id, ServiceEntity serviceEntity) {
        ServiceEntity existingService = serviceEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        existingService.setServiceName(serviceEntity.getServiceName());
        existingService.setServiceDescription(serviceEntity.getServiceDescription());

        return serviceEntityRepository.save(existingService);
    }

    public void deleteService(Long id) {
        serviceEntityRepository.deleteById(id);
    }
}
