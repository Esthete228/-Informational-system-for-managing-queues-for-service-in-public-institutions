package com.magistracy.queue.entities;

/**
 * @param csvContent CSV content as string
 */
public record ReportResponse(long totalTickets, long averageWaitingTime, long maxWaitingTime, long minWaitingTime,
                             String csvContent) {
}