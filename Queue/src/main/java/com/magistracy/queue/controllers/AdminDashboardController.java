package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    // Показати головну сторінку адміністратора з моніторингом черги
    @GetMapping("/admin-dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";  // HTML-шаблон основної сторінки адміністратора
    }

    // Показати сторінку керування послугами
    @GetMapping("/admin-dashboard/services")
    public String showServicesDashboard() {
        return "services";  // HTML-шаблон для керування послугами
    }

    // Показати сторінку керування працівниками
    @GetMapping("/admin-dashboard/employees")
    public String showEmployeesDashboard() {
        return "employees";  // HTML-шаблон для керування працівниками
    }
}
