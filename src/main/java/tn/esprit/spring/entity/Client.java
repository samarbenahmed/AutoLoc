package tn.esprit.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "reservations")
@EqualsAndHashCode(exclude = "reservations")
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long idClient;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "num_permis", unique = true)
    private String numPermis;

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    @Builder.Default
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Reservation> reservations = new LinkedHashSet<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setClient(this);
    }
}