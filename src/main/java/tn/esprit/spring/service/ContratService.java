package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Contrat;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.ReservationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratService {

    private final ContratRepository contratRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contrat " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Contrat> findByValide(boolean valide) {
        return contratRepository.findByValide(valide);
    }

    @Transactional(readOnly = true)
    public Contrat findByReservation_IdReservation(Long reservationId) {
        return contratRepository.findByReservation_IdReservation(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        "Aucun contrat pour la reservation " + reservationId));
    }

    public Contrat save(Contrat contrat) {
        Reservation reservation = valider(contrat, null);
        contrat.setReservation(reservation);
        contrat.setMontantTotal(resoudreMontantTotal(contrat, reservation));
        if (contrat.getDateSignature() == null) {
            contrat.setDateSignature(LocalDate.now());
        }
        contratRepository.save(contrat);
        reservation.setContrat(contrat);
        return contrat;
    }

    public Contrat update(Long id, Contrat contrat) {
        findById(id);
        Reservation reservation = valider(contrat, id);
        contrat.setIdContrat(id);
        contrat.setReservation(reservation);
        contrat.setMontantTotal(resoudreMontantTotal(contrat, reservation));
        Contrat enregistree = contratRepository.save(contrat);
        reservation.setContrat(enregistree);
        return enregistree;
    }

    public void delete(Long id) {
        Contrat contrat = findById(id);
        Reservation reservation = contrat.getReservation();
        contratRepository.delete(contrat);
        if (reservation != null) {
            reservation.setContrat(null);
        }
    }

    /**
     * Remplace la reference "reservation" recue dans le corps de la requete (qui ne contient
     * qu'un identifiant) par l'entite reellement geree. Indispensable car la relation
     * Contrat -> Reservation porte un CascadeType.ALL : une reference detachee provoquerait
     * une erreur "detached entity passed to persist".
     */
    private Reservation valider(Contrat contrat, Long idExistant) {
        if (contrat == null) {
            throw new BadRequestException("Le contrat ne peut pas etre nul");
        }
        if (contrat.getReservation() == null || contrat.getReservation().getIdReservation() == null) {
            throw new BadRequestException("La reservation du contrat est obligatoire");
        }
        Reservation reservation = reservationRepository.findById(contrat.getReservation().getIdReservation())
                .orElseThrow(() -> new NotFoundException(
                        "Reservation " + contrat.getReservation().getIdReservation() + " introuvable"));
        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new ConflictException("Une reservation ANNULEE ne peut pas generer de contrat");
        }
        Contrat existant = contratRepository.findByReservation_IdReservation(reservation.getIdReservation()).orElse(null);
        if (existant != null && !existant.getIdContrat().equals(idExistant)) {
            throw new ConflictException(
                    "Un contrat existe deja pour la reservation " + reservation.getIdReservation());
        }
        return reservation;
    }

    private BigDecimal resoudreMontantTotal(Contrat contrat, Reservation reservation) {
        if (contrat.getMontantTotal() != null
                && contrat.getMontantTotal().compareTo(BigDecimal.ZERO) >= 0) {
            return contrat.getMontantTotal();
        }
        BigDecimal tarif = reservation.getVehicule() == null
                ? null : reservation.getVehicule().getTarifJournalier();
        if (tarif == null) {
            throw new BadRequestException("Le montant total du contrat est obligatoire");
        }
        long nbJours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        if (nbJours < 1) {
            nbJours = 1;
        }
        return tarif.multiply(BigDecimal.valueOf(nbJours)).setScale(2, RoundingMode.HALF_UP);
    }
}