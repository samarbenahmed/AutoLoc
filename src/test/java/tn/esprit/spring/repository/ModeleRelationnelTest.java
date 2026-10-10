package tn.esprit.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import tn.esprit.spring.autolocsamarbenahmed.AutoLocSamarBenahmedApplication;
import tn.esprit.spring.entity.*;
import tn.esprit.spring.entity.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifie que Hibernate cree correctement toutes les tables et que les relations
 * du diagramme UML sont bien materialisees.
 */
@DataJpaTest
@ContextConfiguration(classes = AutoLocSamarBenahmedApplication.class)
class ModeleRelationnelTest {

    @Autowired private AgenceRepository agenceRepository;
    @Autowired private EmployeRepository employeRepository;
    @Autowired private VehiculeRepository vehiculeRepository;
    @Autowired private MaintenanceRepository maintenanceRepository;
    @Autowired private EquipementRepository equipementRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ContratRepository contratRepository;
    @Autowired private PaiementRepository paiementRepository;
    @Autowired private jakarta.persistence.EntityManager entityManager;
    @Autowired private JdbcTemplate jdbc;

    /** Vide le contexte de persistance pour verifier les relations reellement stockees. */
    private void detacher() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void crudCompletSurLesNeufEntites() {
        // Agence
        Agence agence = agenceRepository.save(
                Agence.builder().nom("Agence Centre").ville("Tunis").adresse("12 rue de France")
                        .telephone("20123456").build());
        assertThat(agence.getIdAgence()).isNotNull();

        // Employe -> Agence (1..*)
        Employe employe = employeRepository.save(Employe.builder()
                .nom("Benahmed").prenom("Samar").role(RoleEmploye.MANAGER).agence(agence).build());
        assertThat(employe.getIdEmploye()).isNotNull();
        assertThat(employe.getRole()).isEqualTo(RoleEmploye.MANAGER);
        detacher();
        // Agence 1 -> * Employe
        assertThat(agenceRepository.findById(agence.getIdAgence()).orElseThrow().getEmployes())
                .extracting(Employe::getIdEmploye).contains(employe.getIdEmploye());

        // Vehicule -> Agence (1..*)
        Vehicule vehicule = vehiculeRepository.save(Vehicule.builder()
                .immatriculation("123 Tunis 4567").marque("Peugeot").modele("208")
                .categorie(CategorieVehicule.CITADINE).tarifJournalier(new BigDecimal("45.00"))
                .statut(StatutVehicule.DISPONIBLE).agence(agence).build());
        assertThat(vehicule.getIdVehicule()).isNotNull();
        detacher();
        // Agence 1 -> * Vehicule
        assertThat(agenceRepository.findById(agence.getIdAgence()).orElseThrow().getVehicules())
                .extracting(Vehicule::getIdVehicule).contains(vehicule.getIdVehicule());

        // Equipement + relation ManyToMany via vehicule_equipement
        Equipement gps = equipementRepository.save(Equipement.builder().libelle("GPS").build());
        Equipement clim = equipementRepository.save(Equipement.builder().libelle("Climatisation").build());
        vehicule.addEquipement(gps);
        vehicule.addEquipement(clim);
        vehiculeRepository.save(vehicule);
        vehiculeRepository.flush();

        Vehicule recharge = vehiculeRepository.findByImmatriculation("123 Tunis 4567").orElseThrow();
        assertThat(recharge.getEquipements()).extracting(Equipement::getLibelle)
                .containsExactlyInAnyOrder("GPS", "Climatisation");

        // Maintenance -> Vehicule (1..*)
        Maintenance maintenance = maintenanceRepository.save(Maintenance.builder()
                .dateDebut(LocalDate.of(2026, 1, 5)).dateFin(LocalDate.of(2026, 1, 10))
                .description("Revision complete").vehicule(vehicule).build());
        assertThat(maintenance.getIdMaintenance()).isNotNull();
        assertThat(maintenanceRepository.findByVehicule_IdVehicule(vehicule.getIdVehicule()))
                .extracting(Maintenance::getIdMaintenance).contains(maintenance.getIdMaintenance());

        // Client
        Client client = clientRepository.save(Client.builder()
                .nom("Trabelsi").prenom("Ahmed").email("ahmed.trabelsi@email.tn")
                .telephone("20987654").numPermis("TN123456")
                .dateInscription(LocalDate.of(2025, 12, 1)).build());
        assertThat(client.getIdClient()).isNotNull();

        // Reservation -> Client + Vehicule
        Reservation reservation = reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDate.of(2026, 2, 1)).dateFin(LocalDate.of(2026, 2, 5))
                .statut(StatutReservation.CONFIRMEE).client(client).vehicule(vehicule).build());
        assertThat(reservation.getIdReservation()).isNotNull();
        detacher();
        // Client 1 -> * Reservation et Vehicule 1 -> * Reservation
        assertThat(clientRepository.findById(client.getIdClient()).orElseThrow().getReservations())
                .extracting(Reservation::getIdReservation).contains(reservation.getIdReservation());
        assertThat(vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow().getReservations())
                .extracting(Reservation::getIdReservation).contains(reservation.getIdReservation());

        // Contrat -> Reservation (1..1)
        Contrat contrat = contratRepository.save(Contrat.builder()
                .dateSignature(LocalDate.of(2026, 1, 28)).montantTotal(new BigDecimal("225.00"))
                .valide(true)
                .reservation(reservationRepository.findById(reservation.getIdReservation()).orElseThrow())
                .build());
        assertThat(contrat.getIdContrat()).isNotNull();
        assertThat(contratRepository.findByReservation_IdReservation(reservation.getIdReservation()))
                .isPresent();

        // Paiement -> Contrat (1..*)
        Paiement paiement = paiementRepository.save(Paiement.builder()
                .montant(new BigDecimal("100.00")).datePaiement(LocalDate.of(2026, 1, 30))
                .modePaiement(ModePaiement.CARTE)
                .contrat(contratRepository.findById(contrat.getIdContrat()).orElseThrow())
                .build());
        assertThat(paiement.getIdPaiement()).isNotNull();
        assertThat(paiementRepository.findByContrat_IdContrat(contrat.getIdContrat()))
                .extracting(Paiement::getIdPaiement).contains(paiement.getIdPaiement());

        // Suppressions : on recharge les entites pour qu'elles soient geres par le
        // contexte de persistance (sinon les lignes de vehicule_equipement ne sont pas nettoyees)
        paiementRepository.delete(paiementRepository.findById(paiement.getIdPaiement()).orElseThrow());
        contratRepository.delete(contratRepository.findById(contrat.getIdContrat()).orElseThrow());
        reservationRepository.delete(reservationRepository.findById(reservation.getIdReservation()).orElseThrow());
        clientRepository.delete(clientRepository.findById(client.getIdClient()).orElseThrow());
        maintenanceRepository.delete(maintenanceRepository.findById(maintenance.getIdMaintenance()).orElseThrow());
        vehiculeRepository.delete(vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow());
        employeRepository.delete(employeRepository.findById(employe.getIdEmploye()).orElseThrow());
        agenceRepository.delete(agenceRepository.findById(agence.getIdAgence()).orElseThrow());

        assertThat(agenceRepository.findAll()).isEmpty();
        assertThat(vehiculeRepository.findAll()).isEmpty();
        assertThat(clientRepository.findAll()).isEmpty();
        assertThat(reservationRepository.findAll()).isEmpty();
        assertThat(contratRepository.findAll()).isEmpty();
        assertThat(paiementRepository.findAll()).isEmpty();
        assertThat(equipementRepository.findAll()).hasSize(2);
    }

    @Test
    void leSchemaContientLesTablesEtLesClesEtrangeresDuDiagrammeUml() {
        List<String> tables = jdbc.queryForList(
                "select table_name from information_schema.tables "
                        + "where table_schema = 'PUBLIC' and table_type = 'BASE TABLE'",
                String.class);

        assertThat(tables).contains("AGENCE", "EMPLOYE", "VEHICULE", "MAINTENANCE", "EQUIPEMENT",
                "CLIENT", "RESERVATION", "CONTRAT", "PAIEMENT", "VEHICULE_EQUIPEMENT");

        Map<String, String> clesEtrangeres = new LinkedHashMap<>();
        for (Map<String, Object> ligne : jdbc.queryForList(
                "select constraint_name, table_name from information_schema.table_constraints "
                        + "where constraint_schema = 'PUBLIC' and constraint_type = 'FOREIGN KEY' "
                        + "order by constraint_name")) {
            clesEtrangeres.put((String) ligne.get("CONSTRAINT_NAME"), (String) ligne.get("TABLE_NAME"));
        }

        assertThat(clesEtrangeres)
                .containsEntry("FK_EMPLOYE_AGENCE", "EMPLOYE")
                .containsEntry("FK_VEHICULE_AGENCE", "VEHICULE")
                .containsEntry("FK_MAINTENANCE_VEHICULE", "MAINTENANCE")
                .containsEntry("FK_RESERVATION_CLIENT", "RESERVATION")
                .containsEntry("FK_RESERVATION_VEHICULE", "RESERVATION")
                .containsEntry("FK_CONTRAT_RESERVATION", "CONTRAT")
                .containsEntry("FK_PAIEMENT_CONTRAT", "PAIEMENT")
                .containsEntry("FK_VEHICULE_EQUIPEMENT_VEHICULE", "VEHICULE_EQUIPEMENT")
                .containsEntry("FK_VEHICULE_EQUIPEMENT_EQUIPEMENT", "VEHICULE_EQUIPEMENT");
    }

    @Test
    void requetesDeriveesRetournentLesBonnesEntites() {
        Agence agence = agenceRepository.save(
                Agence.builder().nom("Agence Nord").ville("Sousse").build());
        Employe agent = employeRepository.save(Employe.builder()
                .nom("Khaled").prenom("Meriem").role(RoleEmploye.AGENT).agence(agence).build());
        Vehicule suv = vehiculeRepository.save(Vehicule.builder()
                .immatriculation("200 Sousse 99").marque("Dacia").modele("Duster")
                .categorie(CategorieVehicule.SUV).tarifJournalier(new BigDecimal("70.00"))
                .statut(StatutVehicule.MAINTENANCE).agence(agence).build());

        assertThat(agenceRepository.findByVille("Sousse")).hasSize(1);
        assertThat(employeRepository.findByRole(RoleEmploye.AGENT)).extracting(Employe::getIdEmploye)
                .containsExactly(agent.getIdEmploye());
        assertThat(vehiculeRepository.findByStatut(StatutVehicule.MAINTENANCE))
                .extracting(Vehicule::getIdVehicule).containsExactly(suv.getIdVehicule());
        assertThat(vehiculeRepository.findByCategorie(CategorieVehicule.SUV)).hasSize(1);
        assertThat(vehiculeRepository.findByAgence_IdAgence(agence.getIdAgence())).hasSize(1);
        assertThat(vehiculeRepository.existsByImmatriculation("200 Sousse 99")).isTrue();
    }

    @Test
    void detecteLesReservationsEnConflitSurUnePeriode() {
        Agence agence = agenceRepository.save(Agence.builder().nom("Agence Est").ville("Sfax").build());
        Vehicule vehicule = vehiculeRepository.save(Vehicule.builder()
                .immatriculation("500 Sfax 11").marque("Renault").modele("Clio")
                .categorie(CategorieVehicule.CITADINE).tarifJournalier(new BigDecimal("35.00"))
                .statut(StatutVehicule.DISPONIBLE).agence(agence).build());
        Client client = clientRepository.save(Client.builder()
                .nom("Jouini").prenom("Sami").email("sami.jouini@email.tn").build());

        Reservation bloquante = reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDate.of(2026, 3, 10)).dateFin(LocalDate.of(2026, 3, 15))
                .statut(StatutReservation.CONFIRMEE).client(client).vehicule(vehicule).build());

        // periode qui chevauche -> conflit
        assertThat(reservationRepository.findConflictantes(vehicule.getIdVehicule(),
                LocalDate.of(2026, 3, 12), LocalDate.of(2026, 3, 20))).hasSize(1);
        // periode libre -> aucun conflit
        assertThat(reservationRepository.findConflictantes(vehicule.getIdVehicule(),
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 20))).isEmpty();
        // en excluant la reservation elle-meme -> aucun conflit
        assertThat(reservationRepository.findConflictantesHorsReservation(vehicule.getIdVehicule(),
                bloquante.getIdReservation(), LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 15))).isEmpty();
    }
}