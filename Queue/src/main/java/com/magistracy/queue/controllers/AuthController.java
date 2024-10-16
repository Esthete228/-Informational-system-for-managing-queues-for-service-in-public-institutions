package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.repositories.EmployeeRepository;
import com.magistracy.queue.services.OTPService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository; // Для отримання даних про працівників

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OTPService otpService;
    // Перехід на сторінку логіну для працівників
    @GetMapping("/login-employee")
    public String employeeLoginPage() {
        return "login-employee";  // Повертаємо login-employee.html
    }

    // Перехід на сторінку логіна для клієнтів
    @GetMapping("/login-client")
    public String clientLoginPage() {
        return "login-client";  // Повертаємо login-client.html
    }

    // Перехід на dashboard для працівників після успішної авторизації
    @PostMapping("/authenticate-employee")
    public ModelAndView authenticateEmployee(String username, String password) {
        Optional<Employee> employee = employeeRepository.findByUsername(username);

        // Перевірка, чи працівник існує та чи вірний пароль
        if (employee.isPresent() && employee.get().getPassword().equals(password)) {
            String role = employee.get().getRole();
            if ("admin".equals(role)) {
                return new ModelAndView("redirect:/admin-dashboard");  // Адміністратор
            } else if ("employee".equals(role)) {
                return new ModelAndView("redirect:/employee-dashboard");  // Працівник
            }
        }

        return new ModelAndView("login-employee", "error", "Невірні облікові дані"); // Помилка
    }

    @PostMapping("/authenticate-client")
    public ModelAndView authenticateClient(String phoneNumber, String otp, HttpSession session) {
        Client client = clientRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Клієнт не знайдений"));

        if (otpService.verifyOtp(phoneNumber, otp)) {
            session.setAttribute("clientId", client.getId());  // Зберігаємо clientId у сесії
            return new ModelAndView("redirect:/client-dashboard");
        } else {
            ModelAndView mav = new ModelAndView("login-client");
            mav.addObject("error", "Невірний OTP код");
            return mav;
        }
    }
}

