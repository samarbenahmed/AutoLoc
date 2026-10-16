package tn.esprit.spring.service;

import tn.esprit.spring.entity.Agence;

import java.util.List;

public interface IAgenceService {

    Agence ajouterAgence(Agence agence);

    Agence modifierAgence(Agence agence);

    Agence getAgence(Long id);

    List<Agence> getAllAgence();

    void supprimerAgenceById(Long id);

}
