package com.example.pharmagest.fournisseurs;

public class Fournisseur {
    private int id;
    private String nom;
    private String adresse;
    private String contact;

    public Fournisseur(int id, String nom, String adresse, String contact) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.contact = contact;
    }

    public Fournisseur() {

    }


    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
    public String getContact() { return contact; }

    // Setters (nécessaires pour TableView)
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setContact(String contact) { this.contact = contact; }
}