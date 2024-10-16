package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ClientDashboardController {

    @GetMapping("/client-dashboard")
    public String showClientDashboard() {
        return "client-dashboard";  // Повертає HTML-шаблон особистого кабінету
    }
}
