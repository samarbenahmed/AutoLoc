package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class IAgenceServiceImp implements IAgenceService {

    private final AgenceRepository agenceRepository;

    @Override
    public Agence ajouterAgence(Agence agence) {
        valider(agence);
        return agenceRepository.save(agence);
    }

    @Override
    public Agence modifierAgence(Agence agence) {
        if (agence == null || agence.getIdAgence() == null) {
            throw new BadRequestException("L'identifiant de l'agence est obligatoire pour une modification");
        }
        getAgence(agence.getIdAgence());
        valider(agence);
        return agenceRepository.save(agence);
    }

    @Override
    public Agence getAgence(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de l'agence est obligatoire");
        }
        return agenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agence introuvable avec id " + id));
    }

    @Override
    public List<Agence> getAllAgence() {
        return agenceRepository.findAll();
    }

    @Override
    public void supprimerAgenceById(Long id) {
        getAgence(id);
        agenceRepository.deleteById(id);
    }

    private void valider(Agence agence) {
        if (agence == null) {
            throw new BadRequestException("Agence obligatoire");
        }
        if (estVide(agence.getNom())) {
            throw new BadRequestException("Le nom de l'agence est obligatoire");
        }
        if (estVide(agence.getVille())) {
            throw new BadRequestException("La ville de l'agence est obligatoire");
        }
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
