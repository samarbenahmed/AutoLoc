package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Vehicule;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final IVehiculeService vehiculeService;

    public Vehicule save(Vehicule vehicule) {
        return vehiculeService.ajouterVehicule(vehicule);
    }

    public Vehicule findById(Long id) {
        return vehiculeService.getVehicule(id);
    }
}
