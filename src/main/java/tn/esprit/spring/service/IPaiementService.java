package tn.esprit.spring.service;

import tn.esprit.spring.entity.Paiement;

import java.util.List;

public interface IPaiementService {

    Paiement ajouterPaiement(Paiement paiement);

    Paiement modifierPaiement(Paiement paiement);

    Paiement getPaiement(Long id);

    List<Paiement> getAllPaiement();

    void supprimerPaiementById(Long id);

}
