package com.example.pharmagest.fournisseurs.fournisseursModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.fournisseurs.Fournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class FournisseurModele {
    public ObservableList<Fournisseur> getFournisseurs() {
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM fournisseur";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Fournisseur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean ajouterFournisseur(String nom, String adresse, String contact) {
        String query = "INSERT INTO fournisseur (nom, adresse, contact) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, adresse);
            pstmt.setString(3, contact);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerFournisseur(String id) {
        String query = "DELETE FROM fournisseur WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(id));
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierFournisseur(String id, String champ, String newValue) {
        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }

        if (!champ.equals("nom") && !champ.equals("adresse") && !champ.equals("contact")) {
            return false;
        }
        String query = "UPDATE fournisseur SET " + champ + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newValue);
            pstmt.setInt(2, userId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}