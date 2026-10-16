package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

@AllArgsConstructor
@Service
public class IContratServiceImp implements IContratService {

    private final ContratRepository contratRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Contrat ajouterContrat(Contrat contrat) {
        if (contrat == null) {
            throw new BadRequestException("Contrat obligatoire");
        }
        Reservation reservation = resoudreReservation(contrat);
        verifierContratUnique(reservation, null);
        if (contrat.getDateSignature() == null) {
            contrat.setDateSignature(LocalDate.now());
        }
        contrat.setMontantTotal(calculerMontantTotal(reservation, contrat.getMontantTotal()));
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat modifierContrat(Contrat contrat) {
        if (contrat == null || contrat.getIdContrat() == null) {
            throw new BadRequestException("L'identifiant du contrat est obligatoire pour une modification");
        }
        Contrat existant = getContrat(contrat.getIdContrat());
        if (contrat.getReservation() == null) {
            contrat.setReservation(existant.getReservation());
        }
        Reservation reservation = resoudreReservation(contrat);
        verifierContratUnique(reservation, contrat.getIdContrat());
        if (contrat.getDateSignature() == null) {
            contrat.setDateSignature(existant.getDateSignature());
        }
        if (contrat.getMontantTotal() == null) {
            contrat.setMontantTotal(calculerMontantTotal(reservation, null));
        }
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat getContrat(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant du contrat est obligatoire");
        }
        return contratRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contrat introuvable avec id " + id));
    }

    @Override
    public List<Contrat> getAllContrat() {
        return contratRepository.findAll();
    }

    @Override
    public void supprimerContratById(Long id) {
        getContrat(id);
        contratRepository.deleteById(id);
    }

    private Reservation resoudreReservation(Contrat contrat) {
        if (contrat.getReservation() == null || contrat.getReservation().getIdReservation() == null) {
            throw new BadRequestException("Reservation obligatoire : {\"reservation\":{\"idReservation\":1}}");
        }
        Long idReservation = contrat.getReservation().getIdReservation();
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new NotFoundException("Reservation introuvable avec id " + idReservation));
        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new ConflictException(
                    "Impossible de creer un contrat pour une reservation ANNULEE (id " + idReservation + ")");
        }
        contrat.setReservation(reservation);
        return reservation;
    }

    private void verifierContratUnique(Reservation reservation, Long idContratIgnore) {
        contratRepository.findByReservation_IdReservation(reservation.getIdReservation()).ifPresent(existant -> {
            if (idContratIgnore == null || !idContratIgnore.equals(existant.getIdContrat())) {
                throw new ConflictException(
                        "Un contrat existe deja pour la reservation " + reservation.getIdReservation());
            }
        });
    }

    private BigDecimal calculerMontantTotal(Reservation reservation, BigDecimal montantFourni) {
        BigDecimal tarif = reservation.getVehicule() == null ? null : reservation.getVehicule().getTarifJournalier();
        if (tarif == null) {
            if (montantFourni == null) {
                throw new BadRequestException(
                        "Le vehicule " + (reservation.getVehicule() == null ? "" : reservation.getVehicule().getIdVehicule())
                                + " n'a pas de tarif journalier : montant du contrat impossible a calculer");
            }
            return montantFourni;
        }
        long jours = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin()) + 1;
        return tarif.multiply(BigDecimal.valueOf(jours)).setScale(2, RoundingMode.HALF_UP);
    }
}
