package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Report;
import com.magistracy.queue.entities.ReportRequest;
import com.magistracy.queue.entities.ReportResponse;
import com.magistracy.queue.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController (ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequest request) {
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        Report report = reportService.generateReport(startDate, endDate);

        // Generate the CSV content for download
        byte[] fileContent = generateReportFile(report);

        // Prepare the response with both the CSV content and the report data
        ReportResponse response = new ReportResponse(
                report.getTotalTickets(),
                report.getAverageWaitingTime(),
                report.getMaxWaitingTime(),
                report.getMinWaitingTime(),
                new String(fileContent) // Pass CSV content as a string
        );

        return ResponseEntity.ok(response);
    }

    private byte[] generateReportFile(Report report) {

        // Adding header to CSV

        String sb = "Total Tickets, Average Waiting Time (minutes), Max Waiting Time (minutes), Min Waiting Time (minutes)\n" +

                // Adding the report data
                report.getTotalTickets() + ", " +
                report.getAverageWaitingTime() + ", " +
                report.getMaxWaitingTime() + ", " +
                report.getMinWaitingTime() + "\n";

        // Return the CSV file as bytes
        return sb.getBytes(StandardCharsets.UTF_8);
    }
}
