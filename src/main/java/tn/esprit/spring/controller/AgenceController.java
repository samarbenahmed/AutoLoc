package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.service.IAgenceService;

import java.util.List;

@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final IAgenceService agenceService;

    @PostMapping("/ajouterAgence")
    public Agence ajouterAgence(@RequestBody Agence agence) {
        return agenceService.ajouterAgence(agence);
    }

    @PutMapping("/modifierAgence")
    public Agence modifierAgence(@RequestBody Agence agence) {
        return agenceService.modifierAgence(agence);
    }

    @GetMapping("/recupererTousAgences")
    public List<Agence> getAllAgence() {
        return agenceService.getAllAgence();
    }

    @GetMapping("/recupererAgence/{id}")
    public Agence getAgence(@PathVariable Long id) {
        return agenceService.getAgence(id);
    }

    @DeleteMapping("/deleteAgenceById/{id}")
    public void supprimerAgenceById(@PathVariable Long id) {
        agenceService.supprimerAgenceById(id);
    }
}
