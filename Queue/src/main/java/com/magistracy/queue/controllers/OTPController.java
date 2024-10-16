    package com.magistracy.queue.controllers;

    import com.magistracy.queue.services.OTPService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.servlet.ModelAndView;

    @Controller
    public class OTPController {

        @Autowired
        private OTPService otpService;

        @PostMapping("/send-otp")
        public ModelAndView sendOtp(@RequestParam String phone) {
            System.out.println("Отримано номер телефону: " + phone);
            String otpCode = otpService.generateOTP();  // Генеруємо OTP
            otpService.saveOtp(phone, otpCode);  // Зберігаємо OTP для перевірки
            return new ModelAndView("otp-verification", "phone", phone);  // Переходимо на сторінку введення OTP
        }

        @PostMapping("/verify-otp")
        public ModelAndView verifyOtp(@RequestParam String phone, @RequestParam String otp) {
            System.out.println("Перевіряємо OTP для телефону: " + phone); // Логування телефону
            if (otpService.verifyOtp(phone, otp)) {
                return new ModelAndView("redirect:/client-dashboard");  // Успішна авторизація
            } else {
                ModelAndView mav = new ModelAndView("otp-verification");
                mav.addObject("error", "Невірний код OTP");
                mav.addObject("phone", phone);  // Щоб зберегти телефон у формі
                return mav;
            }
        }

        @PostMapping("/verify-register-otp")
        public ModelAndView verifyClientOtp(@RequestParam String phone, @RequestParam String otp) {
            if (otpService.verifyOtp(phone, otp)) {
                // Перенаправлення до особистого кабінету після успішної авторизації
                return new ModelAndView("redirect:/login-client");
            } else {
                // Якщо код невірний або прострочений, залишаємо на сторінці введення OTP з повідомленням про помилку
                ModelAndView mav = new ModelAndView("otp-verification-reg");
                mav.addObject("error", "Невірний код OTP");
                mav.addObject("phone", phone);  // Щоб зберегти телефон у формі
                return mav;
            }
        }
    }

