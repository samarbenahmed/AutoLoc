package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.CategorieVehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    List<Vehicule> findByStatut(StatutVehicule statut);

    List<Vehicule> findByCategorie(CategorieVehicule categorie);

    List<Vehicule> findByAgence_IdAgence(Long agenceId);
}