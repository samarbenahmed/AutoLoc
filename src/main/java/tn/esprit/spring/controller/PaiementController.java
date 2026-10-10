package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Paiement;
import tn.esprit.spring.entity.enums.ModePaiement;
import tn.esprit.spring.service.PaiementService;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @GetMapping
    public ResponseEntity<List<Paiement>> findAll() {
        return ResponseEntity.ok(paiementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.findById(id));
    }

    @GetMapping("/contrat/{contratId}")
    public ResponseEntity<List<Paiement>> findByContrat(@PathVariable Long contratId) {
        return ResponseEntity.ok(paiementService.findByContrat_IdContrat(contratId));
    }

    @GetMapping("/mode/{modePaiement}")
    public ResponseEntity<List<Paiement>> findByMode(@PathVariable ModePaiement modePaiement) {
        return ResponseEntity.ok(paiementService.findByModePaiement(modePaiement));
    }

    @PostMapping
    public ResponseEntity<Paiement> save(@RequestBody Paiement paiement) {
        return new ResponseEntity<>(paiementService.save(paiement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> update(@PathVariable Long id, @RequestBody Paiement paiement) {
        return ResponseEntity.ok(paiementService.update(id, paiement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}