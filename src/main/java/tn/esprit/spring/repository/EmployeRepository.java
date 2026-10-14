package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.entity.enums.RoleEmploye;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Long> {


}