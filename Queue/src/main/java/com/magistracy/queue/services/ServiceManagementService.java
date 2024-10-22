package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ServiceEntity addService(ServiceEntity serviceEntity) {
        return serviceEntityRepository.save(serviceEntity);
    }

    @Transactional
    public ServiceEntity updateService(Long id, ServiceEntity serviceEntity) {
        ServiceEntity existingService = serviceEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        existingService.setServiceName(serviceEntity.getServiceName());
        existingService.setServiceDescription(serviceEntity.getServiceDescription());

        return serviceEntityRepository.save(existingService);
    }

    @Transactional
    public void deleteService(Long id) {
        serviceEntityRepository.deleteById(id);
    }
}
