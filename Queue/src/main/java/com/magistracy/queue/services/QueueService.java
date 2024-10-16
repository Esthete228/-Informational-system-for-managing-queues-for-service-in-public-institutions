package com.magistracy.queue.services;

import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.User;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QueueService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ServiceEntityRepository serviceEntityRepository;

    // Створення нового запису в черзі
    public Queue createQueueEntry(User user, Long serviceId, LocalDateTime appointmentTime) {
        Service service = (Service) serviceEntityRepository.findById(serviceId).orElseThrow(() -> new RuntimeException("Послугу не знайдено"));

        Queue queue = new Queue();
        queue.setUser(user);
        queue.setServiceEntity((com.magistracy.queue.entities.ServiceEntity) service);
        queue.setAppointmentTime(appointmentTime);

        return queueRepository.save(queue);
    }

    // Зміна часу запису в черзі
    public Queue rescheduleQueueEntry(Long queueId, LocalDateTime newTime) {
        Queue queue = queueRepository.findById(queueId).orElseThrow(() -> new RuntimeException("Запис не знайдено"));
        queue.setAppointmentTime(newTime);

        return queueRepository.save(queue);
    }

    // Видалення запису в черзі
    public void deleteQueueEntry(Long queueId) {
        queueRepository.deleteById(queueId);
    }
}
