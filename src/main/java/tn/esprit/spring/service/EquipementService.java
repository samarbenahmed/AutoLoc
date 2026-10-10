package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.EquipementRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipementService {

    private final EquipementRepository equipementRepository;
    private final VehiculeRepository vehiculeRepository;

    @Transactional(readOnly = true)
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Equipement findById(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipement " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Equipement> searchByLibelle(String libelle) {
        return equipementRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    public Equipement save(Equipement equipement) {
        valider(equipement);
        return equipementRepository.save(equipement);
    }

    public Equipement update(Long id, Equipement equipement) {
        findById(id);
        valider(equipement);
        equipement.setIdEquipement(id);
        return equipementRepository.save(equipement);
    }

    public void delete(Long id) {
        Equipement equipement = findById(id);
        equipementRepository.delete(equipement);
    }

    public void ajouterAuVehicule(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Vehicule " + vehiculeId + " introuvable"));
        Equipement equipement = findById(equipementId);
        vehicule.addEquipement(equipement);
        vehiculeRepository.save(vehicule);
    }

    public void retirerDuVehicule(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new NotFoundException("Vehicule " + vehiculeId + " introuvable"));
        Equipement equipement = findById(equipementId);
        vehicule.getEquipements().remove(equipement);
        equipement.getVehicules().remove(vehicule);
        vehiculeRepository.save(vehicule);
    }

    private void valider(Equipement equipement) {
        if (equipement == null) {
            throw new BadRequestException("L'equipement ne peut pas etre nul");
        }
        if (equipement.getLibelle() == null || equipement.getLibelle().trim().isEmpty()) {
            throw new BadRequestException("Le libelle de l'equipement est obligatoire");
        }
    }
}