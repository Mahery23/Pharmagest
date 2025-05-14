package com.example.pharmagest.prescription.prescriptionModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.prescription.prescription;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class prescriptionModele {

    // Récupérer les médicaments (id et nom)
    public ObservableList<Medicament> getMedicamentsSimple() {
        ObservableList<Medicament> list = FXCollections.observableArrayList();
        String query = "SELECT id, nom FROM medicament";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Medicament med = new Medicament();
                med.setId(rs.getInt("id"));
                med.setNom(rs.getString("nom"));
                list.add(med);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupérer toutes les prescriptions
    public ObservableList<prescription> getAllPrescriptions() {
        ObservableList<prescription> list = FXCollections.observableArrayList();
        String query = "SELECT id, numero_prescription, nom_medecin, date_prescription, nom_patient, medicament FROM prescription ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                prescription p = new prescription(
                        rs.getInt("id"),
                        rs.getString("numero_prescription"),
                        rs.getString("nom_medecin"),
                        rs.getDate("date_prescription").toLocalDate(),
                        rs.getString("nom_patient"),
                        rs.getString("medicament")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Vérifier si le numéro de prescription existe déjà
    public boolean prescriptionNumberExists(String prescriptionNumber) {
        String query = "SELECT COUNT(*) FROM prescription WHERE numero_prescription = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, prescriptionNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ajouter une prescription
    public boolean addPrescription(prescription p) {
        String query = "INSERT INTO prescription (numero_prescription, nom_medecin, date_prescription, nom_patient, medicament) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getNumero_prescription());
            pstmt.setString(2, p.getNom_medecin());
            pstmt.setDate(3, Date.valueOf(p.getDate_prescription()));
            pstmt.setString(4, p.getNom_patient());
            pstmt.setString(5, p.getMedicament());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mettre à jour une prescription existante
    public boolean updatePrescription(prescription p) {
        String query = "UPDATE prescription SET nom_medecin = ?, date_prescription = ?, nom_patient = ?, medicament = ? WHERE numero_prescription = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, p.getNom_medecin());
            pstmt.setDate(2, Date.valueOf(p.getDate_prescription()));
            pstmt.setString(3, p.getNom_patient());
            pstmt.setString(4, p.getMedicament());
            pstmt.setString(5, p.getNumero_prescription());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
