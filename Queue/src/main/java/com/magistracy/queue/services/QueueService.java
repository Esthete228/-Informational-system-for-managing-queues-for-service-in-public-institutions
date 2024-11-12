// QueueService.java

package com.magistracy.queue.services;

import com.magistracy.queue.entities.Appointment;
import com.magistracy.queue.entities.Queue;
import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.repositories.AppointmentRepository;
import com.magistracy.queue.repositories.QueueRepository;
import com.magistracy.queue.repositories.ServiceEntityRepository;
import com.magistracy.queue.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final AppointmentRepository appointmentRepository;
    private final QueueRepository queueRepository;
    private final ServiceEntityRepository serviceEntityRepository;
    private final WorkplaceRepository workplaceRepository;
    private int ticketNumberCounter = 0;

    @Autowired
    public QueueService(AppointmentRepository appointmentRepository, QueueRepository queueRepository,
                        ServiceEntityRepository serviceEntityRepository, WorkplaceRepository workplaceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.queueRepository = queueRepository;
        this.serviceEntityRepository = serviceEntityRepository;
        this.workplaceRepository = workplaceRepository;
    }

    // Автоматичне переміщення попередніх записів у чергу
    @Scheduled(cron = "0 0/1 * * * *") // Що 15 хвилин
    public void autoMoveAppointmentsToQueue() {
        LocalDate today = LocalDate.now();
        moveAppointmentsToQueue(today);
        System.out.println("Автоматичне переміщення записів у чергу завершено.");
    }

    public void moveAppointmentsToQueue(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Отримуємо всі записи на вказану дату
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentTime().isAfter(startOfDay) && a.getAppointmentTime().isBefore(endOfDay))
                .toList();

        if (appointments.isEmpty()) {
            System.out.println("Немає записів для переміщення на дату: " + date);
            return;
        }

        for (Appointment appointment : appointments) {
            Queue queue = new Queue();
            queue.setServiceEntity(appointment.getServiceEntity());

            // Вибір першого доступного робочого місця
            Workplace workplace = workplaceRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Робочі місця відсутні"));
            queue.setWorkplace(workplace);
            queue.setTicketNumber(generateTicketNumber());
            queue.setStatus(Queue.QueueStatus.ACTIVE);

            // Зберігаємо талон у таблиці queue
            queueRepository.save(queue);

            // Видаляємо запис з appointments
            appointmentRepository.delete(appointment);
        }
    }

    public List<Queue> getCurrentQueue(Long workplaceId) {
        // Fetch only ACTIVE status tickets for the specified workplace
        return queueRepository.findByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE);
    }

    public List<Queue> getInProgressTickets() {
        List<Queue> inProgressTickets = queueRepository.findByStatus(Queue.QueueStatus.IN_PROGRESS);
        inProgressTickets.forEach(queue -> queue.getWorkplace().getWorkplaceName()); // Завантаження імені робочого місця
        return inProgressTickets;
    }

    public Queue getCurrentClient(Long workplaceId) {
        // Fetch the current client in IN_PROGRESS status for the specified workplace
        return queueRepository.findFirstByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.IN_PROGRESS)
                .orElse(null);
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

    public Queue callNextClient(Long workplaceId) {
        Queue currentClient = queueRepository.findFirstByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE)
                .orElse(null);
        if (currentClient != null) {
            currentClient.setStatus(Queue.QueueStatus.IN_PROGRESS);
            return queueRepository.save(currentClient);
        }
        throw new RuntimeException("Клієнтів немає в черзі");
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
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        if (queue.getStatus() != Queue.QueueStatus.IN_PROGRESS) {
            throw new RuntimeException("Можна завершувати тільки сесії зі статусом IN_PROGRESS");
        }

        queue.setStatus(Queue.QueueStatus.COMPLETED); // Повертаємо статус "ACTIVE" для нового робочого місця
        queueRepository.save(queue); // Видаляємо талон при завершенні сеансу
    }

    private int generateTicketNumber() {
        return ++ticketNumberCounter;
    }
}