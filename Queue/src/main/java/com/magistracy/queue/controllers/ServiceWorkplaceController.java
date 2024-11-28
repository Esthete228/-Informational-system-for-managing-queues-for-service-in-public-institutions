package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.ServiceWorkplace;
import com.magistracy.queue.services.ServiceWorkplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        public List<Long> serviceIds;
        public List<Long> workplaceIds;
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
    public ResponseEntity<ResponseStatus> linkServicesToWorkplaces(@RequestBody LinkRequest request) {
        try {
            // Обробляємо кожен зв'язок послуги з робочим місцем
            for (Long serviceId : request.serviceIds) {
                for (Long workplaceId : request.workplaceIds) {
                    serviceWorkplaceService.linkServiceToWorkplace(serviceId, workplaceId);
                }
            }
            return ResponseEntity.ok(new ResponseStatus("success", "Послуги успішно прив'язані до робочих місць"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseStatus("error", "Не вдалося прив'язати послуги: " + e.getMessage()));
        }
    }

    // Unlink service from workplace
    @PostMapping("/unlink")
    @ResponseBody
    public ResponseEntity<ResponseStatus> unlinkServiceFromWorkplace(@RequestBody LinkRequest request) {
        try {
            // Обробляємо кожен зв'язок послуги з робочим місцем
            for (Long serviceId : request.serviceIds) {
                for (Long workplaceId : request.workplaceIds) {
                    serviceWorkplaceService.unlinkServiceFromWorkplace(serviceId, workplaceId);
                }
            }
            return ResponseEntity.ok(new ResponseStatus("success", "Послугу успішно від\'язано від робочого місця"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseStatus("error", "Не вдалося від\'язати послугу від робочого місця: " + e.getMessage()));
        }
    }

    @GetMapping("/available-services/{workplaceId}")
    public ResponseEntity<List<ServiceWorkplace>> getAvailableServices(@PathVariable Long workplaceId) {
        try {
            List<ServiceWorkplace> availableServices = serviceWorkplaceService.getServicesForWorkplace(workplaceId);
            return ResponseEntity.ok(availableServices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/available-workplaces/{serviceId}")
    public ResponseEntity<List<ServiceWorkplace>> getAvailableWorkplaces(@PathVariable Long serviceId) {
        try {
            List<ServiceWorkplace> availableWorkplaces = serviceWorkplaceService.getWorkplacesForService(serviceId);
            return ResponseEntity.ok(availableWorkplaces);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
