package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.entity.enums.RoleEmploye;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.EmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final AgenceRepository agenceRepository;

    @Transactional(readOnly = true)
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employe " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Employe> findByAgence_IdAgence(Long agenceId) {
        return employeRepository.findByAgence_IdAgence(agenceId);
    }

    @Transactional(readOnly = true)
    public List<Employe> findByRole(RoleEmploye role) {
        return employeRepository.findByRole(role);
    }

    public Employe save(Employe employe) {
        valider(employe);
        employe.setAgence(agenceRepository.findById(employe.getAgence().getIdAgence())
                .orElseThrow(() -> new NotFoundException(
                        "Agence " + employe.getAgence().getIdAgence() + " introuvable")));
        if (employe.getIdEmploye() == null
                && employeRepository.existsByNomAndPrenomAndAgence_IdAgence(
                        employe.getNom(), employe.getPrenom(), employe.getAgence().getIdAgence())) {
            throw new ConflictException("Un employe avec ce nom et ce prenom existe deja dans cette agence");
        }
        return employeRepository.save(employe);
    }

    public Employe update(Long id, Employe employe) {
        findById(id);
        valider(employe);
        employe.setIdEmploye(id);
        employe.setAgence(agenceRepository.findById(employe.getAgence().getIdAgence())
                .orElseThrow(() -> new NotFoundException(
                        "Agence " + employe.getAgence().getIdAgence() + " introuvable")));
        return employeRepository.save(employe);
    }

    public void delete(Long id) {
        employeRepository.delete(findById(id));
    }

    private void valider(Employe employe) {
        if (employe == null) {
            throw new BadRequestException("L'employe ne peut pas etre nul");
        }
        if (isBlank(employe.getNom()) || isBlank(employe.getPrenom())) {
            throw new BadRequestException("Le nom et le prenom de l'employe sont obligatoires");
        }
        if (employe.getRole() == null) {
            throw new BadRequestException("Le role de l'employe est obligatoire");
        }
        if (employe.getAgence() == null || employe.getAgence().getIdAgence() == null) {
            throw new BadRequestException("L'agence de l'employe est obligatoire");
        }
        if (!agenceRepository.existsById(employe.getAgence().getIdAgence())) {
            throw new NotFoundException("Agence " + employe.getAgence().getIdAgence() + " introuvable");
        }
    }

    private boolean isBlank(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }
}