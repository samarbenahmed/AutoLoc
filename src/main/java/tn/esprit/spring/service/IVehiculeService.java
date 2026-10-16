package tn.esprit.spring.service;

import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.CategorieVehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;

import java.util.List;

public interface IVehiculeService {
    public Vehicule ajouterVehicule(Vehicule vehicule);

    public Vehicule modifierVehicule(Vehicule vehicule);

    public Vehicule getVehicule(Long id);

    public List<Vehicule> getAllVehicule();

    public void supprimerVehiculeById(Long id);


}