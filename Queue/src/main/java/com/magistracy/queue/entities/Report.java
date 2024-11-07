package com.magistracy.queue.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final long totalTickets;
    private final long averageWaitingTime;
    private final long maxWaitingTime;
    private final long minWaitingTime;

    // No-argument constructor required for JPA
    public Report() {
        this.totalTickets = 0;
        this.averageWaitingTime = 0;
        this.maxWaitingTime = 0;
        this.minWaitingTime = 0;
    }

    // Constructor with parameters
    public Report(long totalTickets, long averageWaitingTime, long maxWaitingTime, long minWaitingTime) {
        this.totalTickets = totalTickets;
        this.averageWaitingTime = averageWaitingTime;
        this.maxWaitingTime = maxWaitingTime;
        this.minWaitingTime = minWaitingTime;
    }

    // Getters
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
