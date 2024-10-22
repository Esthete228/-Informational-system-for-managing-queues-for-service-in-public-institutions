package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/add-to-queue")
    public ResponseEntity<Queue> addToQueue(@RequestParam Long clientId, @RequestParam Long serviceId, @RequestParam Long workplaceId) {
        Queue newQueue = queueService.addClientToQueue(clientId, serviceId, workplaceId);
        return ResponseEntity.ok(newQueue);
    }

    @GetMapping("/workplace/{workplaceId}")
    public ResponseEntity<List<Queue>> getQueueByWorkplace(@PathVariable Long workplaceId) {
        List<Queue> queueList = queueService.getQueueByWorkplace(workplaceId);
        return ResponseEntity.ok(queueList);
    }

    @PostMapping("/call-next-client/{workplaceId}")
    public ResponseEntity<Queue> callNextClient(@PathVariable Long workplaceId) {
        Queue nextClient = queueService.callNextClient(workplaceId);
        return ResponseEntity.ok(nextClient);
    }

    @PostMapping("/transfer-client/{queueId}")
    public ResponseEntity<String> transferClient(@PathVariable Long queueId, @RequestParam Long newWorkplaceId) {
        queueService.transferClient(queueId, newWorkplaceId);
        return ResponseEntity.ok("Клієнта передано на інше робоче місце");
    }

    @PostMapping("/complete-session/{queueId}")
    public ResponseEntity<String> completeSession(@PathVariable Long queueId) {
        queueService.completeSession(queueId);
        return ResponseEntity.ok("Сеанс завершено");
    }
}
