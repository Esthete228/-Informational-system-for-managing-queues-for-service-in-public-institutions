package com.magistracy.queue.controllers;

import com.magistracy.queue.services.ServiceWorkplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service-workplace")
public class ServiceWorkplaceController {

    private final ServiceWorkplaceService serviceWorkplaceService;

    @Autowired
    public ServiceWorkplaceController(ServiceWorkplaceService serviceWorkplaceService) {
        this.serviceWorkplaceService = serviceWorkplaceService;
    }

    // DTO для отримання даних
    public static class LinkRequest {
        public Long serviceId;
        public Long workplaceId;
    }

    // Статус відповіді
    public static class ResponseStatus {
        public String status;
        public String message;

        public ResponseStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    // Link service to workplace
    @PostMapping("/link")
    @ResponseBody
    public ResponseEntity<ResponseStatus> linkServiceToWorkplace(@RequestBody LinkRequest request) {
        try {
            // Викликаємо сервіс для прив'язки послуги до робочого місця
            serviceWorkplaceService.linkServiceToWorkplace(request.serviceId, request.workplaceId);
            return ResponseEntity.ok(new ResponseStatus("success", "Послугу успішно прив'язано до робочого місця"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseStatus("error", "Не вдалося прив'язати послугу до робочого місця: " + e.getMessage()));
        }
    }

    // Unlink service from workplace
    @PostMapping("/unlink")
    @ResponseBody
    public ResponseEntity<ResponseStatus> unlinkServiceFromWorkplace(@RequestBody LinkRequest request) {
        try {
            // Викликаємо сервіс для відв'язки послуги від робочого місця
            serviceWorkplaceService.unlinkServiceFromWorkplace(request.serviceId, request.workplaceId);
            return ResponseEntity.ok(new ResponseStatus("success", "Послугу успішно від\'язано від робочого місця"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseStatus("error", "Не вдалося від\'язати послугу від робочого місця: " + e.getMessage()));
        }
    }
}
