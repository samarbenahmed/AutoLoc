package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.enums.StatutReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClient_IdClient(Long clientId);

    List<Reservation> findByVehicule_IdVehicule(Long vehiculeId);

    List<Reservation> findByStatut(StatutReservation statut);

    Optional<Reservation> findByVehicule_IdVehiculeAndStatut(Long vehiculeId, StatutReservation statut);

    Optional<Reservation> findByContrat_IdContrat(Long contratId);

    @Query("""
            select r from Reservation r
            where r.vehicule.idVehicule = :vehiculeId
              and r.statut <> tn.esprit.spring.entity.enums.StatutReservation.ANNULEE
              and r.dateDebut <= :dateFin
              and r.dateFin >= :dateDebut
            """)
    List<Reservation> findConflictantes(@Param("vehiculeId") Long vehiculeId,
                                        @Param("dateDebut") LocalDate dateDebut,
                                        @Param("dateFin") LocalDate dateFin);

    @Query("""
            select r from Reservation r
            where r.vehicule.idVehicule = :vehiculeId
              and r.idReservation <> :reservationId
              and r.statut <> tn.esprit.spring.entity.enums.StatutReservation.ANNULEE
              and r.dateDebut <= :dateFin
              and r.dateFin >= :dateDebut
            """)
    List<Reservation> findConflictantesHorsReservation(@Param("vehiculeId") Long vehiculeId,
                                                      @Param("reservationId") Long reservationId,
                                                      @Param("dateDebut") LocalDate dateDebut,
                                                      @Param("dateFin") LocalDate dateFin);
}