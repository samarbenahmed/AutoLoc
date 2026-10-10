package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Maintenance;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.MaintenanceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;

    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Maintenance> findByVehicule_IdVehicule(Long vehiculeId) {
        return maintenanceRepository.findByVehicule_IdVehicule(vehiculeId);
    }

    public Maintenance save(Maintenance maintenance) {
        valider(maintenance, null);
        maintenance.setVehicule(chargerVehicule(maintenance));
        Maintenance enregistree = maintenanceRepository.save(maintenance);
        majStatutVehicule(enregistree.getVehicule(), StatutVehicule.MAINTENANCE);
        return enregistree;
    }

    public Maintenance update(Long id, Maintenance maintenance) {
        findById(id);
        valider(maintenance, id);
        maintenance.setIdMaintenance(id);
        maintenance.setVehicule(chargerVehicule(maintenance));
        Maintenance enregistree = maintenanceRepository.save(maintenance);
        boolean autreMaintenanceActive = maintenanceRepository
                .existsByVehicule_IdVehiculeAndDateFinIsNull(enregistree.getVehicule().getIdVehicule());
        majStatutVehicule(enregistree.getVehicule(),
                autreMaintenanceActive ? StatutVehicule.MAINTENANCE : StatutVehicule.DISPONIBLE);
        return enregistree;
    }

    public void delete(Long id) {
        Maintenance maintenance = findById(id);
        Vehicule vehicule = maintenance.getVehicule();
        maintenanceRepository.delete(maintenance);
        if (vehicule != null && vehicule.getIdVehicule() != null
                && !maintenanceRepository
                        .existsByVehicule_IdVehiculeAndDateFinIsNull(vehicule.getIdVehicule())) {
            majStatutVehicule(vehicule, StatutVehicule.DISPONIBLE);
        }
    }

    /**
     * Remplace la reference "vehicule" recue dans le corps de la requete (qui ne contient
     * qu'un identifiant) par l'entite reellement geree.
     */
    private Vehicule chargerVehicule(Maintenance maintenance) {
        return vehiculeRepository.findById(maintenance.getVehicule().getIdVehicule())
                .orElseThrow(() -> new NotFoundException(
                        "Vehicule " + maintenance.getVehicule().getIdVehicule() + " introuvable"));
    }

    private void majStatutVehicule(Vehicule vehicule, StatutVehicule statut) {
        Vehicule managed = vehiculeRepository.findById(vehicule.getIdVehicule()).orElse(null);
        if (managed != null && managed.getStatut() != statut) {
            managed.setStatut(statut);
            vehiculeRepository.save(managed);
        }
    }

    private void valider(Maintenance maintenance, Long idExistant) {
        if (maintenance == null) {
            throw new BadRequestException("La maintenance ne peut pas etre nulle");
        }
        if (maintenance.getDateDebut() == null) {
            throw new BadRequestException("La date de debut de la maintenance est obligatoire");
        }
        if (maintenance.getDateFin() != null && maintenance.getDateFin().isBefore(maintenance.getDateDebut())) {
            throw new BadRequestException("La date de fin doit etre posterieure a la date de debut");
        }
        if (maintenance.getVehicule() == null || maintenance.getVehicule().getIdVehicule() == null) {
            throw new BadRequestException("Le vehicule de la maintenance est obligatoire");
        }
        if (!vehiculeRepository.existsById(maintenance.getVehicule().getIdVehicule())) {
            throw new NotFoundException(
                    "Vehicule " + maintenance.getVehicule().getIdVehicule() + " introuvable");
        }
        boolean chevauchement = maintenanceRepository
                .findByVehicule_IdVehicule(maintenance.getVehicule().getIdVehicule()).stream()
                .anyMatch(existante -> !existante.getIdMaintenance().equals(idExistant)
                        && chevauche(existante, maintenance));
        if (chevauchement) {
            throw new BadRequestException("Le vehicule a deja une maintenance sur cette periode");
        }
    }

    private boolean chevauche(Maintenance existante, Maintenance nouvelle) {
        LocalDate debutExistant = existante.getDateDebut();
        LocalDate finExistant = existante.getDateFin() == null ? LocalDate.MAX : existante.getDateFin();
        LocalDate debutNouveau = nouvelle.getDateDebut();
        LocalDate finNouveau = nouvelle.getDateFin() == null ? LocalDate.MAX : nouvelle.getDateFin();
        return !debutNouveau.isAfter(finExistant) && !finNouveau.isBefore(debutExistant);
    }
}