package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.entity.enums.StatutVehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ClientRepository;
import tn.esprit.spring.repository.ReservationRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {


}