package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.service.IVehiculeService;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @PostMapping("/ajouterVehicule")
    public Vehicule ajouterVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.ajouterVehicule(vehicule);
    }

    @PutMapping("/modifierVehicule")
    public Vehicule modifierVehicule(@RequestBody Vehicule vehicule) {
        return vehiculeService.modifierVehicule(vehicule);
    }

    @GetMapping("/recupererTousVehicules")
    public List<Vehicule> getAllVehicule() {
        return vehiculeService.getAllVehicule();
    }

    @GetMapping("/recupererVehicule/{id}")
    public Vehicule getVehiculeById(@PathVariable Long id) {
        return vehiculeService.getVehicule(id);
    }

    @DeleteMapping("/deleteVehiculeById/{id}")
    public void supprimerVehiculeById(@PathVariable Long id) {
        vehiculeService.supprimerVehiculeById(id);
    }
}
