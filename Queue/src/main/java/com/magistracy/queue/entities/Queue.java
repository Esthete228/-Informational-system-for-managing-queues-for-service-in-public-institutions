// Queue.java

package com.magistracy.queue.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Queue {

    public enum QueueStatus {
        ACTIVE, IN_PROGRESS, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonManagedReference
    private ServiceEntity serviceEntity;

    @ManyToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    @JsonManagedReference
    private Workplace workplace;

    private int ticketNumber;

    @Enumerated(EnumType.STRING)
    private QueueStatus status;


    private LocalDateTime createdAt;  // Time when the ticket was created
    private LocalDateTime startedAt;  // Time when the ticket started being processed


    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();  // Set the current time when the ticket is created
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == QueueStatus.IN_PROGRESS && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();  // Set the time when the ticket is being processed
        }
    }

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
}