package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.EmployeRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class IEmployeServiceImp implements IEmployeService {

    private final EmployeRepository employeRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public Employe ajouterEmploye(Employe employe) {
        validerEtResoudreAgence(employe);
        return employeRepository.save(employe);
    }

    @Override
    public Employe modifierEmploye(Employe employe) {
        if (employe == null || employe.getIdEmploye() == null) {
            throw new BadRequestException("L'identifiant de l'employe est obligatoire pour une modification");
        }
        getEmploye(employe.getIdEmploye());
        validerEtResoudreAgence(employe);
        return employeRepository.save(employe);
    }

    @Override
    public Employe getEmploye(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de l'employe est obligatoire");
        }
        return employeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employe introuvable avec id " + id));
    }

    @Override
    public List<Employe> getAllEmploye() {
        return employeRepository.findAll();
    }

    @Override
    public void supprimerEmployeById(Long id) {
        getEmploye(id);
        employeRepository.deleteById(id);
    }

    private void validerEtResoudreAgence(Employe employe) {
        if (employe == null) {
            throw new BadRequestException("Employe obligatoire");
        }
        if (estVide(employe.getNom())) {
            throw new BadRequestException("Le nom de l'employe est obligatoire");
        }
        if (estVide(employe.getPrenom())) {
            throw new BadRequestException("Le prenom de l'employe est obligatoire");
        }
        if (employe.getRole() == null) {
            throw new BadRequestException("Le role de l'employe est obligatoire (AGENT ou MANAGER)");
        }
        if (employe.getAgence() == null || employe.getAgence().getIdAgence() == null) {
            throw new BadRequestException("Agence obligatoire : {\"agence\":{\"idAgence\":1}}");
        }
        Long idAgence = employe.getAgence().getIdAgence();
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new NotFoundException("Agence introuvable avec id " + idAgence));
        employe.setAgence(agence);
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
