package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.entity.enums.RoleEmploye;
import tn.esprit.spring.service.EmployeService;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;

    @GetMapping
    public ResponseEntity<List<Employe>> findAll() {
        return ResponseEntity.ok(employeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @GetMapping("/agence/{agenceId}")
    public ResponseEntity<List<Employe>> findByAgence(@PathVariable Long agenceId) {
        return ResponseEntity.ok(employeService.findByAgence_IdAgence(agenceId));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Employe>> findByRole(@PathVariable RoleEmploye role) {
        return ResponseEntity.ok(employeService.findByRole(role));
    }

    @PostMapping
    public ResponseEntity<Employe> save(@RequestBody Employe employe) {
        return new ResponseEntity<>(employeService.save(employe), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> update(@PathVariable Long id, @RequestBody Employe employe) {
        return ResponseEntity.ok(employeService.update(id, employe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}