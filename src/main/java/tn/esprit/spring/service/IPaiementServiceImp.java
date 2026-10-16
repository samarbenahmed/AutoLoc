package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Contrat;
import tn.esprit.spring.entity.Paiement;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.PaiementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class IPaiementServiceImp implements IPaiementService {

    private final PaiementRepository paiementRepository;
    private final ContratRepository contratRepository;

    @Override
    public Paiement ajouterPaiement(Paiement paiement) {
        if (paiement == null) {
            throw new BadRequestException("Paiement obligatoire");
        }
        Contrat contrat = resoudreContrat(paiement);
        validerMontantEtCumul(paiement, contrat, null);
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement modifierPaiement(Paiement paiement) {
        if (paiement == null || paiement.getIdPaiement() == null) {
            throw new BadRequestException("L'identifiant du paiement est obligatoire pour une modification");
        }
        Paiement existant = getPaiement(paiement.getIdPaiement());
        if (paiement.getContrat() == null) {
            paiement.setContrat(existant.getContrat());
        }
        Contrat contrat = resoudreContrat(paiement);
        validerMontantEtCumul(paiement, contrat, paiement.getIdPaiement());
        return paiementRepository.save(paiement);
    }

    @Override
    public Paiement getPaiement(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant du paiement est obligatoire");
        }
        return paiementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paiement introuvable avec id " + id));
    }

    @Override
    public List<Paiement> getAllPaiement() {
        return paiementRepository.findAll();
    }

    @Override
    public void supprimerPaiementById(Long id) {
        getPaiement(id);
        paiementRepository.deleteById(id);
    }

    private Contrat resoudreContrat(Paiement paiement) {
        if (paiement.getContrat() == null || paiement.getContrat().getIdContrat() == null) {
            throw new BadRequestException("Contrat obligatoire : {\"contrat\":{\"idContrat\":1}}");
        }
        Long idContrat = paiement.getContrat().getIdContrat();
        Contrat contrat = contratRepository.findById(idContrat)
                .orElseThrow(() -> new NotFoundException("Contrat introuvable avec id " + idContrat));
        paiement.setContrat(contrat);
        return contrat;
    }

    private void validerMontantEtCumul(Paiement paiement, Contrat contrat, Long idPaiementIgnore) {
        BigDecimal montant = paiement.getMontant();
        if (montant == null) {
            throw new BadRequestException("Le montant du paiement est obligatoire");
        }
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du paiement doit etre strictement positif");
        }
        if (paiement.getModePaiement() == null) {
            throw new BadRequestException("Le mode de paiement est obligatoire (CARTE, ESPECES ou VIREMENT)");
        }
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDate.now());
        }
        if (contrat.getMontantTotal() == null) {
            return;
        }
        BigDecimal cumul = paiementRepository.findByContrat_IdContrat(contrat.getIdContrat()).stream()
                .filter(existant -> idPaiementIgnore == null || !idPaiementIgnore.equals(existant.getIdPaiement()))
                .map(Paiement::getMontant)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (cumul.add(montant).compareTo(contrat.getMontantTotal()) > 0) {
            throw new ConflictException("Le cumul des paiements depasse le montant total du contrat ("
                    + cumul.add(montant) + " > " + contrat.getMontantTotal() + ")");
        }
    }
}
