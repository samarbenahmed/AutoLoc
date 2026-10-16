package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Client;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ClientRepository;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.ReservationRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class IReservationServiceImp implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ContratRepository contratRepository;

    @Override
    public Reservation ajouterReservation(Reservation reservation) {
        if (reservation == null) {
            throw new BadRequestException("Reservation obligatoire");
        }
        validerDates(reservation);
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.EN_ATTENTE);
        }
        Client client = resoudreClient(reservation);
        reservation.setClient(client);
        Vehicule vehicule = resoudreVehicule(reservation);
        reservation.setVehicule(vehicule);
        verifierVehiculeReservable(vehicule, reservation, null);
        synchroniserStatutVehicule(vehicule, reservation.getStatut());
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation modifierReservation(Reservation reservation) {
        if (reservation == null || reservation.getIdReservation() == null) {
            throw new BadRequestException("L'identifiant de la reservation est obligatoire pour une modification");
        }
        Reservation existante = getReservation(reservation.getIdReservation());
        validerDates(reservation);
        if (reservation.getStatut() == null) {
            reservation.setStatut(existante.getStatut());
        }
        if (reservation.getClient() == null) {
            reservation.setClient(existante.getClient());
        } else {
            reservation.setClient(resoudreClient(reservation));
        }
        if (reservation.getVehicule() == null) {
            reservation.setVehicule(existante.getVehicule());
        } else {
            reservation.setVehicule(resoudreVehicule(reservation));
        }
        verifierVehiculeReservable(reservation.getVehicule(), reservation, reservation.getIdReservation());
        synchroniserStatutVehicule(reservation.getVehicule(), reservation.getStatut());
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation getReservation(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant de la reservation est obligatoire");
        }
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation introuvable avec id " + id));
    }

    @Override
    public List<Reservation> getAllReservation() {
        return reservationRepository.findAll();
    }

    @Override
    public void supprimerReservationById(Long id) {
        Reservation reservation = getReservation(id);
        contratRepository.findByReservation_IdReservation(reservation.getIdReservation()).ifPresent(contrat -> {
            throw new ConflictException("La reservation " + reservation.getIdReservation()
                    + " est deja liee au contrat " + contrat.getIdContrat() + " : suppression impossible");
        });
        reservationRepository.deleteById(id);
    }

    private void validerDates(Reservation reservation) {
        if (reservation.getDateDebut() == null) {
            throw new BadRequestException("La date de debut de la reservation est obligatoire");
        }
        if (reservation.getDateFin() == null) {
            throw new BadRequestException("La date de fin de la reservation est obligatoire");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new BadRequestException(
                    "La date de fin doit etre posterieure ou egale a la date de debut");
        }
    }

    private Client resoudreClient(Reservation reservation) {
        if (reservation.getClient() == null || reservation.getClient().getIdClient() == null) {
            throw new BadRequestException("Client obligatoire : {\"client\":{\"idClient\":1}}");
        }
        Long idClient = reservation.getClient().getIdClient();
        return clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException("Client introuvable avec id " + idClient));
    }

    private Vehicule resoudreVehicule(Reservation reservation) {
        if (reservation.getVehicule() == null || reservation.getVehicule().getIdVehicule() == null) {
            throw new BadRequestException("Vehicule obligatoire : {\"vehicule\":{\"idVehicule\":1}}");
        }
        Long idVehicule = reservation.getVehicule().getIdVehicule();
        return vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new NotFoundException("Vehicule introuvable avec id " + idVehicule));
    }

    private void verifierVehiculeReservable(Vehicule vehicule, Reservation reservation, Long idIgnore) {
        if (!estStatutActif(reservation.getStatut())) {
            return;
        }
        if (vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            throw new ConflictException("Le vehicule " + vehicule.getImmatriculation()
                    + " est en maintenance : reservation impossible");
        }
        List<Reservation> conflits = (idIgnore == null
                ? reservationRepository.findConflictantes(vehicule.getIdVehicule(),
                        reservation.getDateDebut(), reservation.getDateFin())
                : reservationRepository.findConflictantesHorsReservation(vehicule.getIdVehicule(), idIgnore,
                        reservation.getDateDebut(), reservation.getDateFin()))
                .stream()
                .filter(autre -> estStatutActif(autre.getStatut()))
                .toList();
        if (!conflits.isEmpty()) {
            throw new ConflictException("Le vehicule est deja reserve sur cette periode (reservation "
                    + conflits.get(0).getIdReservation() + ")");
        }
    }

    private boolean estStatutActif(StatutReservation statut) {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }

    private void synchroniserStatutVehicule(Vehicule vehicule, StatutReservation statut) {
        if (statut == StatutReservation.CONFIRMEE && vehicule.getStatut() != StatutVehicule.MAINTENANCE) {
            vehicule.setStatut(StatutVehicule.LOUE);
            vehiculeRepository.save(vehicule);
        } else if ((statut == StatutReservation.ANNULEE || statut == StatutReservation.TERMINEE)
                && vehicule.getStatut() == StatutVehicule.LOUE) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }
    }
}
