package com.example.pharmagest.vente.venteModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.vente.vente;
import com.example.pharmagest.medicaments.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class venteModele {

    // Récupère toutes les ventes
    public ObservableList<vente> getAllVentes() {
        ObservableList<vente> ventesList = FXCollections.observableArrayList();
        String query = "SELECT * FROM vente";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vente vente = new vente();
                vente.setId(rs.getInt("id"));
                vente.setNoVente(rs.getInt("no_vente"));
                vente.setDateVente(rs.getDate("date_vente"));
                vente.setTypeVente(rs.getString("type_vente"));
                vente.setPrixTotal(rs.getDouble("prix_total"));
                vente.setStatutVente(rs.getString("statut_vente"));
                vente.setUtilisateurId(rs.getInt("utilisateur_id"));
                ventesList.add(vente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventesList;
    }

    // Récupère la liste des médicaments associés à une vente donnée
    public ObservableList<Medicament> getMedicamentsByVenteId(int venteId) {
        ObservableList<Medicament> medicamentsList = FXCollections.observableArrayList();
        String query = "SELECT " +
                "    M.id AS medicament_id, " +
                "    M.nom AS nom_medicament, " +
                "    M.stock AS stock_actuel, " +
                "    LV.quantite AS quantite_vendue, " +
                "    LV.prix_unitaire " +
                "FROM LigneVente LV " +
                "JOIN Medicament M ON LV.medicament_id = M.id " +
                "JOIN Vente V ON LV.vente_id = V.id " +
                "WHERE V.id = " + venteId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()){
                Medicament med = new Medicament();
                med.setId(rs.getInt("medicament_id"));
                med.setNom(rs.getString("nom_medicament"));
                med.setStock(rs.getInt("stock_actuel"));
                med.setQuantiteVendue(rs.getInt("quantite_vendue"));
                med.setPrixVente(rs.getDouble("prix_unitaire"));
                medicamentsList.add(med);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return medicamentsList;
    }

    // Méthode pour supprimer une vente
    public void supprimerVente(int venteId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Désactiver l'auto-commit pour gérer la transaction
            conn.setAutoCommit(false);
            
            try {
                // Supprimer d'abord les bilans associés aux factures de cette vente
                String deleteBilansQuery = "DELETE FROM bilan WHERE facture_id IN (SELECT id FROM facture WHERE vente_id = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteBilansQuery)) {
                    pstmt.setInt(1, venteId);
                    pstmt.executeUpdate();
                }
                
                // Supprimer ensuite les factures associées
                String deleteFacturesQuery = "DELETE FROM facture WHERE vente_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteFacturesQuery)) {
                    pstmt.setInt(1, venteId);
                    pstmt.executeUpdate();
                }
                
                // Supprimer ensuite les lignes de vente associées
                String deleteLignesQuery = "DELETE FROM lignevente WHERE vente_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteLignesQuery)) {
                    pstmt.setInt(1, venteId);
                    pstmt.executeUpdate();
                }
                
                // Enfin supprimer la vente
                String deleteVenteQuery = "DELETE FROM vente WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteVenteQuery)) {
                    pstmt.setInt(1, venteId);
                    pstmt.executeUpdate();
                }
                
                // Valider la transaction
                conn.commit();
            } catch (SQLException e) {
                // En cas d'erreur, annuler la transaction
                conn.rollback();
                throw e;
            } finally {
                // Réactiver l'auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour insérer dans la table Facture
    public int insertFacture(int venteId, Date dateVente) {
        // Générer un numéro de facture unique (vous pouvez adapter la logique selon vos besoins)
        int numeroFacture = (int) (System.currentTimeMillis() % 100000);
        
        // Récupérer le montant total de la vente
        double montantTotal = 0;
        String queryMontant = "SELECT prix_total FROM Vente WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(queryMontant)) {
            pstmt.setInt(1, venteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                montantTotal = rs.getDouble("prix_total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        // Insérer la facture
        String query = "INSERT INTO Facture (numero_facture, date_emission, montant_total, vente_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, numeroFacture);
            pstmt.setDate(2, dateVente);
            pstmt.setDouble(3, montantTotal);
            pstmt.setInt(4, venteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Méthode pour insérer dans la table Bilan
    public boolean insertBilan(int factureId, Date dateVente, int utilisateurId) {
        String query = "INSERT INTO Bilan (facture_id, date_vente, utilisateur_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, factureId);
            pstmt.setDate(2, dateVente);
            pstmt.setInt(3, utilisateurId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour mettre à jour le statut de la vente
    public boolean updateVenteStatut(int venteId, String nouveauStatut) {
        String query = "UPDATE vente SET statut_vente = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nouveauStatut);
            pstmt.setInt(2, venteId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour récupérer les informations de l'utilisateur
    public Map<String, String> getUtilisateurInfo(int utilisateurId) {
        Map<String, String> userInfo = new HashMap<>();
        String query = "SELECT nom, prenom, statut FROM Utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utilisateurId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userInfo.put("nom", rs.getString("nom"));
                userInfo.put("prenom", rs.getString("prenom"));
                userInfo.put("statut", rs.getString("statut"));
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    // Méthode pour mettre à jour le stock d'un médicament
    public void updateMedicamentStock(int medicamentId, int nouveauStock) {
        String query = "UPDATE medicament SET stock = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, nouveauStock);
            pstmt.setInt(2, medicamentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer le stock actuel d'un médicament
    public int getStockActuel(int medicamentId) {
        String query = "SELECT stock FROM medicament WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, medicamentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
