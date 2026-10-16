package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.service.IEquipementService;

import java.util.List;

@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final IEquipementService equipementService;

    @PostMapping("/ajouterEquipement")
    public Equipement ajouterEquipement(@RequestBody Equipement equipement) {
        return equipementService.ajouterEquipement(equipement);
    }

    @PutMapping("/modifierEquipement")
    public Equipement modifierEquipement(@RequestBody Equipement equipement) {
        return equipementService.modifierEquipement(equipement);
    }

    @GetMapping("/recupererTousEquipements")
    public List<Equipement> getAllEquipement() {
        return equipementService.getAllEquipement();
    }

    @GetMapping("/recupererEquipement/{id}")
    public Equipement getEquipement(@PathVariable Long id) {
        return equipementService.getEquipement(id);
    }

    @DeleteMapping("/deleteEquipementById/{id}")
    public void supprimerEquipementById(@PathVariable Long id) {
        equipementService.supprimerEquipementById(id);
    }
}
