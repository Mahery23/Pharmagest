package com.example.pharmagest.ligneVente.ligneVenteModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.ligneVente.ligneVente;
import com.example.pharmagest.medicaments.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class ligneVenteModele {

    // Récupère la liste de tous les médicaments ou seulement ceux sans prescription
    // si venteLibre est vrai
    public ObservableList<Medicament> getMedicaments(boolean venteLibre) {
        ObservableList<Medicament> medicaments = FXCollections.observableArrayList();
        String query;
        if (venteLibre) {
            // Pour vente libre, on ne récupère que les médicaments ne nécessitant pas de prescription
            query = "SELECT * FROM medicament WHERE necessite_prescription = false";
        } else {
            query = "SELECT * FROM medicament";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Medicament m = new Medicament();
                m.setId(rs.getInt("id"));
                m.setNom(rs.getString("nom"));
                m.setPrixAchat(rs.getDouble("prix_achat"));
                m.setPrixVente(rs.getDouble("prix_vente"));
                m.setStock(rs.getInt("stock"));
                m.setSeuilAlerte(rs.getInt("seuil_alerte"));
                m.setQuantiteMax(rs.getInt("quantite_max"));
                m.setNecessitePrescription(rs.getBoolean("necessite_prescription"));
                m.setFormeMedicament(rs.getString("forme_medicament"));
                m.setFamille(rs.getString("famille"));
                m.setDosage(rs.getString("dosage"));
                m.setFournisseurId(rs.getInt("fournisseur_id"));
                medicaments.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicaments;
    }

    // Méthode par défaut récupérant tous les médicaments (vente avec ordonnance ou pas)
    public ObservableList<Medicament> getMedicaments() {
        return getMedicaments(false);
    }

    // Récupère la liste des lignes de vente
    public ObservableList<ligneVente> getLigneVentes() {
        ObservableList<ligneVente> ligneVentes = FXCollections.observableArrayList();
        String query = "SELECT * FROM lignevente";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ligneVente lv = new ligneVente();
                lv.setId(rs.getInt("id"));
                lv.setVenteId(rs.getInt("vente_id"));
                lv.setMedicamentId(rs.getInt("medicament_id"));
                lv.setQuantite(rs.getInt("quantite"));
                lv.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                lv.setPrescriptionId(rs.getInt("prescription_id"));
                lv.setTypeVente(rs.getString("typeVente"));
                ligneVentes.add(lv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ligneVentes;
    }

    // Méthode pour insérer une vente dans la table Vente et renvoyer l'ID généré
    public int insererVente(int noVente, java.sql.Date dateVente, String typeVente, double prixTotal, String statutVente, int utilisateurId) {
        String query = "INSERT INTO vente(no_vente, date_vente, type_vente, prix_total, statut_vente, utilisateur_id) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, noVente);
            pst.setDate(2, dateVente);
            pst.setString(3, typeVente);
            pst.setDouble(4, prixTotal);
            pst.setString(5, statutVente);
            pst.setInt(6, utilisateurId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Méthode pour insérer une ligne de vente dans la table lignevente
    public boolean insererLigneVente(ligneVente ligne) {
        String query = "INSERT INTO lignevente(vente_id, medicament_id, quantite, prix_unitaire, prescription_id, typeVente) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, ligne.getVenteId());
            pst.setInt(2, ligne.getMedicamentId());
            pst.setInt(3, ligne.getQuantite());
            pst.setDouble(4, ligne.getPrixUnitaire());
            // Si la vente est libre, on passe NULL pour prescription_id
            if (ligne.getPrescriptionId() == 0) {
                pst.setNull(5, Types.INTEGER);
            } else {
                pst.setInt(5, ligne.getPrescriptionId());
            }
            pst.setString(6, ligne.getTypeVente());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour supprimer une ligne de vente (à appeler lors du click sur deleteButton)
    public boolean supprimerLigneVente(int ligneVenteId) {
        String query = "DELETE FROM lignevente WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, ligneVenteId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour effacer toutes les lignes d'une vente (à appeler lors du click sur clearVenteButtonOnClick)
    public boolean clearLigneVente(int venteId) {
        String query = "DELETE FROM lignevente WHERE vente_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, venteId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
