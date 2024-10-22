package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceEntityRepository extends JpaRepository<ServiceEntity, Long> {
}
