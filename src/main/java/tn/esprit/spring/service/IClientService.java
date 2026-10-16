package tn.esprit.spring.service;

import tn.esprit.spring.entity.Client;

import java.util.List;

public interface IClientService {

    Client ajouterClient(Client client);

    Client modifierClient(Client client);

    Client getClient(Long id);

    List<Client> getAllClient();

    void supprimerClientById(Long id);

}
