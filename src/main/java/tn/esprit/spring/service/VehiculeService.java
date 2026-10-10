package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.CategorieVehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;

    @Transactional(readOnly = true)
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicule " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public Vehicule findByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation)
                .orElseThrow(() -> new NotFoundException(
                        "Vehicule avec immatriculation " + immatriculation + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Vehicule> findByStatut(StatutVehicule statut) {
        return vehiculeRepository.findByStatut(statut);
    }

    @Transactional(readOnly = true)
    public List<Vehicule> findByCategorie(CategorieVehicule categorie) {
        return vehiculeRepository.findByCategorie(categorie);
    }

    @Transactional(readOnly = true)
    public List<Vehicule> findByAgence_IdAgence(Long agenceId) {
        return vehiculeRepository.findByAgence_IdAgence(agenceId);
    }

    public Vehicule save(Vehicule vehicule) {
        valider(vehicule);
        if (vehicule.getIdVehicule() == null
                && vehiculeRepository.existsByImmatriculation(vehicule.getImmatriculation())) {
            throw new ConflictException("Un vehicule avec cette immatriculation existe deja");
        }
        if (vehicule.getStatut() == null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }
        vehicule.setAgence(agenceRepository.findById(vehicule.getAgence().getIdAgence())
                .orElseThrow(() -> new NotFoundException(
                        "Agence " + vehicule.getAgence().getIdAgence() + " introuvable")));
        return vehiculeRepository.save(vehicule);
    }

    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existante = findById(id);
        valider(vehicule);
        if (!existante.getImmatriculation().equals(vehicule.getImmatriculation())
                && vehiculeRepository.existsByImmatriculation(vehicule.getImmatriculation())) {
            throw new ConflictException("Un vehicule avec cette immatriculation existe deja");
        }
        vehicule.setIdVehicule(id);
        vehicule.setAgence(agenceRepository.findById(vehicule.getAgence().getIdAgence())
                .orElseThrow(() -> new NotFoundException(
                        "Agence " + vehicule.getAgence().getIdAgence() + " introuvable")));
        return vehiculeRepository.save(vehicule);
    }

    public void delete(Long id) {
        Vehicule vehicule = findById(id);
        if (!vehicule.getReservations().isEmpty() || !vehicule.getMaintenances().isEmpty()) {
            throw new ConflictException(
                    "Impossible de supprimer un vehicule ayant des reservations ou des maintenances");
        }
        vehiculeRepository.delete(vehicule);
    }

    public Vehicule changerStatut(Long id, StatutVehicule statut) {
        if (statut == null) {
            throw new BadRequestException("Le statut du vehicule est obligatoire");
        }
        Vehicule vehicule = findById(id);
        vehicule.setStatut(statut);
        return vehiculeRepository.save(vehicule);
    }

    private void valider(Vehicule vehicule) {
        if (vehicule == null) {
            throw new BadRequestException("Le vehicule ne peut pas etre nul");
        }
        if (isBlank(vehicule.getImmatriculation()) || isBlank(vehicule.getMarque())) {
            throw new BadRequestException("L'immatriculation et la marque du vehicule sont obligatoires");
        }
        if (vehicule.getTarifJournalier() == null
                || vehicule.getTarifJournalier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le tarif journalier doit etre strictement positif");
        }
        if (vehicule.getCategorie() == null) {
            throw new BadRequestException("La categorie du vehicule est obligatoire");
        }
        if (vehicule.getAgence() == null || vehicule.getAgence().getIdAgence() == null) {
            throw new BadRequestException("L'agence du vehicule est obligatoire");
        }
        if (!agenceRepository.existsById(vehicule.getAgence().getIdAgence())) {
            throw new NotFoundException("Agence " + vehicule.getAgence().getIdAgence() + " introuvable");
        }
    }

    private boolean isBlank(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }
}