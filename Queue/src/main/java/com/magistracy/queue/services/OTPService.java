package com.magistracy.queue.services;

import com.magistracy.queue.entities.OtpCodeEntity;
import com.magistracy.queue.repositories.OtpCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    // Генерація одноразового коду
    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));  // 6-значний код
    }

    // Збереження OTP в базу даних
    public void saveOtp(String phoneNumber, String otpCode) {
        OtpCodeEntity otpCodeEntity = new OtpCodeEntity();
        otpCodeEntity.setPhoneNumber(phoneNumber);
        otpCodeEntity.setOtpCode(otpCode);
        otpCodeEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5));  // Код дійсний 5 хвилин

        otpCodeRepository.save(otpCodeEntity);
    }

    // Перевірка OTP
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        System.out.println("Перевірка OTP для номера: " + phoneNumber);
        Optional<OtpCodeEntity> otpRecord = otpCodeRepository.findByPhoneNumber(phoneNumber);
        if (otpRecord.isPresent()) {
            OtpCodeEntity otpCodeEntity = otpRecord.get();
            return otpCodeEntity.getOtpCode().equals(otpCode) &&
                    otpCodeEntity.getExpirationTime().isAfter(LocalDateTime.now());
        }
        return false;  // Код не знайдено або прострочений
    }
}
