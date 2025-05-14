package com.example.pharmagest.commande.commandeModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.commande.commande;
import com.example.pharmagest.fournisseurs.Fournisseur;
import com.example.pharmagest.medicaments.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class commandeModele {

    public ObservableList<Medicament> getMedicaments() {
        ObservableList<Medicament> list = FXCollections.observableArrayList();
        String query = "SELECT m.*, f.nom AS fournisseur FROM medicament m " +
                "LEFT JOIN fournisseur f ON m.fournisseur_id = f.id " +
                "WHERE m.stock <= m.seuil_alerte";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Medicament medicament = new Medicament(
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
                );
                list.add(medicament);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Méthode mise à jour pour récupérer le nom du médicament via une jointure
    public ObservableList<commande> getLigneCommandes() {
        ObservableList<commande> list = FXCollections.observableArrayList();
        String query = "SELECT lc.id, lc.commande_id, m.nom AS medicament_nom, lc.quantites, lc.prix_achat " +
                "FROM lignecommande lc " +
                "JOIN medicament m ON lc.medicament_id = m.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Créer l'objet commande en utilisant les colonnes pertinentes
                commande cmd = new commande(
                        rs.getInt("id"),
                        rs.getInt("commande_id"),
                        rs.getInt("quantites"),
                        rs.getDouble("prix_achat")
                );
                // Définir le nom du médicament récupéré par la jointure
                cmd.setMedicamentNom(rs.getString("medicament_nom"));
                list.add(cmd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<Fournisseur> getAllFournisseurs() {
        ObservableList<Fournisseur> fournisseursList = FXCollections.observableArrayList();
        String query = "SELECT * FROM fournisseur";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur();
                fournisseur.setId(rs.getInt("id"));
                fournisseur.setNom(rs.getString("nom"));
                fournisseursList.add(fournisseur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fournisseursList;
    }

    public int createCommande(int noCommande, Date dateCommande, double prixTotal, int fournisseurId, int utilisateurId) {
        String query = "INSERT INTO commande (no_commande, date_commande, prix_total, fournisseur_id, utilisateur_id, statut) VALUES (?, ?, ?, ?, ?, ?)";
        int commandeId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, noCommande);
            pstmt.setDate(2, dateCommande);
            pstmt.setDouble(3, prixTotal);
            pstmt.setInt(4, fournisseurId);
            pstmt.setInt(5, utilisateurId);
            pstmt.setString(6, "EN ATTENTE");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commandeId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandeId;
    }

    public boolean createLigneCommande(int commandeId, int medicamentId, int quantites, double prixUnitaire) {
        String query = "INSERT INTO lignecommande (commande_id, medicament_id, quantites, prix_achat) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, commandeId);
            pstmt.setInt(2, medicamentId);
            pstmt.setInt(3, quantites);
            pstmt.setDouble(4, prixUnitaire);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMedicamentEnCommande(int medicamentId) {
        String query = "SELECT COUNT(*) FROM lignecommande lc " +
                "JOIN commande c ON lc.commande_id = c.id " +
                "WHERE lc.medicament_id = ? AND c.statut != 'LIVRÉ'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, medicamentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
