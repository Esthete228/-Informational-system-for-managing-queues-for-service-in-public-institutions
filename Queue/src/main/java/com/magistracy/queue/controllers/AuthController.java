package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    // Сторінка аутентифікації
    @GetMapping("/auth")
    public String authPage() {
        return "auth";  // Повертаємо сторінку аутентифікації
    }

    // Перехід на сторінку логіна для працівників
    @GetMapping("/login-employee")
    public String employeeLoginPage() {
        return "login-employee";  // Повертаємо login-employee.html
    }

    // Перехід на сторінку логіна для клієнтів
    @GetMapping("/login-client")
    public String clientLoginPage() {
        return "login-client";  // Повертаємо login-client.html
    }

    // Обробка логіна працівника
    @PostMapping("/authenticate-employee")
    public ModelAndView authenticateEmployee(String username, String password) {
        // Логіка перевірки користувача (приклад для спрощення)
        if ("admin".equals(username) && "password".equals(password)) {
            return new ModelAndView("redirect:/admin-dashboard");  // Успішний вхід
        }
        return new ModelAndView("login-employee", "error", "Невірні облікові дані");
    }
}
