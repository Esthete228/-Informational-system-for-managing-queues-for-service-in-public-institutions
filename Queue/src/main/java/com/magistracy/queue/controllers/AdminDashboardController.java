package com.magistracy.queue.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminDashboardController {

    // Показати головну сторінку адміністратора з моніторингом черги
    @GetMapping("/admin-dashboard")
    public ModelAndView showAdminDashboard(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) {
            return new ModelAndView("admin-dashboard");  // HTML-шаблон основної сторінки адміністратора
        }
        return new ModelAndView("redirect:/login-employee");  // Перенаправлення на сторінку логіну, якщо роль не адміністратор
    }

    // Показати сторінку керування послугами
    @GetMapping("/admin-dashboard/services")
    public ModelAndView showServicesDashboard(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) {
            return new ModelAndView("services");  // HTML-шаблон для керування послугами
        }
        return new ModelAndView("redirect:/login-employee");  // Перенаправлення на логін, якщо роль не адміністратор
    }

    // Показати сторінку керування працівниками
    @GetMapping("/admin-dashboard/employees")
    public ModelAndView showEmployeesDashboard(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) {
            return new ModelAndView("employees");  // HTML-шаблон для керування працівниками
        }
        return new ModelAndView("redirect:/login-employee");  // Перенаправлення на логін, якщо роль не адміністратор
    }

    @GetMapping("/admin-dashboard/workplaces")
    public ModelAndView showWorkplacesDashboard(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) {
            return new ModelAndView("workplaces");

        }
        return new ModelAndView("redirect:/login-employee");
    }
}
