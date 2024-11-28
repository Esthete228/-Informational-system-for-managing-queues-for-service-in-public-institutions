package com.magistracy.queue.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "service_workplace" , uniqueConstraints = @UniqueConstraint(columnNames = {"service_id", "workplace_id"}))
public class ServiceWorkplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}
