package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Client;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private final QueueRepository queueRepository;
    private final ServiceEntityRepository serviceEntityRepository;
    private final ClientRepository clientRepository;
    private final WorkplaceRepository workplaceRepository;

    public QueueController(QueueRepository queueRepository, ServiceEntityRepository serviceEntityRepository, ClientRepository clientRepository, WorkplaceRepository workplaceRepository) {
        this.queueRepository = queueRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.clientRepository = clientRepository;
        this.workplaceRepository = workplaceRepository;
    }

    // Створення запису в черзі для користувача
    @PostMapping("/create-queue")
    public ResponseEntity<String> createQueueEntry(@RequestParam Long userId, @RequestParam Long serviceId, @RequestParam Long workplaceId, @RequestParam String appointmentTime) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        LocalDateTime time = LocalDateTime.parse(appointmentTime);

        Queue newQueueEntry = new Queue();
        newQueueEntry.setClient(client);
        newQueueEntry.setServiceEntity(serviceEntityRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Послугу не знайдено")));
        newQueueEntry.setWorkplace(workplaceRepository.findById(workplaceId).orElseThrow(() -> new RuntimeException("Робоче місце не знайдено")));
        newQueueEntry.setAppointmentTime(time);

        queueRepository.save(newQueueEntry);

        return ResponseEntity.ok("Запис успішно створено на " + time);
    }
}
