package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Maintenance;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final IMaintenanceService maintenanceService;

    public Maintenance save(Maintenance maintenance) {
        return maintenanceService.ajouterMaintenance(maintenance);
    }

    public Maintenance findById(Long id) {
        return maintenanceService.getMaintenance(id);
    }
}
