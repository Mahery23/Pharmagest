package com.example.pharmagest.MajPrix.majPrixModele;

import com.example.pharmagest.MajPrix.majPrix;
import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class majPrixModele {

    // Récupérer tous les médicaments depuis la base de données
    public ObservableList<majPrix> getAllMedicaments() {
        ObservableList<majPrix> medicamentsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM medicament";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                majPrix medicament = new majPrix();
                medicament.setId(rs.getInt("id"));
                medicament.setNom(rs.getString("nom"));
                medicament.setPrixAchat(rs.getDouble("prix_achat"));
                medicament.setPrixVente(rs.getDouble("prix_vente"));
                medicament.setFamille(rs.getString("famille"));
                medicament.setDosage(rs.getString("dosage"));
                medicament.setFormeMedicament(rs.getString("forme_medicament"));

                medicamentsList.add(medicament);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicamentsList;
    }

    // Mettre à jour le prix (achat ou vente) d'un médicament
    public boolean updatePrix(int id, double newPrice, String columnName) {
        // Vérifier que la colonne est bien "prix_achat" ou "prix_vente"
        if (!columnName.equals("prix_achat") && !columnName.equals("prix_vente")) {
            throw new IllegalArgumentException("La colonne doit être 'prix_achat' ou 'prix_vente'");
        }
        String query = "UPDATE medicament SET " + columnName + " = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
