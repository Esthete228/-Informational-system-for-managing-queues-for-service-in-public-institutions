package com.magistracy.queue.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;  // Назва послуги
    private String serviceDescription;  // Опис послуги

    @ElementCollection
    @CollectionTable(name = "service_entity_assigned_workplaces", joinColumns = @JoinColumn(name = "service_entity_id"))
    @Column(name = "assigned_workplaces")
    private List<String> assignedWorkplaces;  // Список робочих місць

    // Геттери та сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public List<String> getAssignedWorkplaces() {
        return assignedWorkplaces;
    }

    public void setAssignedWorkplaces(List<String> assignedWorkplaces) {
        this.assignedWorkplaces = assignedWorkplaces;
    }

}
