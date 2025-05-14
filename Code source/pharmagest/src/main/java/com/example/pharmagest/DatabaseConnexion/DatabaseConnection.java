package com.example.pharmagest.DatabaseConnexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://postgresql-pharmagestbymahery.alwaysdata.net:5432/pharmagestbymahery_bdd"; // Changez l'URL selon votre configuration
    private static final String USER = "pharmagestbymahery"; // Remplacez par votre utilisateur
    private static final String PASSWORD = "Mahery2306"; // Remplacez par votre mot de passe

    public static Connection getConnection() throws SQLException {
        try {
            // Charger le driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            // Retourner la connexion
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("PostgreSQL Driver not found.");
        }
    }
}
