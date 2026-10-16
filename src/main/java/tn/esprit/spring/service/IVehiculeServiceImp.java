package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class IVehiculeServiceImp implements IVehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        validerEtResoudreAgence(vehicule);
        if (vehiculeRepository.existsByImmatriculation(vehicule.getImmatriculation())) {
            throw new ConflictException(
                    "Un vehicule existe deja avec l'immatriculation " + vehicule.getImmatriculation());
        }
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule modifierVehicule(Vehicule vehicule) {
        if (vehicule == null || vehicule.getIdVehicule() == null) {
            throw new BadRequestException("L'identifiant du vehicule est obligatoire pour une modification");
        }
        getVehicule(vehicule.getIdVehicule());
        validerEtResoudreAgence(vehicule);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule getVehicule(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant du vehicule est obligatoire");
        }
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicule introuvable avec id " + id));
    }

    @Override
    public List<Vehicule> getAllVehicule() {
        return vehiculeRepository.findAll();
    }

    @Override
    public void supprimerVehiculeById(Long id) {
        getVehicule(id);
        vehiculeRepository.deleteById(id);
    }

    private void validerEtResoudreAgence(Vehicule vehicule) {
        if (vehicule == null) {
            throw new BadRequestException("Vehicule obligatoire");
        }
        if (estVide(vehicule.getImmatriculation())) {
            throw new BadRequestException("L'immatriculation du vehicule est obligatoire");
        }
        if (estVide(vehicule.getMarque())) {
            throw new BadRequestException("La marque du vehicule est obligatoire");
        }
        BigDecimal tarif = vehicule.getTarifJournalier();
        if (tarif != null && tarif.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le tarif journalier doit etre strictement positif");
        }
        if (vehicule.getAgence() == null || vehicule.getAgence().getIdAgence() == null) {
            throw new BadRequestException("Agence obligatoire : {\"agence\":{\"idAgence\":1}}");
        }
        Long idAgence = vehicule.getAgence().getIdAgence();
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new NotFoundException("Agence introuvable avec id " + idAgence));
        vehicule.setAgence(agence);
        if (vehicule.getStatut() == null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
