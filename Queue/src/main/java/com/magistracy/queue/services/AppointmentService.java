package com.magistracy.queue.services;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Client;  // Залежить від вашої реалізації Client
import com.magistracy.queue.repositories.AppointmentRepository;  // Створіть AppointmentRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment bookAppointment(ServiceEntity service, Client client, LocalDateTime time) {
        // Перевірка, чи вже є запис на цю ж годину
        boolean exists = appointmentRepository.existsByServiceAndAppointmentTime(service, time);
        if (exists) {
            throw new RuntimeException("Ця година вже зайнята");
        }

        Appointment appointment = new Appointment();
        appointment.setService(service);
        appointment.setClient(client);
        appointment.setAppointmentTime(time);
        return appointmentRepository.save(appointment);
    }
}
