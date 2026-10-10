package tn.esprit.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.spring.entity.enums.CategorieVehicule;
import tn.esprit.spring.entity.enums.StatutVehicule;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"agence", "maintenances", "reservations", "equipements"})
@EqualsAndHashCode(exclude = {"agence", "maintenances", "reservations", "equipements"})
@Table(name = "vehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicule")
    private Long idVehicule;

    @Column(name = "immatriculation", nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(name = "marque", nullable = false)
    private String marque;

    @Column(name = "modele")
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", length = 20)
    private CategorieVehicule categorie;

    @Column(name = "tarif_journalier", precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutVehicule statut;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "agence_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicule_agence"))
    private Agence agence;

    @Builder.Default
    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Maintenance> maintenances = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "vehicule_id",
                    foreignKey = @ForeignKey(name = "fk_vehicule_equipement_vehicule")),
            inverseJoinColumns = @JoinColumn(name = "equipement_id",
                    foreignKey = @ForeignKey(name = "fk_vehicule_equipement_equipement")))
    @JsonIgnore
    private Set<Equipement> equipements = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Reservation> reservations = new LinkedHashSet<>();

    public void addMaintenance(Maintenance maintenance) {
        maintenances.add(maintenance);
        maintenance.setVehicule(this);
    }

    public void addEquipement(Equipement equipement) {
        equipements.add(equipement);
        equipement.getVehicules().add(this);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setVehicule(this);
    }
}