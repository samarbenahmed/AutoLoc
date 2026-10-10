# AutoLoc 

## Description 
Plateforme de gestion de location de véhicules multi-agences. 

## Acteurs 
- **Client** : consulte le catalogue, réserve et loue des véhicules 
- **Agent d'agence** : gère les réservations et les retours de véhicules au quotidien 
- **Responsable d'agence** : supervise l'agence, les agents et les stocks de véhicules 
- **Administrateur** : gère la plateforme globale, les agences et les comptes utilisateurs

  ## Relations JPA
  Client -> Reservation (OneToMany, cascade, orphanRemoval, fetch LAZY)