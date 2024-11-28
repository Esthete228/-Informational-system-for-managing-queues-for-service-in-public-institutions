package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.ServiceWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceWorkplaceRepository extends JpaRepository<ServiceWorkplace, Long> {
    List<ServiceWorkplace> findByServiceId(Long serviceId);
    List<ServiceWorkplace> findByWorkplaceId(Long workplaceId);
    List<ServiceWorkplace> findByServiceIdAndWorkplaceId(Long serviceId, Long workplaceId); // New method
}
