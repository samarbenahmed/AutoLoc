package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.EquipementRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class IEquipementServiceImp implements IEquipementService {

    private final EquipementRepository equipementRepository;

    @Override
    public Equipement ajouterEquipement(Equipement equipement) {
        valider(equipement);
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement modifierEquipement(Equipement equipement) {
        if (equipement == null || equipement.getIdEquipement() == null) {
            throw new BadRequestException("L'identifiant de l'equipement est obligatoire pour une modification");
        }
        getEquipement(equipement.getIdEquipement());
        valider(equipement);
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement getEquipement(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de l'equipement est obligatoire");
        }
        return equipementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipement introuvable avec id " + id));
    }

    @Override
    public List<Equipement> getAllEquipement() {
        return equipementRepository.findAll();
    }

    @Override
    public void supprimerEquipementById(Long id) {
        getEquipement(id);
        equipementRepository.deleteById(id);
    }

    private void valider(Equipement equipement) {
        if (equipement == null) {
            throw new BadRequestException("Equipement obligatoire");
        }
        if (equipement.getLibelle() == null || equipement.getLibelle().isBlank()) {
            throw new BadRequestException("Le libelle de l'equipement est obligatoire");
        }
    }
}
