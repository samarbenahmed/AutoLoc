package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Agence;

@Service
@RequiredArgsConstructor
public class AgenceService {

    private final IAgenceService agenceService;

    public Agence save(Agence agence) {
        return agenceService.ajouterAgence(agence);
    }

    public Agence findById(Long id) {
        return agenceService.getAgence(id);
    }
}
