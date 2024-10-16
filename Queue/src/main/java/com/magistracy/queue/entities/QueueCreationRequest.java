package com.magistracy.queue.entities;

import java.time.LocalDateTime;

public class QueueCreationRequest {
    private User user;
    private Long serviceEntityId;
    private LocalDateTime appointmentTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getServiceEntityId() {
        return serviceEntityId;
    }

    public void setServiceEntityId(Long serviceEntityId) {
        this.serviceEntityId = serviceEntityId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}