package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    List<Queue> findByWorkplaceId(Long workplaceId);
}
