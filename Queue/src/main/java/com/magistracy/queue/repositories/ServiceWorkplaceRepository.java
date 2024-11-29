package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.ServiceWorkplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceWorkplaceRepository extends JpaRepository<ServiceWorkplace, Long> {

    // Отримати всі зв'язки для заданої послуги (serviceId)
    List<ServiceWorkplace> findByServiceId(Long serviceId);

    // Отримати всі зв'язки для заданого робочого місця (workplaceId)
    List<ServiceWorkplace> findByWorkplaceId(Long workplaceId);

    // Пошук одного зв'язку між послугою та робочим місцем
    Optional<ServiceWorkplace> findByServiceIdAndWorkplaceId(Long serviceId, Long workplaceId);

    // Пошук багатьох зв'язків для послуги і робочого місця
    List<ServiceWorkplace> findAllByServiceIdAndWorkplaceId(Long serviceId, Long workplaceId);

}
