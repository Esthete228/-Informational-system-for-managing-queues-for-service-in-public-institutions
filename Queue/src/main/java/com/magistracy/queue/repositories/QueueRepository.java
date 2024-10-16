package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findByServiceEntityOrderByAppointmentTimeAsc(ServiceEntity serviceEntity);

    List<Queue> findByServiceEntityAndAppointmentTime(ServiceEntity service, LocalDateTime time);

    List<Queue> findByServiceEntity(ServiceEntity serviceEntity);

}
