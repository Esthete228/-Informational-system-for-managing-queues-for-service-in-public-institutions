package com.magistracy.queue.services;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Report;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final QueueRepository queueRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(QueueRepository queueRepository, ReportRepository reportRepository) {
        this.queueRepository = queueRepository;
        this.reportRepository = reportRepository;
    }

    // Generate and store the report in the database
    public Report generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Retrieve the queues for the given period
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

        // Create a new report entity and store it
        Report report = new Report(totalTickets, averageWaitingTime, maxWaitingTime, minWaitingTime);

        // Save the report to the database
        return reportRepository.save(report);
    }

    // Method to delete the report after CSV generation
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }
}
