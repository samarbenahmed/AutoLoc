package tn.esprit.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.autolocsamarbenahmed.AutoLocSamarBenahmedApplication;
import tn.esprit.spring.entity.*;
import tn.esprit.spring.entity.enums.*;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regles metier : vehicule en maintenance non reservable, pas de double reservation
 * sur une periode incompatible, pas de contrat ni de paiement pour une reservation annulee.
 */
@SpringBootTest(classes = AutoLocSamarBenahmedApplication.class)
@Transactional
class ReservationServiceTest {

    @Autowired private ReservationService reservationService;
    @Autowired private ContratService contratService;
    @Autowired private PaiementService paiementService;
    @Autowired private VehiculeService vehiculeService;
    @Autowired private MaintenanceService maintenanceService;
    @Autowired private ClientService clientService;
    @Autowired private AgenceService agenceService;

    private Agence agence;
    private Vehicule vehicule;
    private Client client;

    @BeforeEach
    void initialiser() {
        agence = agenceService.save(Agence.builder()
                .nom("Agence Test").ville("Tunis").adresse("1 rue test").telephone("20000000").build());
        vehicule = vehiculeService.save(Vehicule.builder()
                .immatriculation("111 Tunis 111").marque("Kia").modele("Picanto")
                .categorie(CategorieVehicule.CITADINE).tarifJournalier(new BigDecimal("40.00"))
                .statut(StatutVehicule.DISPONIBLE).agence(agence).build());
        client = clientService.save(Client.builder()
                .nom("Gharbi").prenom("Rim").email("rim.gharbi@email.tn").build());
    }

    private Reservation nouvelleReservation(LocalDate debut, LocalDate fin) {
        return Reservation.builder().dateDebut(debut).dateFin(fin)
                .client(client).vehicule(vehicule).build();
    }

    @Test
    void uneReservationPrendParDefautLeStatutEnAttente() {
        Reservation reservation = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3)));

        assertThat(reservation.getIdReservation()).isNotNull();
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE);
        assertThat(vehiculeService.findById(vehicule.getIdVehicule()).getStatut())
                .isEqualTo(StatutVehicule.DISPONIBLE);
    }

    @Test
    void unVehiculeEnMaintenanceNePeutPasEtreReserve() {
        maintenanceService.save(Maintenance.builder()
                .dateDebut(LocalDate.of(2026, 3, 1)).dateFin(LocalDate.of(2026, 3, 31))
                .description("Vidange").vehicule(vehicule).build());

        assertThat(vehiculeService.findById(vehicule.getIdVehicule()).getStatut())
                .isEqualTo(StatutVehicule.MAINTENANCE);

        assertThatThrownBy(() -> reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 3))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("maintenance");
    }

    @Test
    void uneDeuxiemeReservationSurUnePeriodeIncompatibleEstRefusee() {
        reservationService.save(nouvelleReservation(
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 20)));

        assertThatThrownBy(() -> reservationService.save(nouvelleReservation(
                LocalDate.of(2026, 5, 15), LocalDate.of(2026, 5, 25))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("deja reserve");
    }

    @Test
    void uneReservationAnnuleeLibereLeVehicule() {
        Reservation reservation = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4)));
        reservationService.changerStatut(reservation.getIdReservation(), StatutReservation.CONFIRMEE);
        assertThat(vehiculeService.findById(vehicule.getIdVehicule()).getStatut())
                .isEqualTo(StatutVehicule.LOUE);

        reservationService.changerStatut(reservation.getIdReservation(), StatutReservation.ANNULEE);
        assertThat(vehiculeService.findById(vehicule.getIdVehicule()).getStatut())
                .isEqualTo(StatutVehicule.DISPONIBLE);

        // le vehicule redevient reservable sur une autre periode
        Reservation seconde = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4)));
        assertThat(seconde.getIdReservation()).isNotNull();
    }

    @Test
    void uneDateDeFinAnterieureALaDateDeDebutEstRefusee() {
        assertThatThrownBy(() -> reservationService.save(nouvelleReservation(
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 1))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("posterieure");
    }

    @Test
    void uneReservationAnnuleeNeGenereNiContratNiPaiement() {
        Reservation reservation = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5)));
        reservationService.changerStatut(reservation.getIdReservation(), StatutReservation.ANNULEE);

        Contrat contrat = Contrat.builder().reservation(reservation).valide(true).build();
        assertThatThrownBy(() -> contratService.save(contrat))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ANNULEE");
    }

    @Test
    void leMontantDuContratEstCalculeAutomatiquePuisLesPaiementsSontEncadres() {
        Reservation reservation = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 4)));
        reservationService.changerStatut(reservation.getIdReservation(), StatutReservation.CONFIRMEE);

        Contrat contrat = contratService.save(
                Contrat.builder().reservation(reservation).valide(true).build());
        // 4 jours x 40.00
        assertThat(contrat.getMontantTotal()).isEqualByComparingTo("160.00");

        Paiement paiement = paiementService.save(Paiement.builder()
                .montant(new BigDecimal("60.00")).modePaiement(ModePaiement.ESPECES)
                .contrat(contrat).build());
        assertThat(paiement.getDatePaiement()).isNotNull();

        // le cumul ne peut pas depasser le montant total
        Paiement trop = Paiement.builder()
                .montant(new BigDecimal("200.00")).modePaiement(ModePaiement.CARTE)
                .contrat(contrat).build();
        assertThatThrownBy(() -> paiementService.save(trop))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("depasse");
    }

    @Test
    void unMontantNegatifOuNulEstRefuse() {
        Reservation reservation = reservationService.save(
                nouvelleReservation(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 2)));
        Contrat contrat = contratService.save(
                Contrat.builder().reservation(reservation).valide(true).build());

        assertThatThrownBy(() -> paiementService.save(Paiement.builder()
                .montant(BigDecimal.ZERO).modePaiement(ModePaiement.CARTE).contrat(contrat).build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("positif");
    }

    @Test
    void unIdentifiantInexistantLeveUneNotFound() {
        assertThatThrownBy(() -> reservationService.findById(999999L))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> clientService.findById(999999L))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> vehiculeService.findById(999999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void unEmailInvalideEstRefuse() {
        assertThatThrownBy(() -> clientService.save(Client.builder()
                .nom("Test").prenom("Email").email("pas-un-email").build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("email");
    }

    @Test
    void unTarifJournalierNonPositifEstRefuse() {
        assertThatThrownBy(() -> vehiculeService.save(Vehicule.builder()
                .immatriculation("999 Tunis 999").marque("Test").modele("Test")
                .categorie(CategorieVehicule.BERLINE).tarifJournalier(new BigDecimal("0"))
                .agence(agence).build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("positif");
    }
}