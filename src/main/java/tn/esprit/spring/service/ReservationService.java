package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final IReservationService reservationService;

    public Reservation save(Reservation reservation) {
        return reservationService.ajouterReservation(reservation);
    }

    public Reservation findById(Long id) {
        return reservationService.getReservation(id);
    }

    public Reservation changerStatut(Long id, StatutReservation statut) {
        if (statut == null) {
            throw new BadRequestException("Le statut de la reservation est obligatoire");
        }
        Reservation reservation = reservationService.getReservation(id);
        reservation.setStatut(statut);
        return reservationService.modifierReservation(reservation);
    }
}
