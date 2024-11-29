package com.magistracy.queue.services;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceWorkplace;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.ServiceWorkplaceRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private final ServiceWorkplaceRepository serviceWorkplaceRepository;
    private final QueueRepository queueRepository;

    public WorkplaceService(WorkplaceRepository workplaceRepository, ServiceEntityRepository serviceEntityRepository, ServiceEntityRepository serviceEntityRepository1, ServiceWorkplaceRepository serviceWorkplaceRepository, QueueRepository queueRepository) {
        this.workplaceRepository = workplaceRepository;
        this.serviceWorkplaceRepository = serviceWorkplaceRepository;
        this.queueRepository = queueRepository;
    }

    public List<Workplace> findAll() {
        return workplaceRepository.findAll();
    }

    public Workplace save(Workplace workplace) {
        return workplaceRepository.save(workplace);
    }

    public Workplace update(Long id, Workplace workplace) {
        Workplace existingWorkplace = findById(id);
        if (existingWorkplace != null) {
            existingWorkplace.setWorkplaceName(workplace.getWorkplaceName());
            return workplaceRepository.save(existingWorkplace);
        }
        return null;
    }

    public void delete(Long id) {
        workplaceRepository.deleteById(id);
    }

    public Workplace findById(Long id) {
        return workplaceRepository.findById(id).orElse(null);
    }

    public Workplace findLeastLoadedWorkplaceForService(Long serviceId) {
        // Отримуємо робочі місця, які можуть обслуговувати цю послугу
        List<Workplace> workplaces = workplaceRepository.findAll();

        // Фільтруємо робочі місця, які підтримують цю послугу
        List<ServiceWorkplace> serviceWorkplaces = serviceWorkplaceRepository.findByServiceId(serviceId);

        // Знаходимо робочі місця, пов'язані з послугою
        List<Workplace> availableWorkplaces = workplaces.stream()
                .filter(workplace -> serviceWorkplaces.stream()
                        .anyMatch(serviceWorkplace -> serviceWorkplace.getWorkplace().equals(workplace)))
                .toList();

        if (availableWorkplaces.isEmpty()) {
            return null; // Якщо немає доступних робочих місць для цієї послуги
        }

        // Повертаємо робоче місце з мінімальним навантаженням
        return availableWorkplaces.stream()
                .min(Comparator.comparingInt(wp -> queueRepository.findByWorkplaceIdAndStatus(wp.getId(), Queue.QueueStatus.ACTIVE).size()))
                .orElseThrow(() -> new RuntimeException("Робочі місця для цієї послуги недоступні"));
    }
}
