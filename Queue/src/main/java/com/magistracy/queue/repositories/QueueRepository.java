package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT w FROM Workplace w WHERE " +
            "(SELECT COUNT(q) FROM Queue q WHERE q.workplace.id = w.id AND q.status = :status) < :limit " +
            "ORDER BY (SELECT COUNT(q) FROM Queue q WHERE q.workplace.id = w.id AND q.status = :status) ASC")
    List<Workplace> findAvailableWorkplaces(@Param("status") Queue.QueueStatus status, @Param("limit") int limit);
}