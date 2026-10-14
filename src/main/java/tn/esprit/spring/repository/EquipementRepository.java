package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Equipement;

import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {


}