package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Contrat;
import tn.esprit.spring.entity.Paiement;
import tn.esprit.spring.entity.enums.ModePaiement;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.PaiementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ContratRepository contratRepository;

    @Transactional(readOnly = true)
    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paiement findById(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paiement " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Paiement> findByContrat_IdContrat(Long contratId) {
        return paiementRepository.findByContrat_IdContrat(contratId);
    }

    @Transactional(readOnly = true)
    public List<Paiement> findByModePaiement(ModePaiement modePaiement) {
        return paiementRepository.findByModePaiement(modePaiement);
    }

    public Paiement save(Paiement paiement) {
        Contrat contrat = valider(paiement, null);
        paiement.setContrat(contrat);
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDate.now());
        }
        verifierPlafond(contrat, paiement.getMontant(), null);
        Paiement enregistre = paiementRepository.save(paiement);
        contrat.getPaiements().add(enregistre);
        contratRepository.save(contrat);
        return enregistre;
    }

    public Paiement update(Long id, Paiement paiement) {
        Paiement existant = findById(id);
        Contrat contrat = valider(paiement, id);
        paiement.setIdPaiement(id);
        paiement.setContrat(contrat);
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDate.now());
        }
        verifierPlafond(contrat, paiement.getMontant(), id);
        Paiement enregistre = paiementRepository.save(paiement);
        contrat.getPaiements().remove(existant);
        contrat.getPaiements().add(enregistre);
        contratRepository.save(contrat);
        return enregistre;
    }

    public void delete(Long id) {
        Paiement paiement = findById(id);
        Contrat contrat = paiement.getContrat();
        contrat.getPaiements().remove(paiement);
        paiement.setContrat(null);
        paiementRepository.delete(paiement);
        if (contrat != null) {
            contratRepository.save(contrat);
        }
    }

    private Contrat valider(Paiement paiement, Long idExistant) {
        if (paiement == null) {
            throw new BadRequestException("Le paiement ne peut pas etre nul");
        }
        if (paiement.getMontant() == null
                || paiement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant du paiement doit etre strictement positif");
        }
        if (paiement.getModePaiement() == null) {
            throw new BadRequestException("Le mode de paiement est obligatoire");
        }
        if (paiement.getContrat() == null || paiement.getContrat().getIdContrat() == null) {
            throw new BadRequestException("Le contrat du paiement est obligatoire");
        }
        Contrat contrat = contratRepository.findById(paiement.getContrat().getIdContrat())
                .orElseThrow(() -> new NotFoundException(
                        "Contrat " + paiement.getContrat().getIdContrat() + " introuvable"));
        if (contrat.getReservation() != null
                && contrat.getReservation().getStatut() == StatutReservation.ANNULEE) {
            throw new ConflictException("Un paiement ne peut pas etre enregistre sur un contrat annule");
        }
        return contrat;
    }

    private void verifierPlafond(Contrat contrat, BigDecimal montant, Long idPaiementExistant) {
        if (contrat.getMontantTotal() == null) {
            return;
        }
        BigDecimal dejaPaye = paiementRepository.findByContrat_IdContrat(contrat.getIdContrat()).stream()
                .filter(p -> idPaiementExistant == null || !p.getIdPaiement().equals(idPaiementExistant))
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (dejaPaye.add(montant).compareTo(contrat.getMontantTotal()) > 0) {
            throw new ConflictException("Le cumul des paiements depasse le montant total du contrat");
        }
    }
}