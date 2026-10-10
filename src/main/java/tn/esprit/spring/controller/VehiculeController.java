package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.CategorieVehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.service.VehiculeService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @GetMapping
    public ResponseEntity<List<Vehicule>> findAll() {
        return ResponseEntity.ok(vehiculeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    @GetMapping("/immatriculation/{immatriculation}")
    public ResponseEntity<Vehicule> findByImmatriculation(@PathVariable String immatriculation) {
        return ResponseEntity.ok(vehiculeService.findByImmatriculation(immatriculation));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Vehicule>> findByStatut(@PathVariable StatutVehicule statut) {
        return ResponseEntity.ok(vehiculeService.findByStatut(statut));
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Vehicule>> findByCategorie(@PathVariable CategorieVehicule categorie) {
        return ResponseEntity.ok(vehiculeService.findByCategorie(categorie));
    }

    @GetMapping("/agence/{agenceId}")
    public ResponseEntity<List<Vehicule>> findByAgence(@PathVariable Long agenceId) {
        return ResponseEntity.ok(vehiculeService.findByAgence_IdAgence(agenceId));
    }

    @PostMapping
    public ResponseEntity<Vehicule> save(@RequestBody Vehicule vehicule) {
        return new ResponseEntity<>(vehiculeService.save(vehicule), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return ResponseEntity.ok(vehiculeService.update(id, vehicule));
    }

    @PutMapping("/{id}/statut/{statut}")
    public ResponseEntity<Vehicule> changerStatut(@PathVariable Long id,
                                                 @PathVariable StatutVehicule statut) {
        return ResponseEntity.ok(vehiculeService.changerStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}