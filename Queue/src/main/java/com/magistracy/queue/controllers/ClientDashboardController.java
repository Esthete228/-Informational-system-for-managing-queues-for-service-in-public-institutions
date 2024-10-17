package com.magistracy.queue.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientDashboardController {

    @GetMapping("/client-dashboard")
    public String showClientDashboard(HttpSession session) {
        // Перевірка, чи клієнт авторизований
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return "redirect:/login-client";  // Якщо не авторизований, перенаправляємо на сторінку логіну
        }
        return "client-dashboard";  // Повертає HTML-шаблон особистого кабінету
    }

    @GetMapping("/appointments")
    public String showAppointments(HttpSession session) {
        // Перевірка, чи клієнт авторизований
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return "redirect:/login-client";  // Якщо не авторизований, перенаправляємо на сторінку логіну
        }
        return "appointment";  // Повертає HTML-шаблон для запису на прийом
    }
}
