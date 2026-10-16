package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r " +
            "where r.vehicule.idVehicule = :idVehicule " +
            "and r.dateDebut <= :dateFin and r.dateFin >= :dateDebut")
    List<Reservation> findConflictantes(@Param("idVehicule") Long idVehicule,
                                        @Param("dateDebut") LocalDate dateDebut,
                                        @Param("dateFin") LocalDate dateFin);

    @Query("select r from Reservation r " +
            "where r.vehicule.idVehicule = :idVehicule " +
            "and r.dateDebut <= :dateFin and r.dateFin >= :dateDebut " +
            "and r.idReservation <> :idReservation")
    List<Reservation> findConflictantesHorsReservation(@Param("idVehicule") Long idVehicule,
                                                       @Param("idReservation") Long idReservation,
                                                       @Param("dateDebut") LocalDate dateDebut,
                                                       @Param("dateFin") LocalDate dateFin);

}
