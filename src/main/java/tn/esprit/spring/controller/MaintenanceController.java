package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Maintenance;
import tn.esprit.spring.service.MaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<Maintenance>> findAll() {
        return ResponseEntity.ok(maintenanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> findById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.findById(id));
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<List<Maintenance>> findByVehicule(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(maintenanceService.findByVehicule_IdVehicule(vehiculeId));
    }

    @PostMapping
    public ResponseEntity<Maintenance> save(@RequestBody Maintenance maintenance) {
        return new ResponseEntity<>(maintenanceService.save(maintenance), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(@PathVariable Long id,
                                              @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.update(id, maintenance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}