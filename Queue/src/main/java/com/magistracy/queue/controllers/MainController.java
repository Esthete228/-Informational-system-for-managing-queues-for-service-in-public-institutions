package com.magistracy.queue.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Головна сторінка
    @GetMapping("/")
    public String mainPage() {
        return "main";  // Повертаємо головну сторінку
    }

}

