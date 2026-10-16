package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Maintenance;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.MaintenanceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class IMaintenanceServiceImp implements IMaintenanceService {

    private static final int DESCRIPTION_MAX = 500;

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;

    @Override
    public Maintenance ajouterMaintenance(Maintenance maintenance) {
        validerEtResoudreVehicule(maintenance);
        marquerVehiculeEnMaintenance(maintenance.getVehicule());
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance modifierMaintenance(Maintenance maintenance) {
        if (maintenance == null || maintenance.getIdMaintenance() == null) {
            throw new BadRequestException("L'identifiant de la maintenance est obligatoire pour une modification");
        }
        Maintenance existante = getMaintenance(maintenance.getIdMaintenance());
        if (maintenance.getVehicule() == null) {
            maintenance.setVehicule(existante.getVehicule());
        }
        validerEtResoudreVehicule(maintenance);
        marquerVehiculeEnMaintenance(maintenance.getVehicule());
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance getMaintenance(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de la maintenance est obligatoire");
        }
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance introuvable avec id " + id));
    }

    @Override
    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    @Override
    public void supprimerMaintenanceById(Long id) {
        Maintenance maintenance = getMaintenance(id);
        Vehicule vehicule = maintenance.getVehicule();
        maintenanceRepository.deleteById(id);
        if (vehicule != null && vehicule.getStatut() == StatutVehicule.MAINTENANCE
                && maintenanceRepository.findByVehicule_IdVehicule(vehicule.getIdVehicule()).isEmpty()) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }
    }

    private void validerEtResoudreVehicule(Maintenance maintenance) {
        if (maintenance == null) {
            throw new BadRequestException("Maintenance obligatoire");
        }
        if (maintenance.getDateDebut() == null) {
            throw new BadRequestException("La date de debut de la maintenance est obligatoire");
        }
        if (maintenance.getDateFin() != null
                && maintenance.getDateFin().isBefore(maintenance.getDateDebut())) {
            throw new BadRequestException(
                    "La date de fin doit etre posterieure ou egale a la date de debut");
        }
        if (maintenance.getDescription() != null
                && maintenance.getDescription().length() > DESCRIPTION_MAX) {
            throw new BadRequestException(
                    "La description de la maintenance ne peut pas depasser " + DESCRIPTION_MAX + " caracteres");
        }
        if (maintenance.getVehicule() == null || maintenance.getVehicule().getIdVehicule() == null) {
            throw new BadRequestException("Vehicule obligatoire : {\"vehicule\":{\"idVehicule\":1}}");
        }
        Long idVehicule = maintenance.getVehicule().getIdVehicule();
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new NotFoundException("Vehicule introuvable avec id " + idVehicule));
        maintenance.setVehicule(vehicule);
    }

    private void marquerVehiculeEnMaintenance(Vehicule vehicule) {
        vehicule.setStatut(StatutVehicule.MAINTENANCE);
        vehiculeRepository.save(vehicule);
    }
}
