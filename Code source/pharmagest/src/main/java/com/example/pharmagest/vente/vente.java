package com.example.pharmagest.vente;

import java.sql.Date;

public class vente {
    private int id;
    private int noVente;
    private Date dateVente;
    private String typeVente;
    private double prixTotal;
    private String statutVente;
    private int utilisateurId;

    // Getters et Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNoVente() {
        return noVente;
    }
    public void setNoVente(int noVente) {
        this.noVente = noVente;
    }
    public Date getDateVente() {
        return dateVente;
    }
    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }
    public String getTypeVente() {
        return typeVente;
    }
    public void setTypeVente(String typeVente) {
        this.typeVente = typeVente;
    }
    public double getPrixTotal() {
        return prixTotal;
    }
    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }
    public String getStatutVente() {
        return statutVente;
    }
    public void setStatutVente(String statutVente) {
        this.statutVente = statutVente;
    }
    public int getUtilisateurId() {
        return utilisateurId;
    }
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}
