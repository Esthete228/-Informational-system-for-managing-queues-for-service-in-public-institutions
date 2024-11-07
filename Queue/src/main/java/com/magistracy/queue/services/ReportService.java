package com.magistracy.queue.services;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Report;
import com.magistracy.queue.repositories.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final QueueRepository queueRepository;

    @Autowired
    public ReportService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    // Створення статистики за обраний період
    public Report generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Отримуємо черги за обраний період
        List<Queue> queues = queueRepository.findByCreatedAtBetween(startDate, endDate);

        long totalTickets = queues.size();
        long totalWaitingTime = queues.stream()
                .mapToLong(queue -> {
                    if (queue.getStartedAt() != null) {
                        return Duration.between(queue.getCreatedAt(), queue.getStartedAt()).toMinutes();
                    }
                    return 0;
                })
                .sum();

        long maxWaitingTime = queues.stream()
                .mapToLong(queue -> {
                    if (queue.getStartedAt() != null) {
                        return Duration.between(queue.getCreatedAt(), queue.getStartedAt()).toMinutes();
                    }
                    return 0;
                })
                .max()
                .orElse(0);

        long minWaitingTime = queues.stream()
                .mapToLong(queue -> {
                    if (queue.getStartedAt() != null) {
                        return Duration.between(queue.getCreatedAt(), queue.getStartedAt()).toMinutes();
                    }
                    return 0;
                })
                .min()
                .orElse(0);

        long averageWaitingTime = totalTickets == 0 ? 0 : totalWaitingTime / totalTickets;

        return new Report(totalTickets, averageWaitingTime, maxWaitingTime, minWaitingTime);
    }
}

