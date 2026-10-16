package tn.esprit.spring.service;

import tn.esprit.spring.entity.Equipement;

import java.util.List;

public interface IEquipementService {

    Equipement ajouterEquipement(Equipement equipement);

    Equipement modifierEquipement(Equipement equipement);

    Equipement getEquipement(Long id);

    List<Equipement> getAllEquipement();

    void supprimerEquipementById(Long id);

}
