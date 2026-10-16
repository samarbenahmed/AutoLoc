package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Equipement;

@Service
@RequiredArgsConstructor
public class EquipementService {

    private final IEquipementService equipementService;

    public Equipement save(Equipement equipement) {
        return equipementService.ajouterEquipement(equipement);
    }

    public Equipement findById(Long id) {
        return equipementService.getEquipement(id);
    }
}
