package com.magistracy.queue.services;


import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // анотація для сервісу Spring залишається
public class ServiceManagementService {

    @Autowired
    private ServiceEntityRepository serviceEntityRepository;

    // Додавання нової послуги
    public ServiceEntity addService(ServiceEntity service) {
        return serviceEntityRepository.save(service);
    }

    // Отримання всіх послуг
    public List<ServiceEntity> getAllServices() {
        return serviceEntityRepository.findAll();
    }

    // Отримання послуги за ID
    public ServiceEntity getServiceById(Long serviceId) {
        return serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));
    }

    // Оновлення існуючої послуги
    public ServiceEntity updateService(Long serviceId, ServiceEntity updatedService) {
        ServiceEntity existingService = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));

        existingService.setServiceName(updatedService.getServiceName());
        existingService.setDescription(updatedService.getDescription());

        return serviceEntityRepository.save(existingService);
    }

    // Видалення послуги
    public void deleteService(Long serviceId) {
        serviceEntityRepository.deleteById(serviceId);
    }
}

