package com.magistracy.queue.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workplaceName;

    // Можливо, додати відслідковування поточного клієнта
    @OneToOne
    private Queue currentQueue;  // Поточний клієнт на обслуговуванні

    // Геттери та сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkplaceName() {
        return workplaceName;
    }

    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
    }

    public Queue getCurrentQueue() {
        return currentQueue;
    }

    public void setCurrentQueue(Queue currentQueue) {
        this.currentQueue = currentQueue;
    }
}
