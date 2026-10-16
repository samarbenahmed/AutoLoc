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
import tn.esprit.spring.service.EquipementService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifie le CRUD REST, les codes HTTP et surtout l'absence de boucle JSON.
 * Tous les endpoints suivent le modele du module Vehicule : /agences/ajouterAgence, etc.
 */
@SpringBootTest(classes = AutoLocSamarBenahmedApplication.class)
@AutoConfigureMockMvc
class ApiRestTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AgenceService agenceService;
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

    @Test
    void crudAgenceRespecteLesCodesHttp() throws Exception {
        mockMvc.perform(get("/agences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/agences/recupererAgence/" + agenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Agence REST"))
                .andExpect(jsonPath("$.ville").value("Bizerte"));

        mockMvc.perform(post("/agences/ajouterAgence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Agence Tunis\",\"ville\":\"Tunis\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Agence Tunis"));

        mockMvc.perform(put("/agences/modifierAgence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idAgence\":" + agenceId + ",\"nom\":\"Agence Renommee\","
                                + "\"ville\":\"Bizerte\",\"telephone\":\"20000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Agence Renommee"));

        mockMvc.perform(delete("/agences/deleteAgenceById/" + agenceId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/agences/recupererAgence/" + agenceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void employeEtVehiculeSontSerialisesAvecLeurAgenceSansBoucle() throws Exception {
        String employe = mockMvc.perform(post("/employes/ajouterEmploye")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Benahmed\",\"prenom\":\"Samar\",\"role\":\"AGENT\","
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("AGENT"))
                .andExpect(jsonPath("$.agence.employes").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        assertThat(employe).doesNotContain("\"employes\"");
        assertThat(objectMapper.readTree(employe).get("agence").get("idAgence").asLong()).isEqualTo(agenceId);

        String vehicule = mockMvc.perform(post("/vehicules/ajouterVehicule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"700 Tunis 7\",\"marque\":\"Peugeot\","
                                + "\"modele\":\"208\",\"categorie\":\"SUV\",\"tarifJournalier\":55.50,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorie").value("SUV"))
                .andExpect(jsonPath("$.statut").value("DISPONIBLE"))
                .andExpect(jsonPath("$.agence.ville").value("Bizerte"))
                .andReturn().getResponse().getContentAsString();

        assertThat(vehicule).doesNotContain("reservations");
        assertThat(vehicule).doesNotContain("maintenances");
        assertThat(vehicule).doesNotContain("equipements");

        mockMvc.perform(get("/vehicules/recupererTousVehicules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void lesCollectionsEnversNeSontJamaisSerialisees() throws Exception {
        Equipement equipement = equipementService.save(
                Equipement.builder().libelle("Radar").build());

        String reponse = mockMvc.perform(get("/equipements/recupererEquipement/" + equipement.getIdEquipement()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libelle").value("Radar"))
                .andReturn().getResponse().getContentAsString();
        assertThat(reponse).doesNotContain("vehicules");

        String reponseClient = mockMvc.perform(get("/clients/recupererTousClients"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(reponseClient).doesNotContain("reservations");
    }

    @Test
    void leScenarioCompletReservationContratPaiementFonctionne() throws Exception {
        String clientJson = mockMvc.perform(post("/clients/ajouterClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Bouzid\",\"prenom\":\"Malek\",\"email\":\"malek.bouzid@email.tn\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long clientId = objectMapper.readTree(clientJson).get("idClient").asLong();

        String vehiculeJson = mockMvc.perform(post("/vehicules/ajouterVehicule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"800 Tunis 8\",\"marque\":\"Dacia\","
                                + "\"modele\":\"Sandero\",\"categorie\":\"BERLINE\",\"tarifJournalier\":50,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long vehiculeId = objectMapper.readTree(vehiculeJson).get("idVehicule").asLong();

        String reservationJson = mockMvc.perform(post("/reservations/ajouterReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateDebut\":\"2026-11-01\",\"dateFin\":\"2026-11-03\","
                                + "\"client\":{\"idClient\":" + clientId + "},"
                                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andReturn().getResponse().getContentAsString();
        long reservationId = objectMapper.readTree(reservationJson).get("idReservation").asLong();

        mockMvc.perform(put("/reservations/modifierReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idReservation\":" + reservationId + ",\"dateDebut\":\"2026-11-01\","
                                + "\"dateFin\":\"2026-11-03\",\"statut\":\"CONFIRMEE\","
                                + "\"client\":{\"idClient\":" + clientId + "},"
                                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"));

        String contratJson = mockMvc.perform(post("/contrats/ajouterContrat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservation\":{\"idReservation\":" + reservationId + "},\"valide\":true}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(contratJson).contains("\"montantTotal\":150.00");
        long contratId = objectMapper.readTree(contratJson).get("idContrat").asLong();

        String contratComplet = mockMvc.perform(get("/contrats/recupererContrat/" + contratId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(contratComplet).get("reservation").get("idReservation").asLong())
                .isEqualTo(reservationId);
        assertThat(contratComplet).doesNotContain("\"contrat\":");
        assertThat(contratComplet).doesNotContain("paiements");

        mockMvc.perform(post("/paiements/ajouterPaiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"montant\":150,\"datePaiement\":\"2026-11-01\","
                                + "\"modePaiement\":\"CARTE\",\"contrat\":{\"idContrat\":" + contratId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modePaiement").value("CARTE"));

        mockMvc.perform(get("/paiements/recupererTousPaiements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(delete("/contrats/deleteContratById/" + contratId)).andExpect(status().isOk());
        mockMvc.perform(delete("/reservations/deleteReservationById/" + reservationId)).andExpect(status().isOk());
    }

    @Test
    void unConflitDePeriodeRetourne409() throws Exception {
        String clientJson = mockMvc.perform(post("/clients/ajouterClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Kon\",\"prenom\":\"Sami\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long clientId = objectMapper.readTree(clientJson).get("idClient").asLong();

        String vehiculeJson = mockMvc.perform(post("/vehicules/ajouterVehicule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"immatriculation\":\"900 Tunis 9\",\"marque\":\"Fiat\","
                                + "\"modele\":\"500\",\"categorie\":\"CITADINE\",\"tarifJournalier\":30,"
                                + "\"agence\":{\"idAgence\":" + agenceId + "}}"))
                .andReturn().getResponse().getContentAsString();
        long vehiculeId = objectMapper.readTree(vehiculeJson).get("idVehicule").asLong();

        String payload = "{\"dateDebut\":\"2026-12-01\",\"dateFin\":\"2026-12-10\","
                + "\"client\":{\"idClient\":" + clientId + "},"
                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}";

        mockMvc.perform(post("/reservations/ajouterReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        String chevauche = "{\"dateDebut\":\"2026-12-05\",\"dateFin\":\"2026-12-08\","
                + "\"client\":{\"idClient\":" + clientId + "},"
                + "\"vehicule\":{\"idVehicule\":" + vehiculeId + "}}";
        mockMvc.perform(post("/reservations/ajouterReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(chevauche))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void uneDonneeInvalideRetourne400() throws Exception {
        mockMvc.perform(post("/clients/ajouterClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prenom\":\"PasDeNom\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(post("/clients/ajouterClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"A\",\"prenom\":\"B\",\"email\":\"casse\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unIdentifiantInvalideRetourne400() throws Exception {
        mockMvc.perform(get("/vehicules/recupererVehicule/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unIdentifiantInconnuRetourne404() throws Exception {
        mockMvc.perform(get("/clients/recupererClient/9999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
