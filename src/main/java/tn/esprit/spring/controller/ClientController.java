package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Client;
import tn.esprit.spring.service.IClientService;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @PostMapping("/ajouterClient")
    public Client ajouterClient(@RequestBody Client client) {
        return clientService.ajouterClient(client);
    }

    @PutMapping("/modifierClient")
    public Client modifierClient(@RequestBody Client client) {
        return clientService.modifierClient(client);
    }

    @GetMapping("/recupererTousClients")
    public List<Client> getAllClient() {
        return clientService.getAllClient();
    }

    @GetMapping("/recupererClient/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @DeleteMapping("/deleteClientById/{id}")
    public void supprimerClientById(@PathVariable Long id) {
        clientService.supprimerClientById(id);
    }
}
