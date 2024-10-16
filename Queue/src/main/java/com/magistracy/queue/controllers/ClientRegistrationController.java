package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.User;
import com.magistracy.queue.repositories.UserRepository;
import com.magistracy.queue.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClientRegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @GetMapping("/register-client")
    public String showRegistrationForm() {
        return "registration-client";  // Відображаємо сторінку реєстрації
    }

    @PostMapping("/register-client")
    public ModelAndView registerClient(@RequestParam String name, @RequestParam String phone) {
        // Створюємо нового користувача з роллю "user"
        User user = new User();
        user.setUsername(name);
        user.setPhoneNumber(phone);
        user.setRole("user");
        userRepository.save(user);

        // Генеруємо та відправляємо OTP
        String otpCode = otpService.generateOTP();
        otpService.saveOtp(phone, otpCode);  // Зберігаємо OTP

        return new ModelAndView("otp-verification-reg", "phone", phone);
    }
}

