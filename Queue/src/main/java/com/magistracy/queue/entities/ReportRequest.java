package com.magistracy.queue.entities;

import java.time.LocalDateTime;

public class ReportRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Конструктори, геттери, сеттери
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}