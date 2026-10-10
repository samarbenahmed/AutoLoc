package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.service.EquipementService;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final EquipementService equipementService;

    @GetMapping
    public ResponseEntity<List<Equipement>> findAll() {
        return ResponseEntity.ok(equipementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipement> findById(@PathVariable Long id) {
        return ResponseEntity.ok(equipementService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Equipement>> search(@RequestParam String libelle) {
        return ResponseEntity.ok(equipementService.searchByLibelle(libelle));
    }

    @PostMapping
    public ResponseEntity<Equipement> save(@RequestBody Equipement equipement) {
        return new ResponseEntity<>(equipementService.save(equipement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipement> update(@PathVariable Long id, @RequestBody Equipement equipement) {
        return ResponseEntity.ok(equipementService.update(id, equipement));
    }

    @PostMapping("/{equipementId}/vehicules/{vehiculeId}")
    public ResponseEntity<Void> ajouterAuVehicule(@PathVariable Long equipementId,
                                                   @PathVariable Long vehiculeId) {
        equipementService.ajouterAuVehicule(vehiculeId, equipementId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{equipementId}/vehicules/{vehiculeId}")
    public ResponseEntity<Void> retirerDuVehicule(@PathVariable Long equipementId,
                                                  @PathVariable Long vehiculeId) {
        equipementService.retirerDuVehicule(vehiculeId, equipementId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}