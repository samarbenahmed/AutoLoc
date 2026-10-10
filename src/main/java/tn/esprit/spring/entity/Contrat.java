package tn.esprit.spring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"reservation", "paiements"})
@EqualsAndHashCode(exclude = {"reservation", "paiements"})
@Table(name = "contrat")
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrat")
    private Long idContrat;

    @Column(name = "date_signature")
    private LocalDate dateSignature;

    @Column(name = "montant_total", precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "valide", nullable = false)
    private boolean valide;


    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_contrat_reservation"))
    private Reservation reservation;

    @Builder.Default
    @OneToMany(mappedBy = "contrat", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Paiement> paiements = new LinkedHashSet<>();

    public void addPaiement(Paiement paiement) {
        paiements.add(paiement);
        paiement.setContrat(this);
    }

    public void removePaiement(Paiement paiement) {
        paiements.remove(paiement);
        paiement.setContrat(null);
    }
}