package com.example.pharmagest.login.LoginModele;

import com.example.pharmagest.login.login;
import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginConnexion {

    public boolean validateLogin(login user, Label loginMessageLabel, Stage stage) {
        try (Connection connectDB = new DatabaseConnection().getConnection()) {
            if (connectDB == null) {
                loginMessageLabel.setText("Connexion à la base échouée.");
                return false;
            }

            // Requête pour vérifier l'identifiant, le mot de passe et récupérer le statut et l'ID
            String query = "SELECT \"id\", \"identifiant\", \"statut\" FROM public.\"utilisateur\" " +
                    "WHERE \"identifiant\" = ? AND \"motdepasse\" = ?";

            try (PreparedStatement stmt = connectDB.prepareStatement(query)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());

                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        // Stockage du statut et de l'ID dans l'objet utilisateur
                        user.setStatut(result.getString("statut"));
                        user.setId(result.getInt("id"));
                        loginMessageLabel.setText("Bienvenue à l'application");
                        return true;
                    }
                }
                loginMessageLabel.setText("Login invalide. Veuillez réessayer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Une erreur s'est produite.");
        }

        return false;
    }
}
