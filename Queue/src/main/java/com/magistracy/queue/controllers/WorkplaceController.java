package com.magistracy.queue.controllers;

import com.magistracy.queue.entities.Workplace;
import com.magistracy.queue.services.WorkplaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin-dashboard/workplaces")
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    public WorkplaceController(WorkplaceService workplaceService) {
        this.workplaceService = workplaceService;
    }

    @GetMapping("/all-workplaces")
    public List<Workplace> getAllWorkplaces() {
        List<Workplace> workplaces = workplaceService.findAll();
        System.out.println(workplaces); // Додаємо лог для перевірки
        return workplaces;
    }

    @PostMapping("/add-workplace")
    public ResponseEntity<?> addWorkplace(@RequestBody Workplace workplace) {
        try {
            if (workplace.getWorkplaceName() == null || workplace.getWorkplaceName().isEmpty()) {
                return ResponseEntity.badRequest().body("Назва робочого місця не може бути пустою");
            }
            Workplace savedWorkplace = workplaceService.save(workplace);
            return ResponseEntity.ok(savedWorkplace);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Сталася помилка: " + e.getMessage());
        }
    }

    @PutMapping("/update-workplace/{id}")
    public Workplace updateWorkplace(@PathVariable Long id, @RequestBody Workplace workplace) {
        return workplaceService.update(id, workplace);
    }

    @DeleteMapping("/delete-workplace/{id}")
    public void deleteWorkplace(@PathVariable Long id) {
        workplaceService.delete(id);
    }
}
