package com.magistracy.queue.services;

import com.magistracy.queue.entities.OtpCodeEntity;
import com.magistracy.queue.repositories.OtpCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtpCleanupService {

    private final OtpCodeRepository otpCodeRepository;

    public OtpCleanupService(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }

    // Метод, який буде виконуватись кожні 5 хвилин
    @Scheduled(fixedRate = 300000)  // 5 хвилин
    public void deleteExpiredOtps() {
        List<OtpCodeEntity> expiredOtps = otpCodeRepository.findAll()
                .stream()
                .filter(otp -> otp.getExpirationTime().isBefore(LocalDateTime.now()))
                .toList();
        otpCodeRepository.deleteAll(expiredOtps);
    }
}
