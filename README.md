# Room Reservation System - Auth Service

## Objectifs
✅ Fournir une solution simple et intuitive pour réserver des salles de réunion.  
✅ Permettre aux administrateurs de gérer les salles et les réservations.  
✅ Assurer une authentification sécurisée des utilisateurs.  
✅ Mettre en place des notifications en temps réel.  

## Membres de l'équipe  
- **Roqia ZERKTOUNI**  
- **Rim Salmoun**  
- **Rekhla Zakaria**  

## Liste des fonctionnalités  

### 🛡️ 1. Gestion des utilisateurs (Auth-Service)  
- ✅ Inscription utilisateur (création de compte avec email/mot de passe)  
- ✅ Inscription administrateur (compte avec rôle spécifique)  
- ✅ Connexion et authentification (JWT)  
- ✅ Gestion des utilisateurs (ajout, modification, suppression par l'administrateur)  
- ✅ Gestion des rôles (utilisateur/admin)  

### 🏠 2. Gestion des salles (Room-Service)  
- ✅ Création de salle (nom, capacité, équipements, etc.)  
- ✅ Affichage des salles disponibles (avec filtres : capacité, équipements, etc.)  
- ✅ Modification des informations d’une salle  
- ✅ Suppression d’une salle (avec gestion des réservations associées)  

### 📅 3. Gestion des réservations (Booking-Service)  
- ✅ Réservation d’une salle (sélection de la date, heure et salle disponible)  
- ✅ Affichage des réservations passées et à venir (historique utilisateur)  
- ✅ Annulation d’une réservation (selon les conditions définies)  
- ✅ Gestion des réservations par l’administrateur (affichage global, gestion des conflits)  

### 📢 4. Système de notifications  
- ✅ Notification de confirmation de réservation (email et/ou push)  
- ✅ Notification en cas de modification ou annulation d’une réservation  
- ✅ Affichage des notifications dans l’application  
