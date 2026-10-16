package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Maintenance;
import tn.esprit.spring.service.IMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @PostMapping("/ajouterMaintenance")
    public Maintenance ajouterMaintenance(@RequestBody Maintenance maintenance) {
        return maintenanceService.ajouterMaintenance(maintenance);
    }

    @PutMapping("/modifierMaintenance")
    public Maintenance modifierMaintenance(@RequestBody Maintenance maintenance) {
        return maintenanceService.modifierMaintenance(maintenance);
    }

    @GetMapping("/recupererTousMaintenances")
    public List<Maintenance> getAllMaintenance() {
        return maintenanceService.getAllMaintenance();
    }

    @GetMapping("/recupererMaintenance/{id}")
    public Maintenance getMaintenance(@PathVariable Long id) {
        return maintenanceService.getMaintenance(id);
    }

    @DeleteMapping("/deleteMaintenanceById/{id}")
    public void supprimerMaintenanceById(@PathVariable Long id) {
        maintenanceService.supprimerMaintenanceById(id);
    }
}
