package com.example.pharmagest.medicaments.medicamentsModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.medicaments.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.Arrays;

public class MedicamentsModele {

    public ObservableList<Medicament> getMedicaments() {
        ObservableList<Medicament> list = FXCollections.observableArrayList();
        // Récupération du nom du fournisseur avec l'alias "fournisseur"
        String query = "SELECT m.*, f.nom AS fournisseur FROM medicament m " +
                "LEFT JOIN fournisseur f ON m.fournisseur_id = f.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Medicament(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_achat"),
                        rs.getDouble("prix_vente"),
                        rs.getInt("stock"),
                        rs.getInt("seuil_alerte"),
                        rs.getInt("quantite_max"),
                        rs.getBoolean("necessite_prescription"),
                        rs.getInt("fournisseur_id"),
                        rs.getString("fournisseur"),
                        rs.getString("famille"),
                        rs.getString("dosage"),
                        rs.getString("forme_medicament")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Modification de la signature pour recevoir le nom du fournisseur
    public boolean ajouterMedicament(String nom, double prixAchat, double prixVente, int stock, int seuilAlerte, int quantiteMax,
                                     boolean necessitePrescription, String fournisseurName, String famille, String dosage, String formeMedicament) {
        String query = "INSERT INTO medicament (nom, prix_achat, prix_vente, stock, seuil_alerte, quantite_max, necessite_prescription, fournisseur_id, famille, dosage, forme_medicament) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int fournisseurId = getFournisseurIdByName(fournisseurName);
            if (fournisseurId == -1) return false;

            pstmt.setString(1, nom);
            pstmt.setDouble(2, prixAchat);
            pstmt.setDouble(3, prixVente);
            pstmt.setInt(4, stock);
            pstmt.setInt(5, seuilAlerte);
            pstmt.setInt(6, quantiteMax);
            pstmt.setBoolean(7, necessitePrescription);
            pstmt.setInt(8, fournisseurId);
            pstmt.setString(9, famille);
            pstmt.setString(10, dosage);
            pstmt.setString(11, formeMedicament);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerMedicament(String id) {
        String query = "DELETE FROM medicament WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(id));
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            // On ne fait rien avec l'erreur, on retourne simplement false
            return false;
        }
    }

    public boolean modifierMedicament(String id, String champ, String newValue) {
        int medId = Integer.parseInt(id);
        String[] allowedFields = {"nom", "prix_achat", "prix_vente", "stock", "seuil_alerte", "quantite_max",
                "necessite_prescription", "fournisseur_id", "famille", "dosage", "forme_medicament"};
        if (!Arrays.asList(allowedFields).contains(champ)) return false;

        String query = "UPDATE medicament SET " + champ + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            switch (champ) {
                case "necessite_prescription":
                    pstmt.setBoolean(1, newValue.equalsIgnoreCase("oui"));
                    break;
                case "fournisseur_id":
                    int fournisseurId = getFournisseurIdByName(newValue);
                    if (fournisseurId == -1) return false;
                    pstmt.setInt(1, fournisseurId);
                    break;
                case "prix_achat":
                case "prix_vente":
                    pstmt.setDouble(1, Double.parseDouble(newValue));
                    break;
                case "stock":
                case "seuil_alerte":
                case "quantite_max":
                    pstmt.setInt(1, Integer.parseInt(newValue));
                    break;
                // Pour 'dosage', il s'agit d'une chaîne de caractères
                case "dosage":
                    pstmt.setString(1, newValue);
                    break;
                default:
                    pstmt.setString(1, newValue);
            }

            pstmt.setInt(2, medId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<String> getFournisseurs() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String query = "SELECT nom FROM fournisseur";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getFournisseurIdByName(String nom) throws SQLException {
        String query = "SELECT id FROM fournisseur WHERE nom = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }
}
