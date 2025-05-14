package com.example.pharmagest.login; // Déclare le package de cette classe.

public class login { // Définition de la classe modèle "login".
    private String username; // Attribut pour le nom d'utilisateur.
    private String password; // Attribut pour le mot de passe.
    private String statut;
    private int id;

    // Getter pour obtenir la valeur du nom d'utilisateur.
    public String getUsername() {
        return username;
    }

    // Setter pour définir ou modifier le nom d'utilisateur.
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter pour obtenir la valeur du mot de passe.
    public String getPassword() {
        return password;
    }

    // Setter pour définir ou modifier le mot de passe.
    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
