package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByServiceAndAppointmentTime(ServiceEntity service, LocalDateTime time);
}
