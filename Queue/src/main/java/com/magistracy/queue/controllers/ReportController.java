package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Report;
import com.magistracy.queue.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Endpoint to generate the report without using ReportRequest and ReportResponse classes
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, String> request) {
        // Extract startDate and endDate from the request map
        LocalDateTime startDate = LocalDateTime.parse(request.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(request.get("endDate"));

        // Generate the report
        Report report = reportService.generateReport(startDate, endDate);

        // Generate the CSV content for download
        byte[] fileContent = generateReportFile(report);

        // Prepare the response without using ReportResponse
        Map<String, Object> response = Map.of(
                "totalTickets", report.getTotalTickets(),
                "averageWaitingTime", report.getAverageWaitingTime(),
                "maxWaitingTime", report.getMaxWaitingTime(),
                "minWaitingTime", report.getMinWaitingTime(),
                "csvContent", new String(fileContent, StandardCharsets.UTF_8)
        );

        return ResponseEntity.ok(response);
    }

    // Helper method to generate CSV content
    private byte[] generateReportFile(Report report) {
        String csvContent = "Total Tickets, Average Waiting Time (minutes), Max Waiting Time (minutes), Min Waiting Time (minutes)\n" +
                report.getTotalTickets() + ", " +
                report.getAverageWaitingTime() + ", " +
                report.getMaxWaitingTime() + ", " +
                report.getMinWaitingTime() + "\n";

        return csvContent.getBytes(StandardCharsets.UTF_8);
    }
}
