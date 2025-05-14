package com.example.pharmagest.receptionCommande.receptionCommandeModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.commande.commande;
import com.example.pharmagest.receptionCommande.receptionCommande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class receptionCommandeModele {

    // Récupérer les commandes depuis la table "Commande"
    public ObservableList<commande> getcommande() {
        ObservableList<commande> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Commande";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new commande(
                        rs.getInt("id"),
                        rs.getInt("no_commande"),
                        rs.getDate("date_commande"),
                        rs.getDouble("prix_total"),
                        rs.getInt("fournisseur_id"),
                        rs.getInt("utilisateur_id"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupérer les livraisons depuis la table "Livraison"
    public ObservableList<receptionCommande> getreceptionCommande() {
        ObservableList<receptionCommande> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM Livraison";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new receptionCommande(
                        rs.getInt("id"),
                        rs.getDate("date_livraison"),
                        rs.getString("status"),
                        rs.getInt("commande_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Insérer une nouvelle livraison dans la table Livraison
    public void addReceptionCommande(receptionCommande livraison) {
        String query = "INSERT INTO Livraison (date_livraison, status, commande_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setDate(1, livraison.getDateLivraison());
            pst.setString(2, livraison.getStatus());
            pst.setInt(3, livraison.getCommandeId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Exécuter la requête donnée pour récupérer les informations sur les médicaments pour une commande
    // Retourne une liste de chaînes contenant les détails pour la confirmation
    public List<String> getMedicamentDetails(int noCommande) {
        List<String> details = new ArrayList<>();
        String query = "SELECT m.id AS medicament_id, m.nom AS nom_medicament, " +
                "lc.quantites AS quantite, lc.prix_achat AS prix_unitaire, " +
                "(lc.quantites * lc.prix_achat) AS total " +
                "FROM LigneCommande lc " +
                "JOIN Medicament m ON lc.medicament_id = m.id " +
                "JOIN Commande c ON lc.commande_id = c.id " +
                "WHERE c.no_commande = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, noCommande);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String info = "ID Médicament : " + rs.getInt("medicament_id") +
                            ", Nom : " + rs.getString("nom_medicament") +
                            ", Quantité : " + rs.getInt("quantite") +
                            ", Prix Unitaire : " + rs.getDouble("prix_unitaire") +
                            ", Total : " + rs.getDouble("total");
                    details.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    // Mettre à jour le stock des médicaments pour une commande donnée
    public void updateMedicamentStock(int noCommande) {
        String selectQuery = "SELECT m.id AS medicament_id, lc.quantites AS quantite " +
                "FROM LigneCommande lc " +
                "JOIN Medicament m ON lc.medicament_id = m.id " +
                "JOIN Commande c ON lc.commande_id = c.id " +
                "WHERE c.no_commande = ?";

        String updateQuery = "UPDATE Medicament SET stock = stock + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectPst = conn.prepareStatement(selectQuery)) {
            selectPst.setInt(1, noCommande);
            try (ResultSet rs = selectPst.executeQuery()) {
                while (rs.next()) {
                    int medicamentId = rs.getInt("medicament_id");
                    int quantite = rs.getInt("quantite");
                    try (PreparedStatement updatePst = conn.prepareStatement(updateQuery)) {
                        updatePst.setInt(1, quantite);
                        updatePst.setInt(2, medicamentId);
                        updatePst.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mettre à jour le statut de la commande en "LIVRÉ"
    public void updateCommandeStatus(int commandeId) {
        String query = "UPDATE Commande SET statut = 'LIVRÉ' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, commandeId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
