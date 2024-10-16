//package com.magistracy.queue;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.core.userdetails.User;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/login").permitAll() // Доступ до сторінки входу
//                        .requestMatchers("/services/**").hasRole("ADMIN") // Доступ для адміністраторів
//                        .requestMatchers("/queues/**").hasAnyRole("ADMIN", "EMPLOYEE") // Доступ для адміністраторів і працівників
//                        .requestMatchers("/users/**").permitAll() // Доступ для всіх до користувачів
//                        .anyRequest().authenticated() // Усі інші запити потребують аутентифікації
//                )
//                .httpBasic(Customizer.withDefaults()); // Використання базової аутентифікації
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        var userDetailsService = new InMemoryUserDetailsManager();
//        userDetailsService.createUser(User.withUsername("admin")
//                .password(passwordEncoder().encode("adminpass"))
//                .roles("ADMIN")
//                .build());
//        userDetailsService.createUser(User.withUsername("employee")
//                .password(passwordEncoder().encode("employeepass"))
//                .roles("EMPLOYEE")
//                .build());
//        return userDetailsService;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//
//
