package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Contrat;

import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Optional<Contrat> findByReservation_IdReservation(Long reservationId);

    List<Contrat> findByValide(boolean valide);
}