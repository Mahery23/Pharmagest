package com.example.pharmagest.prescription;

import java.time.LocalDate;

public class prescription {
    private int id;
    private String numero_prescription;
    private String nom_medecin;
    private LocalDate date_prescription;
    private String nom_patient;
    private String medicament;

    public prescription() { }

    // Pour l'ajout (sans id)
    public prescription(String numero_prescription, String nom_medecin, LocalDate date_prescription, String nom_patient, String medicament) {
        this.numero_prescription = numero_prescription;
        this.nom_medecin = nom_medecin;
        this.date_prescription = date_prescription;
        this.nom_patient = nom_patient;
        this.medicament = medicament;
    }

    // Pour la récupération (avec id)
    public prescription(int id, String numero_prescription, String nom_medecin, LocalDate date_prescription, String nom_patient, String medicament) {
        this.id = id;
        this.numero_prescription = numero_prescription;
        this.nom_medecin = nom_medecin;
        this.date_prescription = date_prescription;
        this.nom_patient = nom_patient;
        this.medicament = medicament;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero_prescription() { return numero_prescription; }
    public void setNumero_prescription(String numero_prescription) { this.numero_prescription = numero_prescription; }

    public String getNom_medecin() { return nom_medecin; }
    public void setNom_medecin(String nom_medecin) { this.nom_medecin = nom_medecin; }

    public LocalDate getDate_prescription() { return date_prescription; }
    public void setDate_prescription(LocalDate date_prescription) { this.date_prescription = date_prescription; }

    public String getNom_patient() { return nom_patient; }
    public void setNom_patient(String nom_patient) { this.nom_patient = nom_patient; }

    public String getMedicament() { return medicament; }
    public void setMedicament(String medicament) { this.medicament = medicament; }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", numero_prescription='" + numero_prescription + '\'' +
                ", nom_medecin='" + nom_medecin + '\'' +
                ", date_prescription=" + date_prescription +
                ", nom_patient='" + nom_patient + '\'' +
                ", medicament='" + medicament + '\'' +
                '}';
    }
}
