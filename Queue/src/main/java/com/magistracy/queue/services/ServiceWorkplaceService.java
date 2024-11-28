package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.ServiceWorkplace;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.ServiceWorkplaceRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceWorkplaceService {

    private final ServiceWorkplaceRepository serviceWorkplaceRepository;
    private final ServiceEntityRepository serviceEntityRepository;
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public ServiceWorkplaceService(ServiceWorkplaceRepository serviceWorkplaceRepository,
                                   ServiceEntityRepository serviceEntityRepository,
                                   WorkplaceRepository workplaceRepository) {
        this.serviceWorkplaceRepository = serviceWorkplaceRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
    }

    // Get all services linked to a particular workplace
    public List<ServiceWorkplace> getServicesForWorkplace(Long workplaceId) {
        return serviceWorkplaceRepository.findByWorkplaceId(workplaceId);
    }

    // Get all workplaces linked to a particular service
    public List<ServiceWorkplace> getWorkplacesForService(Long serviceId) {
        return serviceWorkplaceRepository.findByServiceId(serviceId);
    }

    // Link a service to a workplace
    @Transactional
    public void linkServiceToWorkplace(Long serviceId, Long workplaceId) {
        ServiceEntity service = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Workplace not found"));

        // Перевірка на існування
        boolean exists = serviceWorkplaceRepository.findByServiceIdAndWorkplaceId(serviceId, workplaceId).stream()
                .anyMatch(sw -> sw.getService().equals(service) && sw.getWorkplace().equals(workplace));

        if (!exists) {
            ServiceWorkplace serviceWorkplace = new ServiceWorkplace();
            serviceWorkplace.setService(service);
            serviceWorkplace.setWorkplace(workplace);
            serviceWorkplaceRepository.save(serviceWorkplace);
        }
    }

    // Unlink a service from a workplace
    @Transactional
    public void unlinkServiceFromWorkplace(Long serviceId, Long workplaceId) {
        // Знаходимо всі зв'язки послуги і робочого місця
        List<ServiceWorkplace> linkedServices = serviceWorkplaceRepository.findByServiceIdAndWorkplaceId(serviceId, workplaceId);

        // Якщо зв'язок знайдений, видаляємо його
        serviceWorkplaceRepository.deleteAll(linkedServices);
    }
}
