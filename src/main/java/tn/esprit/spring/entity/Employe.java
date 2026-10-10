package tn.esprit.spring.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.spring.entity.enums.RoleEmploye;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "agence")
@EqualsAndHashCode(exclude = "agence")
@Table(name = "employe")
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employe")
    private Long idEmploye;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private RoleEmploye role;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "agence_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_employe_agence"))
    private Agence agence;
}