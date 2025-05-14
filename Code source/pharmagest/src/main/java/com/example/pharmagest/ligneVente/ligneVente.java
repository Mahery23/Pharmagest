package com.example.pharmagest.ligneVente;

public class ligneVente {
    private int id;
    private int venteId;
    private int medicamentId;
    private int quantite;
    private double prixUnitaire;
    private int prescriptionId;
    private String typeVente; // Exemple : "Libre" ou "Prescrite"

    public ligneVente() {
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getVenteId() {
        return venteId;
    }
    public void setVenteId(int venteId) {
        this.venteId = venteId;
    }
    public int getMedicamentId() {
        return medicamentId;
    }
    public void setMedicamentId(int medicamentId) {
        this.medicamentId = medicamentId;
    }
    public int getQuantite() {
        return quantite;
    }
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    public double getPrixUnitaire() {
        return prixUnitaire;
    }
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    public int getPrescriptionId() {
        return prescriptionId;
    }
    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    public String getTypeVente() {
        return typeVente;
    }
    public void setTypeVente(String typeVente) {
        this.typeVente = typeVente;
    }
}
