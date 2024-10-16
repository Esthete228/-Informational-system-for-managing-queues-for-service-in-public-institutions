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

    private final OtpCodeRepository otpCodeRepository;

    public OTPService(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }

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
        Optional<OtpCodeEntity> otpRecord = otpCodeRepository.findByPhoneNumber(phoneNumber);
        if (otpRecord.isPresent()) {
            OtpCodeEntity otpCodeEntity = otpRecord.get();
            boolean isValid = otpCodeEntity.getOtpCode().equals(otpCode) &&
                    otpCodeEntity.getExpirationTime().isAfter(LocalDateTime.now());

            if (isValid) {
                // Видаляємо OTP-код після успішної перевірки
                otpCodeRepository.delete(otpCodeEntity);
                return true;
            }
        }
        return false;  // Код не знайдено або прострочений
    }
}
