package tn.esprit.spring.service;

import tn.esprit.spring.entity.Reservation;

import java.util.List;

public interface IReservationService {

    Reservation ajouterReservation(Reservation reservation);

    Reservation modifierReservation(Reservation reservation);

    Reservation getReservation(Long id);

    List<Reservation> getAllReservation();

    void supprimerReservationById(Long id);

}
