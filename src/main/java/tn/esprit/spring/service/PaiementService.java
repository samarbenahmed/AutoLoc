package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Paiement;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final IPaiementService paiementService;

    public Paiement save(Paiement paiement) {
        return paiementService.ajouterPaiement(paiement);
    }

    public Paiement findById(Long id) {
        return paiementService.getPaiement(id);
    }
}
