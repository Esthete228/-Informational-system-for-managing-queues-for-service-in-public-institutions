package com.magistracy.queue.entities;

public class ReportResponse {

    private long totalTickets;
    private long averageWaitingTime;
    private long maxWaitingTime;
    private long minWaitingTime;
    private String csvContent; // CSV content as string

    // Constructor
    public ReportResponse(long totalTickets, long averageWaitingTime, long maxWaitingTime, long minWaitingTime, String csvContent) {
        this.totalTickets = totalTickets;
        this.averageWaitingTime = averageWaitingTime;
        this.maxWaitingTime = maxWaitingTime;
        this.minWaitingTime = minWaitingTime;
        this.csvContent = csvContent;
    }

    // Getters and setters
    public long getTotalTickets() {
        return totalTickets;
    }

    public long getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public long getMinWaitingTime() {
        return minWaitingTime;
    }

    public String getCsvContent() {
        return csvContent;
    }
}