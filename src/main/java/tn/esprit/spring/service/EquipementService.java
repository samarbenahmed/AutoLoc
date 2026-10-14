package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.entity.Vehicule;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.EquipementRepository;
import tn.esprit.spring.repository.VehiculeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipementService {


}