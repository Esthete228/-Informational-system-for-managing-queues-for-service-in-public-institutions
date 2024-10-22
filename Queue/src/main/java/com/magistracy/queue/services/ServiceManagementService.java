package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceManagementService {

    private final ServiceEntityRepository serviceEntityRepository;

    public ServiceManagementService(ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository) {
        this.serviceEntityRepository = serviceEntityRepository;
    }

    // Отримати всі послуги
    public List<ServiceEntity> getAllServices() {
        return serviceEntityRepository.findAll();
    }

    // Додати нову послугу
    public ServiceEntity addService(ServiceEntity serviceEntity) {
        return serviceEntityRepository.save(serviceEntity);
    }

    // Оновити існуючу послугу
    public ServiceEntity updateService(Long id, ServiceEntity serviceEntity) {
        // Перш ніж оновити, потрібно перевірити, чи існує послуга
        ServiceEntity existingService = serviceEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        existingService.setServiceName(serviceEntity.getServiceName());
        existingService.setServiceDescription(serviceEntity.getServiceDescription());
        existingService.setWorkplaces(serviceEntity.getWorkplaces());

        return serviceEntityRepository.save(existingService);
    }

    // Видалити послугу
    public void deleteService(Long id) {
        serviceEntityRepository.deleteById(id);
    }
}
