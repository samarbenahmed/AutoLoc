package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Contrat;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final IContratService contratService;

    public Contrat save(Contrat contrat) {
        return contratService.ajouterContrat(contrat);
    }

    public Contrat findById(Long id) {
        return contratService.getContrat(id);
    }
}
