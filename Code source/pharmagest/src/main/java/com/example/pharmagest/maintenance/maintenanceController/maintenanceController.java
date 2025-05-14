package com.example.pharmagest.maintenance.maintenanceController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class maintenanceController {
    public void UtilisateurOnClick (ActionEvent actionEvent)throws IOException{
        // Charger la page de changement d'utilisateur
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-utilisateurs/Utilisateurs.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void MedicamentsOnClick (ActionEvent actionEvent)throws IOException{
        // Charger la page de changement d'utilisateur
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-medicaments/medicament.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void majOnClick (ActionEvent actionEvent)throws IOException{
        // Charger la page de changement d'utilisateur
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-majPrix/majPrix.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void FournisseurOnClick (ActionEvent actionEvent)throws IOException{
        // Charger la page de changement d'utilisateur
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-fournisseurs/Fournisseurs.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void RetourDashboardOnClick (ActionEvent actionEvent)throws IOException{
        // Charger la page de changement d'utilisateur
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-dashboard/Dashboard.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
