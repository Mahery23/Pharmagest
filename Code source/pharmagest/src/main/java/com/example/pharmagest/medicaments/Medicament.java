package com.example.pharmagest.medicaments;

public class Medicament {
    private int id;
    private String nom;
    private double prixAchat;
    private double prixVente;
    private int stock;
    private int seuilAlerte;
    private int quantiteMax;
    private boolean necessitePrescription;
    private int fournisseurId; // ID du fournisseur
    private String fournisseur; // Nom du fournisseur
    private String famille;
    private String dosage;
    private String formeMedicament;
    private int quantiteVendue; // Nouvelle propriété pour stocker la quantité vendue


    // Constructeur complet (13 paramètres)
    public Medicament(int id, String nom, double prixAchat, double prixVente, int stock, int seuilAlerte,
                      int quantiteMax, boolean necessitePrescription, int fournisseurId, String fournisseur,
                      String famille, String dosage, String formeMedicament) {
        this.id = id;
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.seuilAlerte = seuilAlerte;
        this.quantiteMax = quantiteMax;
        this.necessitePrescription = necessitePrescription;
        this.fournisseurId = fournisseurId;
        this.fournisseur = fournisseur;
        this.famille = famille;
        this.dosage = dosage;
        this.formeMedicament = formeMedicament;
    }

    // Constructeur par défaut
    public Medicament() {
    }

    // Nouveau constructeur pour l'affichage sur une autre page (si besoin d'une version sans fournisseur)
    public Medicament(int id, String nom, double prixAchat, double prixVente, int stock, int seuilAlerte,
                      int quantiteMax, boolean necessitePrescription, int fournisseurId, String famille,
                      String dosage, String formeMedicament) {
        this.id = id;
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.seuilAlerte = seuilAlerte;
        this.quantiteMax = quantiteMax;
        this.necessitePrescription = necessitePrescription;
        this.fournisseurId = fournisseurId;
        // Ici, le nom du fournisseur n'est pas transmis, donc on peut le laisser vide
        this.fournisseur = "";
        this.famille = famille;
        this.dosage = dosage;
        this.formeMedicament = formeMedicament;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public double getPrixAchat() {
        return prixAchat;
    }
    public double getPrixVente() {
        return prixVente;
    }
    public int getStock() {
        return stock;
    }
    public int getSeuilAlerte() {
        return seuilAlerte;
    }
    public int getQuantiteMax() {
        return quantiteMax;
    }
    public boolean isNecessitePrescription() {
        return necessitePrescription;
    }
    public int getFournisseurId() {
        return fournisseurId;
    }
    public String getFournisseur() {
        return fournisseur;
    }
    public String getFamille() {
        return famille;
    }
    public String getDosage() {
        return dosage;
    }
    public String getFormeMedicament() {
        return formeMedicament;
    }
    public int getQuantiteVendue() {
        return quantiteVendue;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }
    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    public void setQuantiteMax(int quantiteMax) {
        this.quantiteMax = quantiteMax;
    }
    public void setNecessitePrescription(boolean necessitePrescription) {
        this.necessitePrescription = necessitePrescription;
    }
    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }
    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }
    public void setFamille(String famille) {
        this.famille = famille;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public void setFormeMedicament(String formeMedicament) {
        this.formeMedicament = formeMedicament;
    }
    public void setQuantiteVendue(int quantiteVendue) {
        this.quantiteVendue = quantiteVendue;
    }

    // Méthode pour vérifier si le stock est inférieur au seuil d'alerte
    public boolean verifierSeuilStock() {
        return this.stock < this.seuilAlerte;
    }
}

