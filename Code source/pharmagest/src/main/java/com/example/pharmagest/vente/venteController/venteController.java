package com.example.pharmagest.vente.venteController;

import com.example.pharmagest.vente.vente;
import com.example.pharmagest.vente.venteModele.venteModele;
import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.login.UserSession;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;

public class venteController {

    // TableView pour afficher les ventes
    @FXML
    private TableView<vente> venteTableView;

    // TableView pour afficher les médicaments
    @FXML
    private TableView<Medicament> medicamentsTableView;
    @FXML
    private TableColumn<Medicament, String> colMedicament;
    @FXML
    private TableColumn<Medicament, Integer> colQuantite;  // Ici on utilise "stock" comme quantité
    @FXML
    private TableColumn<Medicament, Double> colPrixUnitaire;

    // Labels pour afficher la facture
    @FXML
    private Label numFactureLabel;
    @FXML
    private Label dateFactureLabel;
    @FXML
    private Label prixTotalLabel;
    @FXML
    private TextField montantRecuTextField;
    @FXML
    private Label montantRenduLabel;
    @FXML
    private TableColumn<vente, Integer> colNoVente;
    @FXML
    private TableColumn<vente, Date> colDate;
    @FXML
    private TableColumn<vente, String> colTypeVente;
    @FXML
    private TableColumn<vente, Double> colPrixTotal;
    @FXML
    private TableColumn<vente, String> colStatut;
    @FXML
    private TableColumn<vente, Integer> colVendeur;

    @FXML
    private Button suprimerVenteButton;

    // Modèle de données
    private venteModele venteModele = new venteModele();

