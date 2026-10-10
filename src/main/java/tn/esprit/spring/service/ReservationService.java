package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ClientRepository;
import tn.esprit.spring.repository.ReservationRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByClient_IdClient(Long clientId) {
        return reservationRepository.findByClient_IdClient(clientId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByVehicule_IdVehicule(Long vehiculeId) {
        return reservationRepository.findByVehicule_IdVehicule(vehiculeId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    public Reservation save(Reservation reservation) {
        valider(reservation, null);
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.EN_ATTENTE);
        }
        chargerReferences(reservation);
        Reservation enregistree = reservationRepository.save(reservation);
        if (enregistree.getStatut() == StatutReservation.CONFIRMEE) {
            majStatutVehicule(enregistree.getVehicule(), StatutVehicule.LOUE);
        }
        return enregistree;
    }

    public Reservation update(Long id, Reservation reservation) {
        findById(id);
        valider(reservation, id);
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.EN_ATTENTE);
        }
        reservation.setIdReservation(id);
        chargerReferences(reservation);
        Reservation enregistree = reservationRepository.save(reservation);
        appliquerStatutVehicule(enregistree);
        return enregistree;
    }

    public Reservation changerStatut(Long id, StatutReservation statut) {
        if (statut == null) {
            throw new BadRequestException("Le statut de la reservation est obligatoire");
        }
        Reservation reservation = findById(id);
        reservation.setStatut(statut);
        Reservation enregistree = reservationRepository.save(reservation);
        appliquerStatutVehicule(enregistree);
        return enregistree;
    }

    public void delete(Long id) {
        Reservation reservation = findById(id);
        if (reservation.getContrat() != null) {
            throw new ConflictException("Impossible de supprimer une reservation possessing un contrat");
        }
        reservationRepository.delete(reservation);
        recalculerStatutVehicule(reservation.getVehicule());
    }

    /**
     * Remplace les objets "de reference" recus dans le corps de la requete (qui ne contiennent
     * qu'un id) par les entites reellement gerees, afin d'eviter les detached entity
     * et de renvoyer des objets complets dans le JSON de reponse.
     */
    private void chargerReferences(Reservation reservation) {
        reservation.setClient(clientRepository.findById(reservation.getClient().getIdClient())
                .orElseThrow(() -> new NotFoundException(
                        "Client " + reservation.getClient().getIdClient() + " introuvable")));
        reservation.setVehicule(vehiculeRepository.findById(reservation.getVehicule().getIdVehicule())
                .orElseThrow(() -> new NotFoundException(
                        "Vehicule " + reservation.getVehicule().getIdVehicule() + " introuvable")));
    }

    private void appliquerStatutVehicule(Reservation reservation) {
        if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
            majStatutVehicule(reservation.getVehicule(), StatutVehicule.LOUE);
        } else {
            recalculerStatutVehicule(reservation.getVehicule());
        }
    }

    private void recalculerStatutVehicule(Vehicule vehicule) {
        if (vehicule == null || vehicule.getIdVehicule() == null) {
            return;
        }
        Vehicule managed = vehiculeRepository.findById(vehicule.getIdVehicule()).orElse(null);
        if (managed == null || managed.getStatut() == StatutVehicule.MAINTENANCE) {
            return;
        }
        boolean locationActive = reservationRepository
                .findByVehicule_IdVehiculeAndStatut(managed.getIdVehicule(), StatutReservation.CONFIRMEE)
                .isPresent();
        majStatutVehicule(managed, locationActive ? StatutVehicule.LOUE : StatutVehicule.DISPONIBLE);
    }

    private void majStatutVehicule(Vehicule vehicule, StatutVehicule statut) {
        if (vehicule == null || vehicule.getIdVehicule() == null) {
            return;
        }
        Vehicule managed = vehiculeRepository.findById(vehicule.getIdVehicule()).orElse(null);
        if (managed != null && managed.getStatut() != statut) {
            managed.setStatut(statut);
            vehiculeRepository.save(managed);
        }
    }

    private void valider(Reservation reservation, Long idExistant) {
        if (reservation == null) {
            throw new BadRequestException("La reservation ne peut pas etre nulle");
        }
        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new BadRequestException("Les dates de debut et de fin sont obligatoires");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new BadRequestException("La date de fin doit etre posterieure a la date de debut");
        }
        if (reservation.getClient() == null || reservation.getClient().getIdClient() == null) {
            throw new BadRequestException("Le client de la reservation est obligatoire");
        }
        if (!clientRepository.existsById(reservation.getClient().getIdClient())) {
            throw new NotFoundException("Client " + reservation.getClient().getIdClient() + " introuvable");
        }
        if (reservation.getVehicule() == null || reservation.getVehicule().getIdVehicule() == null) {
            throw new BadRequestException("Le vehicule de la reservation est obligatoire");
        }
        Long vehiculeId = reservation.getVehicule().getIdVehicule();
        if (!vehiculeRepository.existsById(vehiculeId)) {
            throw new NotFoundException("Vehicule " + vehiculeId + " introuvable");
        }
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId).orElseThrow();
        if (vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            throw new ConflictException("Le vehicule " + vehiculeId
                    + " est en maintenance et ne peut pas etre reserve");
        }
        List<Reservation> conflitantes = idExistant == null
                ? reservationRepository.findConflictantes(vehiculeId,
                        reservation.getDateDebut(), reservation.getDateFin())
                : reservationRepository.findConflictantesHorsReservation(vehiculeId, idExistant,
                        reservation.getDateDebut(), reservation.getDateFin());
        if (!conflitantes.isEmpty()) {
            throw new ConflictException("Le vehicule " + vehiculeId
                    + " est deja reserve sur une periode incompatible");
        }
    }
}