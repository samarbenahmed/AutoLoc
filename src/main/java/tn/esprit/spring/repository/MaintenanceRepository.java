package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Maintenance;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {



}