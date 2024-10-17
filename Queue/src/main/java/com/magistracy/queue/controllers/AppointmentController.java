package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Client;
import com.magistracy.queue.repositories.AppointmentRepository;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.services.AppointmentService;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ServiceEntityRepository serviceEntityRepository;
    private final ClientRepository clientRepository;

    public AppointmentController(AppointmentService appointmentService, ServiceEntityRepository serviceEntityRepository, ClientRepository clientRepository) {
        this.appointmentService = appointmentService;
        this.serviceEntityRepository = serviceEntityRepository;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/book-appointment")
    public String showBookingPage(Model model, HttpSession session) {
        // Отримання ID клієнта із сесії
        Long clientId = (Long) session.getAttribute("clientId");

        if (clientId == null) {
            throw new RuntimeException("Клієнт не авторизований");
        }

        model.addAttribute("clientId", clientId); // Передаємо clientId у модель
        return "book-appointment"; // Назва HTML шаблону для сторінки бронювання
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            // Отримуємо clientId з сесії, а не з запиту
            Long clientId = (Long) session.getAttribute("clientId");

            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Клієнт не авторизований");
            }

            // Витягуємо інші параметри з запиту
            Long serviceId = Long.valueOf((String) request.get("serviceId"));
            String appointmentTimeStr = (String) request.get("appointmentTime");
            LocalDateTime appointmentTime = LocalDateTime.parse(appointmentTimeStr);

            // Логування отриманих значень
            System.out.println("Received booking request: serviceId=" + serviceId + ", clientId=" + clientId + ", appointmentTime=" + appointmentTime);

            // Знаходимо службу та клієнта за ID
            ServiceEntity service = serviceEntityRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Служба не знайдена"));
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Клієнт не знайдений"));

            // Логіка для бронювання запису
            Appointment appointment = appointmentService.bookAppointment(service, client, appointmentTime);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при бронюванні: " + e.getMessage());
        }
    }
}
