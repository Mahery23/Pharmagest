package com.example.pharmagest.receptionCommande;

import java.sql.Date;

public class receptionCommande {
    private int id;
    private Date dateLivraison;
    private String status;
    private int commandeId;

    // Constructeur complet
    public receptionCommande(int id, Date dateLivraison, String status, int commandeId) {
        this.id = id;
        this.dateLivraison = dateLivraison;
        this.status = status;
        this.commandeId = commandeId;
    }

    // Constructeur vide
    public receptionCommande() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getDateLivraison() {
        return dateLivraison;
    }
    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getCommandeId() {
        return commandeId;
    }
    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }
}
