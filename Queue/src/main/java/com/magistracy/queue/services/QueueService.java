// QueueService.java

package com.magistracy.queue.services;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final ServiceEntityRepository serviceEntityRepository;
    private final WorkplaceRepository workplaceRepository;
    private int ticketNumberCounter = 0;

    @Autowired
    public QueueService(QueueRepository queueRepository,
                        ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository) {
        this.queueRepository = queueRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
    }

    public Queue createTicket(Long serviceId, Long workplaceId) {
        ServiceEntity serviceEntity = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послуга не знайдена"));
        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Робоче місце не знайдено"));

        Queue queue = new Queue();
        queue.setServiceEntity(serviceEntity);
        queue.setWorkplace(workplace);
        queue.setTicketNumber(generateTicketNumber());
        queue.setStatus(Queue.QueueStatus.ACTIVE);

        return queueRepository.save(queue);
    }

    public List<Queue> getCurrentQueue(Long workplaceId) {
        // Fetch only ACTIVE status tickets for the specified workplace
        return queueRepository.findByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE);
    }

    public Queue getCurrentClient(Long workplaceId) {
        // Fetch the current client in IN_PROGRESS status for the specified workplace
        return queueRepository.findFirstByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.IN_PROGRESS)
                .orElse(null);
    }

    public Queue callNextClient(Long workplaceId) {
        Queue currentClient = queueRepository.findFirstByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE)
                .orElse(null);
        if (currentClient != null) {
            currentClient.setStatus(Queue.QueueStatus.IN_PROGRESS);
            return queueRepository.save(currentClient);
        }
        throw new RuntimeException("Клієнтів немає в черзі");
    }

    public Queue updateTicket(Long ticketId, Long newServiceId, Long newWorkplaceId) {
        Queue ticket = queueRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        if (newServiceId != null) {
            ServiceEntity newServiceEntity = serviceEntityRepository.findById(newServiceId)
                    .orElseThrow(() -> new RuntimeException("Нова послуга не знайдена"));
            ticket.setServiceEntity(newServiceEntity);
        }

        if (newWorkplaceId != null) {
            Workplace newWorkplace = workplaceRepository.findById(newWorkplaceId)
                    .orElseThrow(() -> new RuntimeException("Нове робоче місце не знайдено"));
            ticket.setWorkplace(newWorkplace);
        }

        return queueRepository.save(ticket);
    }

    public void deleteTicket(Long ticketId) {
        if (queueRepository.existsById(ticketId)) {
            queueRepository.deleteById(ticketId);
        } else {
            throw new RuntimeException("Талон не знайдено");
        }
    }

    public Queue transferClient(Long queueId, Long newWorkplaceId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        Workplace newWorkplace = workplaceRepository.findById(newWorkplaceId)
                .orElseThrow(() -> new RuntimeException("Нове робоче місце не знайдено"));

        queue.setWorkplace(newWorkplace);
        queue.setStatus(Queue.QueueStatus.ACTIVE); // Повертаємо статус "ACTIVE" для нового робочого місця
        return queueRepository.save(queue);
    }

    public void completeSession(Long queueId) {
        if (queueRepository.existsById(queueId)) {
            queueRepository.deleteById(queueId); // Видаляємо талон при завершенні сеансу
        } else {
            throw new RuntimeException("Талон не знайдено");
        }
    }

    private int generateTicketNumber() {
        return ++ticketNumberCounter;
    }
}