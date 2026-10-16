package tn.esprit.spring.service;

import tn.esprit.spring.entity.Employe;

import java.util.List;

public interface IEmployeService {

    Employe ajouterEmploye(Employe employe);

    Employe modifierEmploye(Employe employe);

    Employe getEmploye(Long id);

    List<Employe> getAllEmploye();

    void supprimerEmployeById(Long id);

}
