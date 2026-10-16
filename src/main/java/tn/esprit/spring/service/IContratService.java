package tn.esprit.spring.service;

import tn.esprit.spring.entity.Contrat;

import java.util.List;

public interface IContratService {

    Contrat ajouterContrat(Contrat contrat);

    Contrat modifierContrat(Contrat contrat);

    Contrat getContrat(Long id);

    List<Contrat> getAllContrat();

    void supprimerContratById(Long id);

}
