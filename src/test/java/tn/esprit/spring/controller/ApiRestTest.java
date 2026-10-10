package tn.esprit.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.spring.autolocsamarbenahmed.AutoLocSamarBenahmedApplication;
import tn.esprit.spring.entity.Agence;
import tn.esprit.spring.entity.Equipement;
import tn.esprit.spring.service.AgenceService;
import tn.esprit.spring.service.ClientService;
import tn.esprit.spring.service.EquipementService;
import tn.esprit.spring.service.VehiculeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifie le CRUD REST, les codes HTTP et surtout l'absence de boucle JSON.
 */
@SpringBootTest(classes = AutoLocSamarBenahmedApplication.class)
@AutoConfigureMockMvc
class ApiRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AgenceService agenceService;
    @Autowired private VehiculeService vehiculeService;
    @Autowired private ClientService clientService;
    @Autowired private EquipementService equipementService;

    @Autowired private tn.esprit.spring.repository.AgenceRepository agenceRepository;
    @Autowired private tn.esprit.spring.repository.EmployeRepository employeRepository;
    @Autowired private tn.esprit.spring.repository.VehiculeRepository vehiculeRepository;
    @Autowired private tn.esprit.spring.repository.MaintenanceRepository maintenanceRepository;
    @Autowired private tn.esprit.spring.repository.EquipementRepository equipementRepository;
    @Autowired private tn.esprit.spring.repository.ClientRepository clientRepository;
    @Autowired private tn.esprit.spring.repository.ReservationRepository reservationRepository;
    @Autowired private tn.esprit.spring.repository.ContratRepository contratRepository;
    @Autowired private tn.esprit.spring.repository.PaiementRepository paiementRepository;

    private Long agenceId;

    @BeforeEach
    void initialiser() {
        nettoyer();
        Agence agence = agenceService.save(Agence.builder()
                .nom("Agence REST").ville("Bizerte").adresse("5 rue rest")
                .telephone("20777777").build());
        agenceId = agence.getIdAgence();
    }

    private void nettoyer() {
        paiementRepository.deleteAllInBatch();
        contratRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        maintenanceRepository.deleteAllInBatch();
        clientRepository.deleteAllInBatch();
        vehiculeRepository.deleteAllInBatch();
        equipementRepository.deleteAllInBatch();
        employeRepository.deleteAllInBatch();
        agenceRepository.deleteAllInBatch();
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    @Test
    void crudAgenceRespecteLesCodesHttp() throws Exception {
        mockMvc.perform(get("/api/agences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/agences/" + agenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Agence REST"))
                .andExpect(jsonPath("$.ville").value("Bizerte"));

        mockMvc.perform(post("/api/agences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Agence Tunis\",\"ville\":\"Tunis\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/agences/" + agenceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Agence Renommee\",\"ville\":\"Bizerte\",\"telephone\":\"20000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Agence Renommee"));

        mockMvc.perform(delete("/api/agences/" + agenceId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/agences/" + agenceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void employeEtVehiculeSontSerialisesAvecLeurAgenceSansBoucle() throws Exception {
        var employe = mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Benahmed\",\"prenom\":\"Samar\",\"role\":\"AGENT\","
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("AGENT"))
                .andExpect(jsonPath("$.agence.idAgence").value(agenceId))
                .andExpect(jsonPath("$.agence.employes").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        assertThat(employe).doesNotContain("\"employes\"");

        var vehicule = mockMvc.perform(post("/api/vehicules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"700 Tunis 7\",\"marque\":\"Peugeot\","
                                + "\"modele\":\"208\",\"categorie\":\"SUV\",\"tarifJournalier\":55.50,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categorie").value("SUV"))
                .andExpect(jsonPath("$.statut").value("DISPONIBLE"))
                .andExpect(jsonPath("$.agence.ville").value("Bizerte"))
                .andReturn().getResponse().getContentAsString();

        // Aucune collection inverse ne doit apparaitre : pas de Vehicule -> Reservation -> Vehicule
        assertThat(vehicule).doesNotContain("reservations");
        assertThat(vehicule).doesNotContain("maintenances");
        assertThat(vehicule).doesNotContain("equipements");

        mockMvc.perform(get("/api/vehicules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void lesCollectionsEnversNeSontJamaisSerialisees() throws Exception {
        Equipement equipement = equipementService.save(
                Equipement.builder().libelle("Radar").build());

        var reponse = mockMvc.perform(get("/api/equipements/" + equipement.getIdEquipement()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libelle").value("Radar"))
                .andReturn().getResponse().getContentAsString();
        assertThat(reponse).doesNotContain("vehicules");

        var reponseClient = mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(reponseClient).doesNotContain("reservations");
    }

    @Test
    void leScenarioCompletReservationContratPaiementFonctionne() throws Exception {
        var clientJson = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Bouzid\",\"prenom\":\"Malek\",\"email\":\"malek.bouzid@email.tn\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long clientId = objectMapper.readTree(clientJson).get("idClient").asLong();

        var vehiculeJson = mockMvc.perform(post("/api/vehicules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"800 Tunis 8\",\"marque\":\"Dacia\","
                                + "\"modele\":\"Sandero\",\"categorie\":\"BERLINE\",\"tarifJournalier\":50,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long vehiculeId = objectMapper.readTree(vehiculeJson).get("idVehicule").asLong();

        var reservationJson = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\","
                                + "\"client\":{\"idClient\":" + clientId + "},"
                                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andReturn().getResponse().getContentAsString();
        Long reservationId = objectMapper.readTree(reservationJson).get("idReservation").asLong();

        mockMvc.perform(put("/api/reservations/" + reservationId + "/statut/CONFIRMEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"));

        var contratJson = mockMvc.perform(post("/api/contrats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservation\":{\"idReservation\":" + reservationId + "},\"valide\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montantTotal").value(150.0))
                .andReturn().getResponse().getContentAsString();
        Long contratId = objectMapper.readTree(contratJson).get("idContrat").asLong();

        var contratComplet = mockMvc.perform(get("/api/contrats/" + contratId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservation.idReservation").value(reservationId))
                .andReturn().getResponse().getContentAsString();
        // Contrat -> paiements -> Paiement -> contrat ne doit pas reboucler
        assertThat(contratComplet).doesNotContain("\"contrat\":");

        mockMvc.perform(post("/api/paiements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"montant\":150,\"datePaiement\":\"2026-11-01\","
                                + "\"modePaiement\":\"CARTE\",\"contrat\":{\"idContrat\":" + contratId + "}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modePaiement").value("CARTE"));

        mockMvc.perform(get("/api/paiements/contrat/" + contratId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/api/contrats/" + contratId)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/reservations/" + reservationId)).andExpect(status().isNoContent());
    }

    @Test
    void unConflitDePeriodeRetourne409() throws Exception {
        var clientJson = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Kon\",\"prenom\":\"Sami\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long clientId = objectMapper.readTree(clientJson).get("idClient").asLong();

        var vehiculeJson = mockMvc.perform(post("/api/vehicules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"900 Tunis 9\",\"marque\":\"Fiat\","
                                + "\"modele\":\"500\",\"categorie\":\"CITADINE\",\"tarifJournalier\":30,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andReturn().getResponse().getContentAsString();
        Long vehiculeId = objectMapper.readTree(vehiculeJson).get("idVehicule").asLong();

        String payload = "{\"dateDebut\":\"2026-12-01\",\"dateFin\":\"2026-12-10\","
                + "\"client\":{\"idClient\":" + clientId + "},"
                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}";

        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated());

        String chevauche = "{\"dateDebut\":\"2026-12-05\",\"dateFin\":\"2026-12-08\","
                + "\"client\":{\"idClient\":" + clientId + "},"
                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}";
        mockMvc.perform(post("/api/reservations").contentType(MediaType.APPLICATION_JSON).content(chevauche))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void uneDonneeInvalideRetourne400() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prenom\":\"PasDeNom\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"A\",\"prenom\":\"B\",\"email\":\"casse\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unStatutInconnuRetourne400() throws Exception {
        mockMvc.perform(get("/api/vehicules/statut/INCONNU"))
                .andExpect(status().isBadRequest());
    }
}