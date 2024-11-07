package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

    List<Queue> findByWorkplaceIdAndStatus(Long workplaceId, Queue.QueueStatus status);

    Optional <Queue> findFirstByWorkplaceIdAndStatus(Long workplaceId, Queue.QueueStatus queueStatus);

    List<Queue> findByStatus(Queue.QueueStatus status);

    List<Queue> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}