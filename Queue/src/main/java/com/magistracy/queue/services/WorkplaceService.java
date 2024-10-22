package com.magistracy.queue.services;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;

    public WorkplaceService(WorkplaceRepository workplaceRepository, ServiceEntityRepository serviceEntityRepository, ServiceEntityRepository serviceEntityRepository1) {
        this.workplaceRepository = workplaceRepository;
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
}
