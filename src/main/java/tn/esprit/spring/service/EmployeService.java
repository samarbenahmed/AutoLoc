package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Employe;
import tn.esprit.spring.entity.enums.RoleEmploye;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.AgenceRepository;
import tn.esprit.spring.repository.EmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeService {


    }
