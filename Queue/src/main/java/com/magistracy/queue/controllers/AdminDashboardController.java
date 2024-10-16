package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminDashboardController {

    @GetMapping("/admin-dashboard")
    public String showClientDashboard() {
        return "admin-dashboard";  // Повертає HTML-шаблон особистого кабінету
    }
}