package com.magistracy.queue.services;

import com.magistracy.queue.entities.*;
import com.magistracy.queue.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final AppointmentRepository appointmentRepository;
    private final QueueRepository queueRepository;
    private final WorkplaceRepository workplaceRepository;
    private final ServiceWorkplaceRepository serviceWorkplaceRepository;
    private final WorkplaceService workplaceService;  // Додаємо WorkplaceService для роботи з робочими місцями

    private int ticketNumberCounter = 0;

    @Autowired
    public QueueService(AppointmentRepository appointmentRepository, QueueRepository queueRepository,
                        WorkplaceRepository workplaceRepository,
                        ServiceWorkplaceRepository serviceWorkplaceRepository, WorkplaceService workplaceService) {
        this.appointmentRepository = appointmentRepository;
        this.queueRepository = queueRepository;
        this.workplaceRepository = workplaceRepository;
        this.serviceWorkplaceRepository = serviceWorkplaceRepository;
        this.workplaceService = workplaceService;
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

        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentTime().isAfter(startOfDay) && a.getAppointmentTime().isBefore(endOfDay))
                .toList();

        if (appointments.isEmpty()) {
            System.out.println("Немає записів для переміщення на дату: " + date);
            return;
        }

        int ticketLimit = 5;

        for (Appointment appointment : appointments) {
            Queue queue = new Queue();
            queue.setServiceEntity(appointment.getServiceEntity());

            // Перевірка завантаженості та вибір робочого місця для послуги
            Workplace workplace = findLeastLoadedWorkplaceForService(appointment.getServiceEntity().getId());
            if (workplace == null) {
                System.out.println("Не знайдено доступного робочого місця для послуги " + appointment.getServiceEntity().getServiceName());
                continue; // Пропускаємо запис, якщо немає доступного робочого місця
            }

            // Перевіряємо, чи не перевищено ліміт для робочого місця
            if (isWorkplaceOverloaded(workplace.getId(), ticketLimit)) {
                workplace = findLeastLoadedWorkplaceForService(appointment.getServiceEntity().getId());
            }

            queue.setWorkplace(workplace);
            queue.setTicketNumber(generateTicketNumber());
            queue.setStatus(Queue.QueueStatus.ACTIVE);

            queueRepository.save(queue);
            appointmentRepository.delete(appointment);
        }
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
        // Отримуємо робочі місця, які можуть обслуговувати цю послугу
        List<Workplace> workplaces = workplaceRepository.findAll();

        // Фільтруємо робочі місця, які підтримують цю послугу
        List<ServiceWorkplace> serviceWorkplaces = serviceWorkplaceRepository.findByServiceId(serviceId);

        // Знаходимо робочі місця, пов'язані з послугою
        List<Workplace> availableWorkplaces = workplaces.stream()
                .filter(workplace -> serviceWorkplaces.stream()
                        .anyMatch(serviceWorkplace -> serviceWorkplace.getWorkplace().equals(workplace)))
                .toList();

        if (availableWorkplaces.isEmpty()) {
            return null; // Якщо немає доступних робочих місць для цієї послуги
        }

        // Повертаємо робоче місце з мінімальним навантаженням
        return availableWorkplaces.stream()
                .min(Comparator.comparingInt(wp -> queueRepository.findByWorkplaceIdAndStatus(wp.getId(), Queue.QueueStatus.ACTIVE).size()))
                .orElseThrow(() -> new RuntimeException("Робочі місця для цієї послуги недоступні"));
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