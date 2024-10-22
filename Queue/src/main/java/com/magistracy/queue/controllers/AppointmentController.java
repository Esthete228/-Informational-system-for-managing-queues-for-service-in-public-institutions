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
import java.util.List;
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

    @GetMapping("/appointment")
    public String showBookingPage(Model model, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");

        if (clientId == null) {
            throw new RuntimeException("Клієнт не авторизований");
        }

        model.addAttribute("clientId", clientId);
        return "appointment"; // Назва HTML шаблону
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Long clientId = (Long) session.getAttribute("clientId");

            if (clientId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Клієнт не авторизований");
            }

            Long serviceId = Long.valueOf((String) request.get("serviceId"));
            String appointmentTimeStr = (String) request.get("appointmentTime");
            LocalDateTime appointmentTime = LocalDateTime.parse(appointmentTimeStr);

            ServiceEntity service = serviceEntityRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Служба не знайдена"));
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Клієнт не знайдений"));

            Appointment appointment = appointmentService.bookAppointment(service, client, appointmentTime);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при бронюванні: " + e.getMessage());
        }
    }

    @GetMapping("/client-appointments")
    public ResponseEntity<List<Appointment>> getClientAppointments(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByClient(clientId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/update/{appointmentId}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, Object> request,
            HttpSession session
    ) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Клієнт не авторизований");
        }

        String newAppointmentTimeStr = (String) request.get("newAppointmentTime");
        LocalDateTime newAppointmentTime = LocalDateTime.parse(newAppointmentTimeStr);

        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, clientId, newAppointmentTime);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при оновленні: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Клієнт не авторизований");
        }

        try {
            appointmentService.deleteAppointment(appointmentId, clientId);
            return ResponseEntity.ok("Запис видалено");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка при видаленні: " + e.getMessage());
        }
    }
}