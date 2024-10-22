package com.magistracy.queue.services;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final ServiceEntityRepository serviceEntityRepository;
    private final WorkplaceRepository workplaceRepository;

    public QueueService(QueueRepository queueRepository, ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository) {
        this.queueRepository = queueRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
    }

    // Створення нового запису в черзі
    public Queue createQueueEntry(Client client, Long serviceId, Long workplaceId, LocalDateTime appointmentTime) {
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Робоче місце не знайдено"));

        Queue queue = new Queue();
        queue.setClient(client);
        queue.setServiceEntity(serviceEntityRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Послуга не знайдена")));
        queue.setWorkplace(workplace);
        queue.setAppointmentTime(appointmentTime);

        return queueRepository.save(queue);
    }
}
