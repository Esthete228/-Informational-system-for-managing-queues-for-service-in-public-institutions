package com.magistracy.queue.services;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Метод для отримання всіх записів клієнта за його ID
    public List<Appointment> getAppointmentsByClient(Long clientId) {
        return appointmentRepository.findByClientId(clientId); // Передбачається, що цей метод є в репозиторії
    }

    // Метод для редагування запису
    public Appointment updateAppointment(Long appointmentId, Long clientId, LocalDateTime newAppointmentTime) {
        // Знаходимо запис за його ID і перевіряємо, що цей запис належить клієнту
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (appointmentOptional.isPresent()) {
            Appointment appointment = appointmentOptional.get();

            // Перевіряємо, чи збігається ID клієнта з тим, який робив запис
            if (!appointment.getClient().getId().equals(clientId)) {
                throw new RuntimeException("Ви не можете редагувати цей запис");
            }

            // Оновлюємо час запису
            appointment.setAppointmentTime(newAppointmentTime);

            // Зберігаємо зміни
            return appointmentRepository.save(appointment);
        } else {
            throw new RuntimeException("Запис не знайдений");
        }
    }

    // Метод для видалення запису
    public void deleteAppointment(Long appointmentId, Long clientId) {
        // Знаходимо запис за його ID і перевіряємо, що цей запис належить клієнту
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (appointmentOptional.isPresent()) {
            Appointment appointment = appointmentOptional.get();

            // Перевіряємо, чи збігається ID клієнта з тим, який робив запис
            if (!appointment.getClient().getId().equals(clientId)) {
                throw new RuntimeException("Ви не можете видаляти цей запис");
            }

            // Видаляємо запис
            appointmentRepository.delete(appointment);
        } else {
            throw new RuntimeException("Запис не знайдений");
        }
    }

    // Метод для бронювання запису
    public Appointment bookAppointment(ServiceEntity serviceEntity, Client client, LocalDateTime appointmentTime) {
        Appointment appointment = new Appointment();
        appointment.setServiceEntity(serviceEntity);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        return appointmentRepository.save(appointment);
    }
}