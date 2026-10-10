package tn.esprit.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "agence")
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agence")
    private Long idAgence;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "ville", nullable = false)
    private String ville;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone")
    private String telephone;

    @Builder.Default
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Employe> employes = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Vehicule> vehicules = new LinkedHashSet<>();

    public void addEmploye(Employe employe) {
        employes.add(employe);
        employe.setAgence(this);
    }

    public void addVehicule(Vehicule vehicule) {
        vehicules.add(vehicule);
        vehicule.setAgence(this);
    }
}