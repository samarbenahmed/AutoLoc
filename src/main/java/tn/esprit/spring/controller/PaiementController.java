package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Paiement;
import tn.esprit.spring.service.IPaiementService;

import java.util.List;

@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;

    @PostMapping("/ajouterPaiement")
    public Paiement ajouterPaiement(@RequestBody Paiement paiement) {
        return paiementService.ajouterPaiement(paiement);
    }

    @PutMapping("/modifierPaiement")
    public Paiement modifierPaiement(@RequestBody Paiement paiement) {
        return paiementService.modifierPaiement(paiement);
    }

    @GetMapping("/recupererTousPaiements")
    public List<Paiement> getAllPaiement() {
        return paiementService.getAllPaiement();
    }

    @GetMapping("/recupererPaiement/{id}")
    public Paiement getPaiement(@PathVariable Long id) {
        return paiementService.getPaiement(id);
    }

    @DeleteMapping("/deletePaiementById/{id}")
    public void supprimerPaiementById(@PathVariable Long id) {
        paiementService.supprimerPaiementById(id);
    }
}
