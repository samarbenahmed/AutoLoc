package tn.esprit.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entity.Reservation;
import tn.esprit.spring.entity.enums.StatutReservation;
import tn.esprit.spring.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Reservation>> findByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.findByClient_IdClient(clientId));
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<List<Reservation>> findByVehicule(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(reservationService.findByVehicule_IdVehicule(vehiculeId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Reservation>> findByStatut(@PathVariable StatutReservation statut) {
        return ResponseEntity.ok(reservationService.findByStatut(statut));
    }

    @PostMapping
    public ResponseEntity<Reservation> save(@RequestBody Reservation reservation) {
        return new ResponseEntity<>(reservationService.save(reservation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable Long id,
                                              @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.update(id, reservation));
    }

    @PutMapping("/{id}/statut/{statut}")
    public ResponseEntity<Reservation> changerStatut(@PathVariable Long id,
                                                     @PathVariable StatutReservation statut) {
        return ResponseEntity.ok(reservationService.changerStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}