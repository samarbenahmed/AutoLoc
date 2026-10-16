package tn.esprit.spring.service;

import tn.esprit.spring.entity.Maintenance;

import java.util.List;

public interface IMaintenanceService {

    Maintenance ajouterMaintenance(Maintenance maintenance);

    Maintenance modifierMaintenance(Maintenance maintenance);

    Maintenance getMaintenance(Long id);

    List<Maintenance> getAllMaintenance();

    void supprimerMaintenanceById(Long id);

}
