package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByAppointmentTimeAndServiceEntity(LocalDateTime appointmentTime, ServiceEntity serviceEntity);

    List<Appointment> findByAppointmentTime(LocalDateTime appointmentTime);
}