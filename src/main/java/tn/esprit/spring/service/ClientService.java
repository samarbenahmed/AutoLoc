package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.entity.Client;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.ConflictException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ClientRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client " + id + " introuvable"));
    }

    @Transactional(readOnly = true)
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client avec l'email " + email + " introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Client> searchByNom(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }

    public Client save(Client client) {
        valider(client);
        if (client.getIdClient() == null && client.getEmail() != null
                && clientRepository.existsByEmail(client.getEmail())) {
            throw new ConflictException("Un client avec cet email existe deja");
        }
        if (client.getDateInscription() == null) {
            client.setDateInscription(LocalDate.now());
        }
        return clientRepository.save(client);
    }

    public Client update(Long id, Client client) {
        findById(id);
        valider(client);
        client.setIdClient(id);
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);
        if (!client.getReservations().isEmpty()) {
            throw new ConflictException("Impossible de supprimer un client ayant des reservations");
        }
        clientRepository.delete(client);
    }

    private void valider(Client client) {
        if (client == null) {
            throw new BadRequestException("Le client ne peut pas etre nul");
        }
        if (isBlank(client.getNom()) || isBlank(client.getPrenom())) {
            throw new BadRequestException("Le nom et le prenom du client sont obligatoires");
        }
        if (client.getEmail() != null && !client.getEmail().matches(EMAIL_REGEX)) {
            throw new BadRequestException("L'email du client est invalide");
        }
    }

    private boolean isBlank(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }
}