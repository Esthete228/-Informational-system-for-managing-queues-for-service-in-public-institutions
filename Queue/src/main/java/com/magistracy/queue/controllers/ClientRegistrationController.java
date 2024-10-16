package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClientRegistrationController {

    private final ClientRepository clientRepository;

    private final OTPService otpService;

    public ClientRegistrationController(ClientRepository clientRepository, OTPService otpService) {
        this.clientRepository = clientRepository;
        this.otpService = otpService;
    }

    @GetMapping("/register-client")
    public String showRegistrationForm() {
        return "registration-client";  // Відображаємо сторінку реєстрації
    }

    @PostMapping("/register-client")
    public ModelAndView registerClient(@RequestParam String name, @RequestParam String phone) {
        // Створюємо нового користувача з роллю "user"
        Client client = new Client();
        client.setUsername(name);
        client.setPhoneNumber(phone);
        client.setRole("client");
        clientRepository.save(client);

        // Генеруємо та відправляємо OTP
        String otpCode = otpService.generateOTP();
        otpService.saveOtp(phone, otpCode);  // Зберігаємо OTP

        return new ModelAndView("otp-verification-reg", "phone", phone);
    }
}

