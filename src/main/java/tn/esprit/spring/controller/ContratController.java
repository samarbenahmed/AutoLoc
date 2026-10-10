package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Contrat;
import tn.esprit.spring.service.ContratService;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final ContratService contratService;

    @GetMapping
    public ResponseEntity<List<Contrat>> findAll() {
        return ResponseEntity.ok(contratService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrat> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contratService.findById(id));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<Contrat> findByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(contratService.findByReservation_IdReservation(reservationId));
    }

    @GetMapping("/valide/{valide}")
    public ResponseEntity<List<Contrat>> findByValide(@PathVariable boolean valide) {
        return ResponseEntity.ok(contratService.findByValide(valide));
    }

    @PostMapping
    public ResponseEntity<Contrat> save(@RequestBody Contrat contrat) {
        return new ResponseEntity<>(contratService.save(contrat), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contrat> update(@PathVariable Long id, @RequestBody Contrat contrat) {
        return ResponseEntity.ok(contratService.update(id, contrat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contratService.delete(id);
        return ResponseEntity.noContent().build();
    }
}