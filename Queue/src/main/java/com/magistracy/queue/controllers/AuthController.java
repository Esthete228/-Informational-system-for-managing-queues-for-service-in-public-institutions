package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.entities.Employee;
import com.magistracy.queue.repositories.ClientRepository;
import com.magistracy.queue.repositories.EmployeeRepository;
import com.magistracy.queue.services.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AuthController {

    private final ClientRepository clientRepository;
    private final OTPService otpService;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AuthController(ClientRepository clientRepository, OTPService otpService, EmployeeRepository employeeRepository) {
        this.clientRepository = clientRepository;
        this.otpService = otpService;
        this.employeeRepository = employeeRepository;
    }

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @GetMapping("/")
    public String mainPage() {
        return "main";  // Повертаємо HTML-шаблон головної сторінки
    }

    @GetMapping("/login-employee")
    public String employeeLoginPage() {
        return "login-employee";
    }

    @GetMapping("/login-client")
    public String clientLoginPage() {
        return "login-client";
    }

    @GetMapping("/verification")
    public String otpSendPage() {
        return "otp-verification";
    }

    private ModelAndView authenticateUser(String userId, String role, HttpSession session, String redirectUrl) {
        session.setAttribute("userId", userId);
        session.setAttribute("role", role);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @GetMapping("/user-session")
    @ResponseBody
    public UserSessionResponse getUserSession(HttpServletRequest request) {
        // Отримуємо сесію
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            Long userId = (Long) session.getAttribute("userId");
            String workplace = (String) session.getAttribute("workplace");

            // Логування даних сесії
            System.out.println("Session Data - Username: " + username);
            System.out.println("Session Data - User ID: " + userId);
            System.out.println("Session Data - Workplace: " + workplace);

            // Перевірка наявності даних
            if (username != null && userId != null && workplace != null) {
                return new UserSessionResponse(username, userId, workplace);
            }
        }
        System.out.println("No session data found.");
        return new UserSessionResponse("unknown", null, null); // Повернути значення за замовчуванням
    }


    @PostMapping("/authenticate-employee")
    public ModelAndView authenticateEmployee(String username, String password, HttpSession session) {
        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isPresent() && passwordEncoder.matches(password, employee.get().getPassword())) {
            String role = employee.get().getRole();

            // Зберігаємо дані в сесії
            session.setAttribute("username", employee.get().getUsername());
            session.setAttribute("userId", employee.get().getId());
            session.setAttribute("workplaceId", employee.get().getWorkplace().getId());
            // Логування даних, що зберігаються в сесії
            System.out.println("Authenticated User - Username: " + employee.get().getUsername());
            System.out.println("Authenticated User - User ID: " + employee.get().getId());
            System.out.println("Authenticated User - Workplace ІD: " + employee.get().getWorkplace().getId());

            return authenticateUser(employee.get().getId().toString(), role, session, role.equals("admin") ? "/admin-dashboard" : "/employee-dashboard");
        }
        System.out.println("Authentication failed for username: " + username);
        return new ModelAndView("login-employee", "error", "Невірні облікові дані");
    }

    @GetMapping("/current-workplace")
    public ResponseEntity<Long> getCurrentWorkplace(HttpSession session) {
        Long workplaceId = (Long) session.getAttribute("workplaceId");
        if (workplaceId == null) {
            return ResponseEntity.badRequest().body(null); // Handle accordingly
        }
        return ResponseEntity.ok(workplaceId);
    }


    @PostMapping("/send-otp")
    public ModelAndView sendOtp(@RequestParam String phone) {
        String otpCode = otpService.generateOTP();
        otpService.saveOtp(phone, otpCode);
        return new ModelAndView("otp-verification", "phone", phone);
    }

    @PostMapping("/verify-otp")
    public ModelAndView verifyOtp(
            @RequestParam String phone, @RequestParam String otp, HttpSession session) {
        // Перевірка, чи існує клієнт перед верифікацією OTP
        Optional<Client> clientOptional = clientRepository.findByPhoneNumber(phone);
        if (clientOptional.isPresent() && otpService.verifyOtp(phone, otp)) {
            Client client = clientOptional.get();

            // Збереження сесії з роллю
            session.setAttribute("clientId", client.getId());
            session.setAttribute("role", "client");

            return new ModelAndView("redirect:/client-dashboard");
        }
        return new ModelAndView("otp-verification", "error", "Невірний OTP код або клієнт не знайдений");
    }

    @PostMapping("/verify-register-otp")
    public ModelAndView verifyRegisterOtp(@RequestParam String phone, @RequestParam String otp, HttpSession session) {
        if (otpService.verifyOtp(phone, otp)) {
            Optional<Client> clientOptional = clientRepository.findByPhoneNumber(phone);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                session.setAttribute("clientId", client.getId());
                session.setAttribute("role", "client");
                return new ModelAndView("redirect:/login-client");
            } else {
                return new ModelAndView("otp-verification", "error", "Клієнт не знайдений");
            }
        } else {
            return new ModelAndView("otp-verification", "error", "Невірний OTP код");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    public static class UserSessionResponse {
        private String username;
        private Long userId;
        private String workplace;

        public UserSessionResponse(String username, Long userId, String workplace) {
            this.username = username;
            this.userId = userId;
            this.workplace = workplace;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getWorkplace() {
            return workplace;
        }

        public void setWorkplace(String workplace) {
            this.workplace = workplace;
        }

        public String getUsername() {
            return username;
        }
    }
}
