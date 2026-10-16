package tn.esprit.spring.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Client;
import tn.esprit.spring.exception.BadRequestException;
import tn.esprit.spring.exception.NotFoundException;
import tn.esprit.spring.repository.ClientRepository;

import java.util.List;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
public class IClientServiceImp implements IClientService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final ClientRepository clientRepository;

    @Override
    public Client ajouterClient(Client client) {
        valider(client);
        return clientRepository.save(client);
    }

    @Override
    public Client modifierClient(Client client) {
        if (client == null || client.getIdClient() == null) {
            throw new BadRequestException("L'identifiant du client est obligatoire pour une modification");
        }
        getClient(client.getIdClient());
        valider(client);
        return clientRepository.save(client);
    }

    @Override
    public Client getClient(Long id) {
        if (id == null) {
            throw new BadRequestException("L'identifiant du client est obligatoire");
        }
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable avec id " + id));
    }

    @Override
    public List<Client> getAllClient() {
        return clientRepository.findAll();
    }

    @Override
    public void supprimerClientById(Long id) {
        getClient(id);
        clientRepository.deleteById(id);
    }

    private void valider(Client client) {
        if (client == null) {
            throw new BadRequestException("Client obligatoire");
        }
        if (estVide(client.getNom())) {
            throw new BadRequestException("Le nom du client est obligatoire");
        }
        if (estVide(client.getPrenom())) {
            throw new BadRequestException("Le prenom du client est obligatoire");
        }
        if (!estVide(client.getEmail()) && !EMAIL_PATTERN.matcher(client.getEmail().trim()).matches()) {
            throw new BadRequestException("Adresse email invalide : " + client.getEmail());
        }
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
