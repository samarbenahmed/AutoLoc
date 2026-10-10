# AutoLoc

## Description
Plateforme de gestion de location de véhicules multi-agences.

## Acteurs
- **Client** : consulte le catalogue, réserve et loue des véhicules
- **Agent d'agence** : gère les réservations et les retours de véhicules au quotidien
- **Responsable d'agence** : supervise l'agence, les agents et les stocks de véhicules
- **Administrateur** : gère la plateforme globale, les agences et les comptes utilisateurs

## Relations JPA
- Client -> Reservation (OneToMany, cascade, orphanRemoval, fetch LAZY)

## Interfaces et implémentations des services
Chaque entité possède une interface de service et son implémentation, dans le package `tn.esprit.spring.service`.
L'implémentation est annotée `@Service` et implémente l'interface correspondante.

| Entité | Interface | Implémentation |
|---|---|---|
| Agence | `IAgenceService` | `IAgenceServiceImp` |
| Client | `IClientService` | `IClientServiceImp` |
| Contrat | `IContratService` | `IContratServiceImp` |
| Employe | `IEmployeService` | `IEmployeServiceImp` |
| Equipement | `IEquipementService` | `IEquipementServiceImp` |
| Maintenance | `IMaintenanceService` | `IMaintenanceServiceImp` |
| Paiement | `IPaiementService` | `IPaiementServiceImp` |
| Reservation | `IReservationService` | `IReservationServiceImp` |
| Vehicule | `IVehiculeService` | `IVehiculeServiceImp` |