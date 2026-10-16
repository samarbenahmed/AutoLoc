package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Employe;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final IEmployeService employeService;

    public Employe save(Employe employe) {
        return employeService.ajouterEmploye(employe);
    }

    public Employe findById(Long id) {
        return employeService.getEmploye(id);
    }
}
