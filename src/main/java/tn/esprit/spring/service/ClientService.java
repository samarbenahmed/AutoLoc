package tn.esprit.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entity.Client;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final IClientService clientService;

    public Client save(Client client) {
        return clientService.ajouterClient(client);
    }

    public Client findById(Long id) {
        return clientService.getClient(id);
    }
}
