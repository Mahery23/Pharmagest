package com.example.pharmagest.commande;

import java.sql.Date;

public class commande {
    private int id;
    private int noCommande;
    private Date dateCommande;
    private double prixTotal;
    private int fournisseurId;
    private int utilisateurId;
    private String statut;
    private int medicamentId;
    private int quantites;
    private double prixUnitaire;
    private String medicamentNom; // Nouvelle propriété pour le nom du médicament

    public commande() {
    }

    public commande(int id, String fournisseur) {
        this.id = id;
        this.fournisseurId = Integer.parseInt(fournisseur); // Assumer que le fournisseur est un ID sous forme de String
    }

    public commande(int id, int commandeId, int medicamentId, int quantites, double prixUnitaire) {
        this.id = id;
        this.noCommande = commandeId;
        this.medicamentId = medicamentId;
        this.quantites = quantites;
        this.prixUnitaire = prixUnitaire;
        this.prixTotal = quantites * prixUnitaire;
    }

    public commande(int id, int noCommande, Date dateCommande, double prixTotal, int fournisseurId, int utilisateurId, String statut) {
        this.id = id;
        this.noCommande = noCommande;
        this.dateCommande = dateCommande;
        this.prixTotal = prixTotal;
        this.fournisseurId = fournisseurId;
        this.utilisateurId = utilisateurId;
        this.statut = statut;
    }

    public commande(int id, int commandeId, int quantites, double prixAchat) {
    }

    // Getters et Setters existants
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNoCommande() {
        return noCommande;
    }
    public void setNoCommande(int noCommande) {
        this.noCommande = noCommande;
    }

    public Date getDateCommande() {
        return dateCommande;
    }
    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getPrixTotal() {
        return prixTotal;
    }
    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }
    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getMedicamentId() {
        return medicamentId;
    }
    public void setMedicamentId(int medicamentId) {
        this.medicamentId = medicamentId;
    }

    public int getQuantites() {
        return quantites;
    }
    public void setQuantites(int quantites) {
        this.quantites = quantites;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    // Getter et Setter pour medicamentNom
    public String getMedicamentNom() {
        return medicamentNom;
    }
    public void setMedicamentNom(String medicamentNom) {
        this.medicamentNom = medicamentNom;
    }
}
