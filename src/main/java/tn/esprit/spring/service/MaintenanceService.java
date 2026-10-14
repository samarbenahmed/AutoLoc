package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Maintenance;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.MaintenanceRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {


}