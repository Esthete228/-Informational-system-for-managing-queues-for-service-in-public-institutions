package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.ServiceEntity;
import com.magistracy.queue.entities.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {
}