package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceService {

    private final AgenceRepository agenceRepository;

    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agence " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Agence> findByVille(String ville) {
        return agenceRepository.findByVille(ville);
    }

    public Agence save(Agence agence) {
        valider(agence);
        if (agence.getIdAgence() == null && agenceRepository.existsByNom(agence.getNom())) {
            throw new ConflictException("Une agence nommée " + agence.getNom() + " existe déjà");
        }
        return agenceRepository.save(agence);
    }

    public Agence update(Long id, Agence agence) {
        findById(id);
        valider(agence);
        agence.setIdAgence(id);
        return agenceRepository.save(agence);
    }

    public void delete(Long id) {
        Agence agence = findById(id);
        if (!agence.getEmployes().isEmpty() || !agence.getVehicules().isEmpty()) {
            throw new ConflictException("Impossible de supprimer une agence ayant des employés ou des véhicules");
        }
        agenceRepository.delete(agence);
    }

    public Optional<Agence> findOptionalById(Long id) {
        return agenceRepository.findById(id);
    }

    private void valider(Agence agence) {
        if (agence == null) {
            throw new BadRequestException("L'agence ne peut pas être nulle");
        }
        if (isBlank(agence.getNom()) || isBlank(agence.getVille())) {
            throw new BadRequestException("Le nom et la ville de l'agence sont obligatoires");
        }
    }

    private boolean isBlank(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }
}