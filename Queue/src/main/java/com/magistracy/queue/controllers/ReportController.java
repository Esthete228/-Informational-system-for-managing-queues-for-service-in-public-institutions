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

    // Endpoint to generate the report and return it as an object
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, String> request) {
        try {
            // Log the incoming request data for debugging
            System.out.println("Start Date: " + request.get("startDate"));
            System.out.println("End Date: " + request.get("endDate"));

            LocalDateTime startDate = LocalDateTime.parse(request.get("startDate"));
            LocalDateTime endDate = LocalDateTime.parse(request.get("endDate"));

            // Log parsed dates
            System.out.println("Parsed Start Date: " + startDate);
            System.out.println("Parsed End Date: " + endDate);

            // Generate the report and store it in the database
            Report report = reportService.generateReport(startDate, endDate);

            // Generate the CSV content for download
            byte[] fileContent = generateReportFile(report);

            // Prepare the response with the generated report data
            Map<String, Object> response = Map.of(
                    "totalTickets", report.getTotalTickets(),
                    "averageWaitingTime", report.getAverageWaitingTime(),
                    "maxWaitingTime", report.getMaxWaitingTime(),
                    "minWaitingTime", report.getMinWaitingTime(),
                    "csvContent", new String(fileContent, StandardCharsets.UTF_8),
                    "reportId", report.getId()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(500).body(Map.of("error", "Invalid date format"));
        }
    }

    // Endpoint to delete the report after download
    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("Report deleted successfully");
    }

    // Helper method to generate CSV content from the report
    private byte[] generateReportFile(Report report) {
        String csvContent = "Total Tickets, Average Waiting Time (minutes), Max Waiting Time (minutes), Min Waiting Time (minutes)\n" +
                report.getTotalTickets() + ", " +
                report.getAverageWaitingTime() + ", " +
                report.getMaxWaitingTime() + ", " +
                report.getMinWaitingTime() + "\n";

        return csvContent.getBytes(StandardCharsets.UTF_8);
    }
}
