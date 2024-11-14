package com.magistracy.queue.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AESUtils {

    private static final String ALGORITHM = "AES";
    private static final String ENCODING = "UTF-8";

    // Generates a random 128-bit AES key
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128); // AES-128
        return keyGenerator.generateKey();
    }

    // Encrypts the OTP code with the provided AES key
    public static String encrypt(String otpCode, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(otpCode.getBytes(ENCODING));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypts the OTP code with the provided AES key
    public static String decrypt(String encryptedOtp, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedOtp));
        return new String(decryptedBytes, ENCODING);
    }
}