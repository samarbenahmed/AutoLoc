package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Agence;

import java.util.List;
import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, Long> {


}