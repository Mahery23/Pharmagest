package com.example.pharmagest.utilisateurs.utilisateurModele;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.utilisateurs.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurModele {

    public boolean ajouterUtilisateur(String nom, String prenom, String identifiant, String email, String numero, String motDePasse, String statut) {
        String query = "INSERT INTO utilisateur (nom, prenom, identifiant, email, numero, motdepasse, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, identifiant);
            pstmt.setString(4, email);
            pstmt.setString(5, numero);
            pstmt.setString(6, motDePasse);
            pstmt.setString(7, statut);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierChampUtilisateur(String id, String champ, String newValue) {
        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }

        if (!champ.equals("nom") && !champ.equals("prenom") && !champ.equals("identifiant") &&
                !champ.equals("email") && !champ.equals("numero") && !champ.equals("statut")) {
            return false;
        }
        String query = "UPDATE utilisateur SET " + champ + " = ? WHERE id = ?";
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

    public boolean supprimerUtilisateur(String id, String motDePasse) {
        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }

        String query = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public ObservableList<Utilisateur> getUtilisateurs() {
        ObservableList<Utilisateur> list = FXCollections.observableArrayList();
        String query = "SELECT id, nom, prenom, identifiant, email, numero, statut FROM utilisateur";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String identifiant = rs.getString("identifiant");
                String email = rs.getString("email");
                String numero = rs.getString("numero");
                String statut = rs.getString("statut");
                // Le mot de passe n'est pas affiché dans la table
                list.add(new Utilisateur(id, nom, prenom, identifiant, email, numero, "", statut));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean verifierMotDePasse(String id, String motDePasse) {
        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return false;
        }

        String query = "SELECT motdepasse FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String motDePasseBD = rs.getString("motdepasse");
                return motDePasseBD.equals(motDePasse);
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
