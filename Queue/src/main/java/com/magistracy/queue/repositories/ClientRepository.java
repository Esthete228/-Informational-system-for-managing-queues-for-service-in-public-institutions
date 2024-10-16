package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsername(String username);

    Optional<Client> findByPhoneNumber(String phoneNumber);
}