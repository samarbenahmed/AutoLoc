package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.service.AgenceService;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final AgenceService agenceService;

    @GetMapping
    public ResponseEntity<List<Agence>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agence> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agenceService.findById(id));
    }

    @GetMapping("/ville/{ville}")
    public ResponseEntity<List<Agence>> findByVille(@PathVariable String ville) {
        return ResponseEntity.ok(agenceService.findByVille(ville));
    }

    @PostMapping
    public ResponseEntity<Agence> save(@RequestBody Agence agence) {
        return new ResponseEntity<>(agenceService.save(agence), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agence> update(@PathVariable Long id, @RequestBody Agence agence) {
        return ResponseEntity.ok(agenceService.update(id, agence));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}