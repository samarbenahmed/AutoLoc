package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Agence;

import java.util.List;

public interface AgenceRepository extends JpaRepository<Agence, Long> {

    List<Agence> findByVille(String ville);

}
