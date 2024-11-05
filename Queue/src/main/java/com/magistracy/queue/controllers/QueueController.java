package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.services.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/create-ticket")
    public ResponseEntity<Queue> createTicket(@RequestBody Map<String, Long> payload) {
        Long serviceId = payload.get("serviceId");
        Long workplaceId = payload.get("workplaceId");
        Queue newQueue = queueService.createTicket(serviceId, workplaceId);
        return ResponseEntity.ok(newQueue);
    }

    @PutMapping("/update-ticket/{queueId}")
    public ResponseEntity<Queue> updateTicket(@PathVariable Long queueId, @RequestBody Map<String, Long> payload) {
        Long newServiceId = payload.get("newServiceId");
        Long newWorkplaceId = payload.get("newWorkplaceId");

        Queue updatedQueue = queueService.updateTicket(queueId, newServiceId, newWorkplaceId);
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

    @PostMapping("/call-next-client/{workplaceId}")
    public ResponseEntity<Queue> callNextClient(@PathVariable Long workplaceId) {
        Queue nextClient = queueService.callNextClient(workplaceId);
        return ResponseEntity.ok(nextClient);
    }

    @PutMapping("/transfer-client/{queueId}")
    public ResponseEntity<?> transferClient(@PathVariable Long queueId,
                                            @RequestBody Map<String, Long> payload) {
        Long toWorkplaceId = payload.get("toWorkplaceId");
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