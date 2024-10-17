package com.magistracy.queue;

import com.magistracy.queue.services.ClientDetailsService;
import com.magistracy.queue.services.EmployeeDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/auth", "/login-employee", "/login-client", "/register-client", "/register-client", "/send-otp", "/verify-otp", "/verify-register-otp").permitAll() // Дозволити доступ до цих сторінок
                        .anyRequest().authenticated() // Всі інші запити потребують аутентифікації
                )
                .formLogin((form) -> form
                        .loginPage("/") // Ваш маршрут для аутентифікації
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
