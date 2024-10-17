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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final OTPService otpService;

    @Autowired
    public AuthController(EmployeeRepository employeeRepository, ClientRepository clientRepository, OTPService otpService) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
        this.otpService = otpService;
    }

    @GetMapping("/login-employee")
    public String employeeLoginPage() {
        return "login-employee";  // Повертаємо login-employee.html
    }

    @GetMapping("/login-client")
    public String clientLoginPage() {
        return "login-client";  // Повертаємо login-client.html
    }

    private ModelAndView authenticateUser(String userId, String role, HttpSession session, String redirectUrl) {
        session.setAttribute("userId", userId);
        session.setAttribute("role", role);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @PostMapping("/authenticate-employee")
    public ModelAndView authenticateEmployee(String username, String password, HttpSession session) {
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isPresent() && employee.get().getPassword().equals(password)) {
            String role = employee.get().getRole();
            return authenticateUser(employee.get().getId().toString(), role, session, role.equals("admin") ? "/admin-dashboard" : "/employee-dashboard");
        }
        return new ModelAndView("login-employee", "error", "Невірні облікові дані");
    }

    @PostMapping("/send-otp")
    public ModelAndView sendOtp(@RequestParam String phone) {
        String otpCode = otpService.generateOTP();
        otpService.saveOtp(phone, otpCode);
        return new ModelAndView("otp-verification", "phone", phone);
    }

    @PostMapping("/verify-otp")
    public ModelAndView verifyOtp(@RequestParam String phone, @RequestParam String otp, HttpSession session) {
        if (otpService.verifyOtp(phone, otp)) {
            Client client = clientRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> new RuntimeException("Клієнт не знайдений"));
            session.setAttribute("clientId", client.getId());
            session.setAttribute("role", "client");
            return new ModelAndView("redirect:/client-dashboard");
        } else {
            return new ModelAndView("otp-verification", "error", "Невірний OTP код");
        }
    }

    @PostMapping("/verify-register-otp")
    public ModelAndView verifyRegisterOtp(@RequestParam String phone, @RequestParam String otp, HttpSession session) {
        if (otpService.verifyOtp(phone, otp)) {
            Client client = clientRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> new RuntimeException("Клієнт не знайдений"));
            session.setAttribute("clientId", client.getId());
            session.setAttribute("role", "client");
            return new ModelAndView("redirect:/login-client");
        } else {
            return new ModelAndView("otp-verification-reg", "error", "Невірний OTP код");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}