    // Méthode d'initialisation appelée après le chargement du FXML
    @FXML
    public void initialize() {
        // Configuration des colonnes
        colNoVente.setCellValueFactory(new PropertyValueFactory<>("noVente"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        colTypeVente.setCellValueFactory(new PropertyValueFactory<>("typeVente"));
        colPrixTotal.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statutVente"));
        colVendeur.setCellValueFactory(new PropertyValueFactory<>("utilisateurId"));

        // Configuration de la colonne vendeur pour afficher le prénom
        colVendeur.setCellFactory(column -> new TableCell<vente, Integer>() {
            @Override
            protected void updateItem(Integer utilisateurId, boolean empty) {
                super.updateItem(utilisateurId, empty);
                if (empty) {
                    setText(null);
                } else {
                    Map<String, String> userInfo = venteModele.getUtilisateurInfo(utilisateurId);
                    setText(userInfo.get("prenom"));
                }
            }
        });

        // Configuration de la colonne statut pour le style
        colStatut.setCellFactory(column -> new TableCell<vente, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    if ("FACTURÉ".equals(statut)) {
                        setStyle("-fx-text-fill: gray;");
                        // Désactiver la sélection de la ligne
                        TableRow<vente> row = getTableRow();
                        if (row != null) {
                            row.setStyle("-fx-opacity: 0.7;");
                            row.setDisable(true);
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Charger les données
        venteTableView.setItems(venteModele.getAllVentes());

        // Ajouter un écouteur de sélection
        venteTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Vérifier si la vente est facturée
                if ("FACTURÉ".equals(newSelection.getStatutVente())) {
                    // Désactiver le bouton de suppression
                    suprimerVenteButton.setDisable(true);
                    // Désactiver la sélection
                    venteTableView.getSelectionModel().clearSelection();
                } else {
                    // Activer le bouton de suppression
                    suprimerVenteButton.setDisable(false);
                }
            } else {
                // Désactiver le bouton si aucune vente n'est sélectionnée
                suprimerVenteButton.setDisable(true);
            }
        });

        // Configuration des colonnes pour la TableView des médicaments
        colMedicament.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixVente"));

        // Initialisation des labels de la facture
        numFactureLabel.setText("");
        dateFactureLabel.setText("");
        prixTotalLabel.setText("");
        montantRecuTextField.clear();
        montantRenduLabel.setText("");

        // Listener sur la sélection d'une vente pour mettre à jour la facture et afficher les médicaments associés
        venteTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedVente) -> {
            if (selectedVente != null) {
                numFactureLabel.setText("FACT" + selectedVente.getNoVente());
                dateFactureLabel.setText(selectedVente.getDateVente().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                prixTotalLabel.setText(String.valueOf(selectedVente.getPrixTotal()));

                // Charger la liste des médicaments associés à la vente sélectionnée
                int venteId = selectedVente.getId();
                ObservableList<Medicament> medicaments = venteModele.getMedicamentsByVenteId(venteId);
                medicamentsTableView.setItems(medicaments);
            } else {
                numFactureLabel.setText("");
                dateFactureLabel.setText("");
                prixTotalLabel.setText("");
                medicamentsTableView.getItems().clear();
            }
        });
    }

    // Gestion du bouton de retour
    public void retourButtonOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-dashboard/Dashboard.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Réinitialise les informations de facture et la liste des médicaments
    public void clearFactureButtonOnClick(ActionEvent actionEvent) {
        numFactureLabel.setText("...");
        dateFactureLabel.setText("...");
        prixTotalLabel.setText("...");
        montantRecuTextField.clear();
        montantRenduLabel.setText("...");
        medicamentsTableView.getItems().clear();
    }

    @FXML
    public void calculerMontantRendu() {
        try {
            double montantRecu = Double.parseDouble(montantRecuTextField.getText());
            double prixTotal = Double.parseDouble(prixTotalLabel.getText());
            double montantRendu = montantRecu - prixTotal;
            montantRenduLabel.setText(String.format("%.2f", montantRendu));
        } catch (NumberFormatException e) {
            montantRenduLabel.setText("...");
        }
    }

    public void envoyerFactureButtonOnClick(ActionEvent actionEvent) {
        vente selectedVente = venteTableView.getSelectionModel().getSelectedItem();
        if (selectedVente == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Aucune vente sélectionnée");
            alert.setContentText("Veuillez sélectionner une vente pour générer la facture.");
            alert.showAndWait();
            return;
        }

        // Vérifier si le montant reçu est vide
        if (montantRecuTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Montant reçu manquant");
            alert.setContentText("Veuillez saisir le montant reçu avant de générer la facture.");
            alert.showAndWait();
            return;
        }

        try {
            double montantRecu = Double.parseDouble(montantRecuTextField.getText());
            double prixTotal = selectedVente.getPrixTotal();
            
            // Vérification du montant négatif
            if (montantRecu < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Montant invalide");
                alert.setContentText("Le montant reçu ne peut pas être négatif.");
                alert.showAndWait();
                return;
            }
            
            // Vérification du montant insuffisant
            if (montantRecu < prixTotal) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Montant insuffisant");
                alert.setContentText("Le montant reçu est inférieur au prix total. Veuillez entrer un montant suffisant.");
                alert.showAndWait();
                return;
            }

            // 1. Mise à jour du stock des médicaments
            ObservableList<Medicament> medicaments = venteModele.getMedicamentsByVenteId(selectedVente.getId());
            for (Medicament med : medicaments) {
                // Calculer le nouveau stock
                int nouveauStock = med.getStock() - med.getQuantiteVendue();
                if (nouveauStock < 0) {
                    throw new Exception("Stock insuffisant pour le médicament : " + med.getNom());
                }
                // Mettre à jour le stock dans la base de données
                venteModele.updateMedicamentStock(med.getId(), nouveauStock);
            }

            // 2. Insertion dans la table Facture
            int factureId = venteModele.insertFacture(selectedVente.getId(), selectedVente.getDateVente());
            if (factureId == -1) {
                throw new Exception("Erreur lors de la création de la facture");
            }

            // 3. Insertion dans la table Bilan avec l'ID de l'utilisateur connecté
            boolean bilanInserted = venteModele.insertBilan(
                factureId,
                selectedVente.getDateVente(),
                UserSession.getUserId() // Utilisation de l'ID de l'utilisateur connecté
            );

            // 4. Mise à jour du statut de la vente
            boolean statutUpdated = venteModele.updateVenteStatut(selectedVente.getId(), "FACTURÉ");

            // 5. Génération du PDF
            String fileName = "facture_" + selectedVente.getNoVente() + ".pdf";
            String filePath = "facturePDF/" + fileName;

            // Remplacer la partie qui génère le PDF par un simple message de succès
            showAlert(Alert.AlertType.INFORMATION, "Vente validée avec succès !");

            // Rafraîchir la table des ventes
            venteTableView.setItems(venteModele.getAllVentes());

            // Réinitialiser tous les champs après l'envoi réussi
            // Réinitialiser les labels
            numFactureLabel.setText("");
            dateFactureLabel.setText("");
            prixTotalLabel.setText("");
            montantRecuTextField.clear();
            montantRenduLabel.setText("");

            // Vider la sélection de la table des ventes
            venteTableView.getSelectionModel().clearSelection();

            // Vider la table des médicaments
            medicamentsTableView.getItems().clear();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération de la facture");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void suprimerVenteButtonOnClick(ActionEvent actionEvent) {
        vente selectedVente = venteTableView.getSelectionModel().getSelectedItem();
        if (selectedVente != null) {
            venteModele.supprimerVente(selectedVente.getId());
            // Rafraîchir la liste des ventes
            venteTableView.setItems(venteModele.getAllVentes());
            // Désactiver le bouton après la suppression
            suprimerVenteButton.setDisable(true);
        }
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
