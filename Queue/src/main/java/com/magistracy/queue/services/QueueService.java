package com.magistracy.queue.services;

import com.magistracy.queue.entities.*;
import com.magistracy.queue.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final AppointmentRepository appointmentRepository;
    private final QueueRepository queueRepository;
    private final WorkplaceRepository workplaceRepository;
    private final ServiceWorkplaceRepository serviceWorkplaceRepository;

    private int ticketNumberCounter = 0;
    int ticketLimit = 5;

    @Autowired
    public QueueService(AppointmentRepository appointmentRepository, QueueRepository queueRepository,
                        WorkplaceRepository workplaceRepository,
                        ServiceWorkplaceRepository serviceWorkplaceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.queueRepository = queueRepository;
        this.workplaceRepository = workplaceRepository;
        this.serviceWorkplaceRepository = serviceWorkplaceRepository;
    }

    @Scheduled(cron = "0 * * * * *") // Виконання кожну хвилину
    public void autoMoveAppointmentsToQueue() {
        LocalDateTime now = LocalDateTime.now();

        // Отримуємо записи, у яких час настав або пройшов
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getAppointmentTime().isBefore(now) || appointment.getAppointmentTime().isEqual(now))
                .toList();

        if (appointments.isEmpty()) {
            System.out.println("Немає записів для переміщення на час: " + now);
            return;
        }

        for (Appointment appointment : appointments) {
            createQueueFromAppointment(appointment);
        }

        System.out.println("Автоматичне переміщення записів у чергу завершено.");
    }

    private void createQueueFromAppointment(Appointment appointment) {

        Queue queue = new Queue();
        queue.setServiceEntity(appointment.getServiceEntity());

        // Отримуємо ім'я клієнта з пов'язаного об'єкта Client
        String clientName = appointment.getClient().getUsername();
        queue.setClientName(clientName); // Збереження імені клієнта у черзі
        queue.setCreatedAt(appointment.getAppointmentTime()); // Використовуємо час із запису

        // Пошук відповідного робочого місця
        Workplace workplace = findLeastLoadedWorkplaceForService(appointment.getServiceEntity().getId());
        if (workplace == null || isWorkplaceOverloaded(workplace.getId(), ticketLimit)) {
            System.out.println("Робоче місце перевантажено або не знайдено для послуги " + appointment.getServiceEntity().getServiceName());
            return;
        }

        queue.setWorkplace(workplace);
        queue.setTicketNumber(generateTicketNumber());
        queue.setStatus(Queue.QueueStatus.ACTIVE);

        queueRepository.save(queue);
        appointmentRepository.delete(appointment);

        System.out.println("Талон створено для клієнта " + clientName + " на час " + queue.getCreatedAt());
    }


    public List<Queue> getCurrentQueue(Long workplaceId) {
        return queueRepository.findByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE);
    }

    public List<Queue> getInProgressTickets() {
        List<Queue> inProgressTickets = queueRepository.findByStatus(Queue.QueueStatus.IN_PROGRESS);
        inProgressTickets.forEach(queue -> queue.getWorkplace().getWorkplaceName());
        return inProgressTickets;
    }

    public Queue getCurrentClient(Long workplaceId) {
        return queueRepository.findFirstByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.IN_PROGRESS)
                .orElse(null);
    }

    // Перевірка, чи перевищено ліміт для робочого місця
    private boolean isWorkplaceOverloaded(Long workplaceId, int limit) {
        int ticketCount = queueRepository.findByWorkplaceIdAndStatus(workplaceId, Queue.QueueStatus.ACTIVE).size();
        return ticketCount >= limit;
    }

    public Workplace findLeastLoadedWorkplaceForService(Long serviceId) {
        // Get all workplaces
        List<Workplace> workplaces = workplaceRepository.findAll();

        // Get service-workplace associations
        List<ServiceWorkplace> serviceWorkplaces = serviceWorkplaceRepository.findByServiceId(serviceId);

        // Find workplaces that are associated with the service
        List<Workplace> availableWorkplaces = workplaces.stream()
                .filter(workplace -> serviceWorkplaces.stream()
                        .anyMatch(serviceWorkplace -> serviceWorkplace.getWorkplace().getId().equals(workplace.getId())))
                .filter(workplace -> !isWorkplaceOverloaded(workplace.getId(), ticketLimit)) // Overload check
                .toList();

        System.out.println("Available workplaces for service " + serviceId + ": " + availableWorkplaces.size());

        if (availableWorkplaces.isEmpty()) {
            return null; // No available workplaces
        }

        return availableWorkplaces.stream()
                .min(Comparator.comparingInt(wp -> queueRepository.findByWorkplaceIdAndStatus(wp.getId(), Queue.QueueStatus.ACTIVE).size()))
                .orElseThrow(() -> new RuntimeException("No available workplaces for this service"));
    }

    // Створення талону для послуги і робочого місця
    public Queue createTicket(Long serviceId, Long workplaceId) {
        Optional<ServiceWorkplace> serviceWorkplace = serviceWorkplaceRepository
                .findByServiceIdAndWorkplaceId(serviceId, workplaceId);

        if (serviceWorkplace.isEmpty()) {
            throw new IllegalArgumentException("Немає доступного робочого місця для цієї послуги.");
        }

        Queue queue = new Queue();
        queue.setServiceEntity(serviceWorkplace.get().getService());
        queue.setWorkplace(serviceWorkplace.get().getWorkplace());
        queue.setStatus(Queue.QueueStatus.ACTIVE);
        queue.setTicketNumber(generateTicketNumber());

        return queueRepository.save(queue);
    }

    private int generateTicketNumber() {
        return ++ticketNumberCounter;
    }

    // Оновлення талону
    public Queue updateTicket(Long queueId, Long newServiceId, Long newWorkplaceId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Талон не знайдено"));

        Optional<ServiceWorkplace> serviceWorkplace = serviceWorkplaceRepository
                .findByServiceIdAndWorkplaceId(newServiceId, newWorkplaceId);

        if (serviceWorkplace.isEmpty()) {
            throw new IllegalArgumentException("Немає доступного робочого місця для цієї послуги.");
        }

        queue.setServiceEntity(serviceWorkplace.get().getService());
        queue.setWorkplace(serviceWorkplace.get().getWorkplace());

        return queueRepository.save(queue);
    }

    public void deleteTicket(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Талон не знайдено"));
        queueRepository.delete(queue);
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

    public Queue transferClient(Long queueId, Long toWorkplaceId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        Workplace newWorkplace = workplaceRepository.findById(toWorkplaceId)
                .orElseThrow(() -> new RuntimeException("Нове робоче місце не знайдено"));

        queue.setWorkplace(newWorkplace);
        queue.setStatus(Queue.QueueStatus.ACTIVE);

        // Логіка для перенаправлення клієнта (наприклад, зміна статусу або іншої інформації)
        return queueRepository.save(queue);
    }

    public void completeSession(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Талон не знайдено"));

        if (queue.getStatus() != Queue.QueueStatus.IN_PROGRESS) {
            throw new RuntimeException("Можна завершувати тільки сесії зі статусом IN_PROGRESS");
        }

        queue.setStatus(Queue.QueueStatus.COMPLETED);
        queueRepository.save(queue);
    }
}