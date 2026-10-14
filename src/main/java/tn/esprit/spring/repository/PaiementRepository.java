package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Paiement;
import tn.esprit.spring.entity.enums.ModePaiement;

import java.time.LocalDate;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

}