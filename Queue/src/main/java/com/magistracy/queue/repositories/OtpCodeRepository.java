package com.magistracy.queue.repositories;

import com.magistracy.queue.entities.OtpCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCodeEntity, Long> {
    Optional<OtpCodeEntity> findByPhoneNumber(String phoneNumber);
}
