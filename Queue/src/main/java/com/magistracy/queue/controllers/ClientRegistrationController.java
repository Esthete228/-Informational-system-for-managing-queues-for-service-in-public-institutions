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

    @Autowired
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
        // Перевірка наявності клієнта з таким же номером телефону
        if (clientRepository.findByPhoneNumber(phone).isPresent()) {
            return new ModelAndView("registration-client", "error", "Клієнт з таким номером телефону вже зареєстрований.");
        }

        // Створюємо нового користувача з роллю "client"
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
