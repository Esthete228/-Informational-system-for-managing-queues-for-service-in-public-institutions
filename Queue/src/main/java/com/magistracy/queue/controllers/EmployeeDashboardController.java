package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeDashboardController {

    @GetMapping("/employee-dashboard")
    public String showEmployeeDashboard() {
        return "employee-dashboard";  // Повертає HTML-шаблон панелі працівника
    }
}
