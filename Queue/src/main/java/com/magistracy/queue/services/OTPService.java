package com.magistracy.queue.services;

import com.magistracy.queue.entities.OtpCodeEntity;
import com.magistracy.queue.repositories.OtpCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);
    private final SecretKey secretKey;
    private final OtpCodeRepository otpCodeRepository;

    @Autowired
    public OTPService(OtpCodeRepository otpCodeRepository) throws Exception {
        this.secretKey = AESUtils.generateKey();  // Secret key for encryption and decryption
        this.otpCodeRepository = otpCodeRepository;
    }

    // Method to generate OTP
    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // Generate 6-digit OTP
    }

    // Encrypt OTP code and log both encrypted and decrypted version
    public String encryptAndLogOTP(String otpCode) throws Exception {
        // Encrypt OTP
        String encryptedOtp = AESUtils.encrypt(otpCode, secretKey);

        // Log the encrypted OTP and its decrypted value for debugging
        String decryptedOtp = AESUtils.decrypt(encryptedOtp, secretKey);

        // Log encrypted OTP and its decrypted version for debug purposes
        logger.debug("Encrypted OTP: {}", encryptedOtp);  // Log the encrypted OTP
        logger.debug("Decrypted OTP (for logging purposes): {}", decryptedOtp);  // Log the decrypted OTP for debugging

        return encryptedOtp;  // Return the encrypted OTP to be saved in the database
    }

    // Save OTP code to the database (encrypted)
    public void saveOtp(String phoneNumber, String otpCode) {
        try {
            String encryptedOtp = encryptAndLogOTP(otpCode);  // Encrypt OTP and log the info
            OtpCodeEntity otpCodeEntity = new OtpCodeEntity();
            otpCodeEntity.setPhoneNumber(phoneNumber);
            otpCodeEntity.setOtpCode(encryptedOtp);  // Save encrypted OTP to database
            otpCodeEntity.setExpirationTime(LocalDateTime.now().plusMinutes(5));  // Set expiration time (5 mins)
            otpCodeRepository.save(otpCodeEntity);  // Save OTP entity to the database
        } catch (Exception e) {
            logger.error("Error encrypting OTP for phone {}: {}", phoneNumber, e.getMessage());
        }
    }

    // Verify OTP by decrypting and comparing it
    public boolean verifyOtp(String phoneNumber, String otp) {
        try {
            Optional<OtpCodeEntity> otpCodeEntityOptional = otpCodeRepository.findByPhoneNumber(phoneNumber);

            if (otpCodeEntityOptional.isPresent()) {
                OtpCodeEntity otpCodeEntity = otpCodeEntityOptional.get();

                // Check if OTP has expired
                if (otpCodeEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
                    logger.debug("OTP for phone {} has expired.", phoneNumber);
                    return false; // OTP expired
                }

                // Decrypt the OTP from the database for comparison
                String decryptedOtp = AESUtils.decrypt(otpCodeEntity.getOtpCode(), secretKey);
                logger.debug("Decrypted OTP for phone {}: {}", phoneNumber, decryptedOtp);  // Log the decrypted OTP

                // Check if the entered OTP matches the decrypted OTP
                if (decryptedOtp.equals(otp)) {
                    // OTP matched, delete the OTP code from the database
                    otpCodeRepository.delete(otpCodeEntity);  // Delete the OTP from the database
                    logger.debug("OTP successfully verified and deleted for phone {}.", phoneNumber);
                    return true;  // OTP matched and deleted successfully
                }
            }

            return false;  // No OTP found for the phone number or OTP didn't match
        } catch (Exception e) {
            logger.error("Error verifying OTP for phone {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
}
