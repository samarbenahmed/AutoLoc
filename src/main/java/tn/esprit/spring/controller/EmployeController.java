package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.service.IEmployeService;

import java.util.List;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final IEmployeService employeService;

    @PostMapping("/ajouterEmploye")
    public Employe ajouterEmploye(@RequestBody Employe employe) {
        return employeService.ajouterEmploye(employe);
    }

    @PutMapping("/modifierEmploye")
    public Employe modifierEmploye(@RequestBody Employe employe) {
        return employeService.modifierEmploye(employe);
    }

    @GetMapping("/recupererTousEmployes")
    public List<Employe> getAllEmploye() {
        return employeService.getAllEmploye();
    }

    @GetMapping("/recupererEmploye/{id}")
    public Employe getEmploye(@PathVariable Long id) {
        return employeService.getEmploye(id);
    }

    @DeleteMapping("/deleteEmployeById/{id}")
    public void supprimerEmployeById(@PathVariable Long id) {
        employeService.supprimerEmployeById(id);
    }
}
