package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Contrat;
import tn.esprit.spring.service.IContratService;

import java.util.List;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @PostMapping("/ajouterContrat")
    public Contrat ajouterContrat(@RequestBody Contrat contrat) {
        return contratService.ajouterContrat(contrat);
    }

    @PutMapping("/modifierContrat")
    public Contrat modifierContrat(@RequestBody Contrat contrat) {
        return contratService.modifierContrat(contrat);
    }

    @GetMapping("/recupererTousContrats")
    public List<Contrat> getAllContrat() {
        return contratService.getAllContrat();
    }

    @GetMapping("/recupererContrat/{id}")
    public Contrat getContrat(@PathVariable Long id) {
        return contratService.getContrat(id);
    }

    @DeleteMapping("/deleteContratById/{id}")
    public void supprimerContratById(@PathVariable Long id) {
        contratService.supprimerContratById(id);
    }
}
