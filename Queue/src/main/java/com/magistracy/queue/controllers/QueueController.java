package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.services.QueueService;
import com.magistracy.queue.services.WorkplaceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private final QueueService queueService;
    private final WorkplaceService workplaceService;

    public QueueController(QueueService queueService, WorkplaceService workplaceService) {
        this.queueService = queueService;
        this.workplaceService = workplaceService;
    }

    @PostMapping("/create-ticket/{serviceId}")
    public ResponseEntity<Queue> createTicket(@PathVariable Long serviceId) {
        // Знаходимо відповідне робоче місце для послуги
        Workplace workplace = workplaceService.findLeastLoadedWorkplaceForService(serviceId);

        if (workplace == null) {
            throw new IllegalArgumentException("Немає доступного робочого місця для цієї послуги.");
        }

        Queue queue = queueService.createTicket(serviceId, workplace.getId());
        return ResponseEntity.ok(queue);
    }

    @PutMapping("/update-ticket/{queueId}")
    public ResponseEntity<Queue> updateTicket(@PathVariable Long queueId, @RequestBody Map<String, Long> payload) {
        Long newServiceId = payload.get("newServiceId");

        // Знаходимо відповідне робоче місце для нової послуги
        Workplace workplace = workplaceService.findLeastLoadedWorkplaceForService(newServiceId);

        if (workplace == null) {
            throw new IllegalArgumentException("Немає доступного робочого місця для цієї послуги.");
        }

        Queue updatedQueue = queueService.updateTicket(queueId, newServiceId, workplace.getId());
        return ResponseEntity.ok(updatedQueue);
    }

    @DeleteMapping("/delete-ticket/{queueId}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long queueId) {
        queueService.deleteTicket(queueId);
        return ResponseEntity.ok("Талон успішно видалено");
    }

    @GetMapping("/current-queue/{workplaceId}")
    public ResponseEntity<List<Queue>> getCurrentQueue(@PathVariable Long workplaceId) {
        List<Queue> queue = queueService.getCurrentQueue(workplaceId);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/current-client/{workplaceId}")
    public ResponseEntity<Queue> getCurrentClient(@PathVariable Long workplaceId) {
        Queue currentClient = queueService.getCurrentClient(workplaceId);
        return ResponseEntity.ok(currentClient);
    }

    @GetMapping("/in-progress-queue")
    public ResponseEntity<List<Queue>> getInProgressQueue() {
        List<Queue> inProgressQueue = queueService.getInProgressTickets();
        return ResponseEntity.ok(inProgressQueue);
    }

    @GetMapping(value = "/queue-updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamQueueUpdates() {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (true) {
                    // Перевірка на зміни в черзі (змінюйте відповідно до вашої логіки)
                    List<Queue> updatedQueue = queueService.getInProgressTickets();
                    emitter.send(updatedQueue); // Надсилаємо дані черги
                    Thread.sleep(5000); // Перевіряємо зміни кожні 5 секунд (можна налаштувати)
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    @PostMapping("/call-next-client/{workplaceId}")
    public ResponseEntity<Queue> callNextClient(@PathVariable Long workplaceId) {
        Queue nextClient = queueService.callNextClient(workplaceId);
        return ResponseEntity.ok(nextClient);
    }

    @PutMapping("/transfer-client/{queueId}")
    public ResponseEntity<?> transferClient(@PathVariable Long queueId,
                                            @RequestBody Map<String, Long> payload) {
        Long toWorkplaceId = payload.get("toWorkplaceId");

        if (toWorkplaceId == null) {
            return ResponseEntity.badRequest().body("Відсутні необхідні дані для перенаправлення.");
        }

        try {
            Queue updatedQueue = queueService.transferClient(queueId, toWorkplaceId);
            return ResponseEntity.ok(updatedQueue);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/complete-session/{queueId}")
    public ResponseEntity<?> completeSession(@PathVariable Long queueId) {
        // Тепер параметр буде використовуватися
        try {
            // Викликаємо метод завершення сеансу
            queueService.completeSession(queueId);
            return ResponseEntity.ok("Сесію завершено успішно");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}