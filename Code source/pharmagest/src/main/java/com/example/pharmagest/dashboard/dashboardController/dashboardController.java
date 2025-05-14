package com.example.pharmagest.dashboard.dashboardController;

import com.example.pharmagest.login.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    // Boutons du dashboard
    @FXML
    private Button MaintenanceButton;
    @FXML
    private Button CommandeButton;
    @FXML
    private Button ReceptionCommandeButton;
    @FXML
    private Button VenteButton;
    @FXML
    private Button CaisseButton;

    private String userStatus;

    // Met à jour le statut de l'utilisateur et configure les boutons
    public void setUserStatus(String status) {
        this.userStatus = status;
        updateButtonAccess();
    }

    // Configure l'accès aux boutons selon le statut de l'utilisateur
    private void updateButtonAccess() {
        if ("Vendeur".equals(userStatus)) {
            // Masquer les boutons non accessibles aux vendeurs
            MaintenanceButton.setDisable(true);
            MaintenanceButton.setVisible(false);
            CommandeButton.setDisable(true);
            CommandeButton.setVisible(false);
            ReceptionCommandeButton.setDisable(true);
            ReceptionCommandeButton.setVisible(false);
            
            // Activer uniquement les boutons vente et caisse
            VenteButton.setDisable(false);
            VenteButton.setVisible(true);
            CaisseButton.setDisable(false);
            CaisseButton.setVisible(true);
        } else if ("Admin".equals(userStatus)) {
            // Activer tous les boutons pour l'admin
            MaintenanceButton.setDisable(false);
            MaintenanceButton.setVisible(true);
            CommandeButton.setDisable(false);
            CommandeButton.setVisible(true);
            ReceptionCommandeButton.setDisable(false);
            ReceptionCommandeButton.setVisible(true);
            VenteButton.setDisable(false);
            VenteButton.setVisible(true);
            CaisseButton.setDisable(false);
            CaisseButton.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Récupérer le statut de la session au démarrage
        String sessionStatus = UserSession.getUserStatus();
        if (sessionStatus != null) {
            setUserStatus(sessionStatus);
        }
    }

    // Gestion du changement d'utilisateur
    public void ChangerUtilisateurOnAction(ActionEvent actionEvent) throws IOException {
        // Nettoyer la session avant de changer d'utilisateur
        UserSession.clearSession();
        
        // Redirection vers la page de connexion
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-login/Login.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode pour rediriger vers la page Maintenance
    public void MaintenanceButtonOnAction(ActionEvent actionEvent) throws IOException {
        // Charger la page de Maintenance
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-maintenance/Maintenance.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode pour rediriger vers la page Vente
    public void VenteButtonOnAction(ActionEvent actionEvent) throws IOException {
        // Charger la page de Vente
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-vente/Vente.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode pour rediriger vers la page Caisse
    public void CaisseButtonOnAction(ActionEvent actionEvent) throws IOException {
        // Charger la page de Caisse
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-ligneVente/ligneVente-1.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode pour rediriger vers la page Commande
    public void CommandeButtonOnAction(ActionEvent actionEvent) throws IOException {
        // Charger la page de Commande
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-commande/Commande.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode pour rediriger vers la page Réception de commande
    public void ReceptionCommandeButtonOnAction(ActionEvent actionEvent) throws IOException {
        // Charger la page de Réception de commande
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-receptionCommande/Reception-commande.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void QuitterOnClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}

