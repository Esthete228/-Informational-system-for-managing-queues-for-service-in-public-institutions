package com.magistracy.queue.services;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {

    private final QueueRepository queueRepository;

    private final ClientRepository clientRepository;

    private final ServiceEntityRepository serviceEntityRepository;

    private final WorkplaceRepository workplaceRepository;

    public QueueService(QueueRepository queueRepository, ClientRepository clientRepository, ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository) {
        this.queueRepository = queueRepository;
        this.clientRepository = clientRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
    }

    // Метод додавання клієнта до черги
    public Queue addClientToQueue(Long clientId, Long serviceId, Long workplaceId) {
        // Знаходимо клієнта, послугу та робоче місце
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клієнта не знайдено"));

        ServiceEntity serviceEntity = serviceEntityRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Послугу не знайдено"));

        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new RuntimeException("Робоче місце не знайдено"));

        // Створюємо новий запис у черзі
        Queue queue = new Queue();
        queue.setClient(client);
        queue.setServiceEntity(serviceEntity);
        queue.setWorkplace(workplace);
        queue.setAppointmentTime(LocalDateTime.now()); // Час додавання до черги

        return queueRepository.save(queue);
    }

    // Отримати чергу для робочого місця
    public List<Queue> getQueueByWorkplace(Long workplaceId) {
        return queueRepository.findByWorkplaceId(workplaceId);
    }

    // Виклик наступного клієнта
    public Queue callNextClient(Long workplaceId) {
        List<Queue> queueList = getQueueByWorkplace(workplaceId);
        if (queueList.isEmpty()) {
            throw new RuntimeException("Немає клієнтів у черзі");
        }
        return queueList.get(0);  // Перший клієнт у черзі
    }

    // Передати клієнта на інше робоче місце
    public void transferClient(Long queueId, Long newWorkplaceId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        Workplace newWorkplace = workplaceRepository.findById(newWorkplaceId)
                .orElseThrow(() -> new RuntimeException("Нове робоче місце не знайдено"));

        queue.setWorkplace(newWorkplace);
        queueRepository.save(queue);
    }

    // Завершити сеанс
    public void completeSession(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        queueRepository.delete(queue);  // Видалення клієнта з черги після завершення сеансу
    }
}
