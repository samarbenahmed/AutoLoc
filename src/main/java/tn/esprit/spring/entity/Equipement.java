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
@ToString(exclude = "vehicules")
@EqualsAndHashCode(exclude = "vehicules")
@Table(name = "equipement")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipement")
    private Long idEquipement;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Builder.Default
    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Vehicule> vehicules = new LinkedHashSet<>();
}