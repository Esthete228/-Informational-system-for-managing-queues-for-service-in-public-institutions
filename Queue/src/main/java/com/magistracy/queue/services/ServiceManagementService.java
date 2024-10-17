package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceManagementService {

    private final ServiceEntityRepository serviceEntityRepository;

    @Autowired
    public ServiceManagementService(ServiceEntityRepository serviceEntityRepository) {
        this.serviceEntityRepository = serviceEntityRepository;
    }

    public ServiceEntity addService(ServiceEntity service) {
        // Переконайтесь, що всі поля заповнені перед збереженням
        System.out.println("Додаємо послугу з назвою: " + service.getServiceName());  // Додаємо логування
        return serviceEntityRepository.save(service);
    }

    public ServiceEntity updateService(Long serviceId, ServiceEntity updatedService) {
        return serviceEntityRepository.findById(serviceId)
                .map(service -> {
                    service.setServiceName(updatedService.getServiceName());
                    service.setServiceDescription(updatedService.getServiceDescription());
                    service.setAssignedWorkplaces(updatedService.getAssignedWorkplaces());
                    return serviceEntityRepository.save(service); // Оновлення в базі
                }).orElseThrow(() -> new RuntimeException("Послугу не знайдено"));
    }

    public void deleteService(Long serviceId) {
        serviceEntityRepository.deleteById(serviceId);
    }
}
