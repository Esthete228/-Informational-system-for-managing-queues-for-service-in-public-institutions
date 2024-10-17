package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Client;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public QueueController(QueueRepository queueRepository, ServiceEntityRepository serviceEntityRepository, ClientRepository clientRepository) {
        this.queueRepository = queueRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.clientRepository = clientRepository;
    }

    // Створення запису в черзі для користувача
    @PostMapping("/create")
    public ResponseEntity<String> createQueueEntry(@RequestParam Long userId, @RequestParam Long serviceId, @RequestParam String appointmentTime) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        ServiceEntity service = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));

        // Перевіряємо, чи вільний час
        LocalDateTime time = LocalDateTime.parse(appointmentTime);
        List<Queue> existingQueue = queueRepository.findByServiceEntityAndAppointmentTime(service, time);

        if (!existingQueue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Час зайнятий. Виберіть інший час.");
        }

        Queue newQueueEntry = new Queue();
        newQueueEntry.setUser(client);
        newQueueEntry.setServiceEntity(service);
        newQueueEntry.setAppointmentTime(time);

        queueRepository.save(newQueueEntry);

        return ResponseEntity.ok("Запис успішно створено на " + time);
    }

    // Отримання черги для певної послуги
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Queue>> getQueueForService(@PathVariable Long serviceId) {
        ServiceEntity serviceEntity = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));
        List<Queue> queueList = queueRepository.findByServiceEntity(serviceEntity);
        return ResponseEntity.ok(queueList);
    }

    // Видалення попереднього запису і перезапис на новий час
    @PostMapping("/reschedule")
    public ResponseEntity<String> rescheduleQueueEntry(@RequestParam Long queueId, @RequestParam String newAppointmentTime) {
        Queue existingQueueEntry = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Запис не знайдено"));

        LocalDateTime newTime = LocalDateTime.parse(newAppointmentTime);

        // Перевіряємо, чи вільний новий час
        List<Queue> conflictingEntries = queueRepository.findByServiceEntityAndAppointmentTime(existingQueueEntry.getServiceEntity(), newTime);

        if (!conflictingEntries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Новий час зайнятий. Виберіть інший.");
        }

        // Оновлюємо час запису
        existingQueueEntry.setAppointmentTime(newTime);
        queueRepository.save(existingQueueEntry);

        return ResponseEntity.ok("Запис оновлено на " + newTime);
    }

    // Видалення запису з черги
    @DeleteMapping("/delete/{queueId}")
    public ResponseEntity<String> deleteQueueEntry(@PathVariable Long queueId) {
        queueRepository.deleteById(queueId);
        return ResponseEntity.ok("Запис успішно видалено");
    }

    // Виклик клієнта до обслуговування
    @PostMapping("/call-client")
    public ResponseEntity<String> callNextClient(@RequestParam Long serviceId) {
        ServiceEntity service = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));
        List<Queue> queueList = queueRepository.findByServiceEntityOrderByAppointmentTimeAsc(service);

        if (queueList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Немає клієнтів у черзі");
        }

        Queue nextClient = queueList.get(0);
        // Логіка виклику клієнта
        queueRepository.delete(nextClient); // Видаляємо після обслуговування
        return ResponseEntity.ok("Клієнта " + nextClient.getUser().getUsername() + " викликано до обслуговування");
    }
}
