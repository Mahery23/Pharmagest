package com.example.pharmagest.ligneVente.ligneVenteController;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.ligneVente.ligneVente;
import com.example.pharmagest.ligneVente.ligneVenteModele.ligneVenteModele;
import com.example.pharmagest.login.UserSession;
import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.prescription.prescriptionController.prescriptionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Desktop;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class ligneVenteController {

    // TableView pour les médicaments
    @FXML
    private TableView<Medicament> medicamentsTableView;
    @FXML
    private TableColumn<Medicament, Integer> colID;
    @FXML
    private TableColumn<Medicament, String> colNom;
    @FXML
    private TableColumn<Medicament, Double> colPrixVente;
    @FXML
    private TableColumn<Medicament, Integer> colStock;
    @FXML
    private TableColumn<Medicament, Boolean> colPrescription;
    @FXML
    private TableColumn<Medicament, String> colFamille;
    @FXML
    private TableColumn<Medicament, String> colDosage;
    @FXML
    private TableColumn<Medicament, String> colFormeMedicament;

    // TableView pour les lignes de vente
    @FXML
    private TableView<ligneVente> ligneVenteTableView;
    @FXML
    private TableColumn<ligneVente, Integer> colIDLigneVente;
    @FXML
    private TableColumn<ligneVente, Integer> colVenteID;
    @FXML
    private TableColumn<ligneVente, Integer> colMedicamentID;
    @FXML
    private TableColumn<ligneVente, Integer> colQuantite;
    @FXML
    private TableColumn<ligneVente, Double> colPrixUnit;
    @FXML
    private TableColumn<ligneVente, Integer> colPrescriptionID;
    @FXML
    private TableColumn<ligneVente, String> colTypeVente;

    // Autres contrôles
    @FXML
    private RadioButton ordonanceRadio;
    @FXML
    private RadioButton venteLibreRadio;
    @FXML
    private TextField rechercheTextField;
    @FXML
    private TextField quantiteTextField;
    @FXML
    private Button addButtonLigneVente;
    @FXML
    private Button addButtonVente;
    @FXML
    private Button clearVenteButtonOnClick;
    @FXML
    private Button RetourDashboard;

    // Attributs internes
    private ObservableList<Medicament> allMedicaments;
    private FilteredList<Medicament> filteredMedicaments;
    private ligneVenteModele modele;
    // Variable pour stocker l'id de l'ordonnance confirmée (initialement 0 = aucune ordonnance)
    private int confirmedPrescriptionId = 0;

    @FXML
    public void initialize() {
        modele = new ligneVenteModele();

        // Configuration des colonnes de la TableView des médicaments
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colPrescription.setCellValueFactory(new PropertyValueFactory<>("necessitePrescription"));
        colFamille.setCellValueFactory(new PropertyValueFactory<>("famille"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colFormeMedicament.setCellValueFactory(new PropertyValueFactory<>("formeMedicament"));

        // Chargement initial de tous les médicaments
        allMedicaments = modele.getMedicaments();
        // Création d'une FilteredList pour le filtrage en temps réel
        filteredMedicaments = new FilteredList<>(allMedicaments, m -> filtreMedicament(m));
        medicamentsTableView.setItems(filteredMedicaments);

        // Désactiver la TableView tant qu'aucune option n'est sélectionnée
        medicamentsTableView.setDisable(true);

        // Listener pour le champ de recherche qui met à jour le filtre
        rechercheTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMedicaments.setPredicate(m -> filtreMedicament(m));
            medicamentsTableView.refresh();
        });

        // Configuration des colonnes de la TableView des lignes de vente
        colIDLigneVente.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVenteID.setCellValueFactory(new PropertyValueFactory<>("venteId"));
        colMedicamentID.setCellValueFactory(new PropertyValueFactory<>("medicamentId"));
        
        // Configuration de la cellFactory pour afficher le nom du médicament
        colMedicamentID.setCellFactory(column -> {
            return new TableCell<ligneVente, Integer>() {
                @Override
                protected void updateItem(Integer medicamentId, boolean empty) {
                    super.updateItem(medicamentId, empty);
                    if (empty || medicamentId == null) {
                        setText(null);
                    } else {
                        // Recherche du médicament dans la liste des médicaments
                        for (Medicament med : allMedicaments) {
                            if (med.getId() == medicamentId) {
                                setText(med.getNom());
                                break;
                            }
                        }
                    }
                }
            };
        });

        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPrixUnit.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colPrescriptionID.setCellValueFactory(new PropertyValueFactory<>("prescriptionId"));
        colTypeVente.setCellValueFactory(new PropertyValueFactory<>("typeVente"));

        // Initialisation de la TableView des lignes de vente avec une liste vide
        ligneVenteTableView.setItems(FXCollections.observableArrayList());

        // Mise en place d'un ToggleGroup pour les RadioButton
        ToggleGroup group = new ToggleGroup();
        venteLibreRadio.setToggleGroup(group);
        ordonanceRadio.setToggleGroup(group);

        // Listener sur le ToggleGroup pour activer la TableView dès qu'une option est sélectionnée
        group.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                medicamentsTableView.setDisable(false);
                // Met à jour le filtre pour tenir compte du type de vente
                filteredMedicaments.setPredicate(m -> filtreMedicament(m));
            } else {
                medicamentsTableView.setDisable(true);
            }
        });

        // Ajout d'un gestionnaire d'événements pour le double-clic sur une ligne
        ligneVenteTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ligneVente selectedLigne = ligneVenteTableView.getSelectionModel().getSelectedItem();
                if (selectedLigne != null) {
                    quantiteTextField.setText(String.valueOf(selectedLigne.getQuantite()));
                }
            }
        });
    }

    // Méthode de filtrage combinant recherche texte et vente libre
    private boolean filtreMedicament(Medicament m) {
        String searchText = rechercheTextField.getText().toLowerCase();
        boolean correspondRecherche = searchText.isEmpty() ||
                m.getNom().toLowerCase().contains(searchText) ||
                String.valueOf(m.getId()).contains(searchText);
        // Si vente libre est sélectionnée, on affiche uniquement les médicaments sans prescription
        boolean correspondTypeVente = !venteLibreRadio.isSelected() || (!m.isNecessitePrescription());
        return correspondRecherche && correspondTypeVente;
    }

    // Bouton pour retourner au dashboard
    public void RetourDashboardOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-dashboard/Dashboard.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Action sur le radio pour vente avec ordonnance
    public void ventePrescrisOnClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pharmagest/view-ligneVente/prescription.fxml"));
            Parent prescriptionRoot = loader.load();

            // Récupérer le contrôleur de la prescription
            prescriptionController prescController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(prescriptionRoot));

            // Ajouter un listener pour réinitialiser l'état si la fenêtre est fermée sans confirmation
            stage.setOnCloseRequest(event -> {
                prescController.resetPrescriptionState();
                ToggleGroup group = venteLibreRadio.getToggleGroup();
                group.selectToggle(null);
            });

            stage.showAndWait(); // Attend la fermeture de la fenêtre

            // Récupérer l'ID après confirmation
            confirmedPrescriptionId = prescriptionController.confirmedPrescriptionId;

            // Réinitialiser le filtre pour afficher tous les médicaments
            filteredMedicaments.setPredicate(m -> filtreMedicament(m));
            medicamentsTableView.refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Action sur le radio pour vente sans ordonnance
    public void venteLibreRadioOnClick(ActionEvent actionEvent) {
        // Met à jour le filtre pour n'afficher que les médicaments sans prescription
        filteredMedicaments.setPredicate(m -> filtreMedicament(m));
        medicamentsTableView.refresh();
    }

    // Fonction de vérification de la cohérence entre le type de vente et l'ID de prescription
    private boolean verifyPrescription(Medicament selectedMedicament) {
        if (selectedMedicament.isNecessitePrescription()) {
            if (venteLibreRadio.isSelected()) {
                showAlert(Alert.AlertType.WARNING, "Médicament nécessitant une ordonnance. Veuillez sélectionner vente avec ordonnance.");
                return false;
            }
            if (ordonanceRadio.isSelected() && prescriptionController.confirmedPrescriptionId == 0) {
                showAlert(Alert.AlertType.WARNING, "Aucune prescription confirmée. Veuillez confirmer une prescription existante.");
                return false;
            }
        } else {
            if (ordonanceRadio.isSelected() && prescriptionController.confirmedPrescriptionId == 0) {
                showAlert(Alert.AlertType.WARNING, "Aucune prescription confirmée. Veuillez confirmer une prescription existante.");
                return false;
            }
        }
        return true;
    }

    // Méthode pour ajouter une ligne de vente avec vérification
    public void addButtonLigneVenteOnAction(ActionEvent actionEvent) {
        Medicament selectedMedicament = medicamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedMedicament == null) {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un médicament.");
            return;
        }
        String quantiteStr = quantiteTextField.getText();
        if (quantiteStr == null || quantiteStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Entrez la quantité.");
            return;
        }
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "La quantité doit être un nombre.");
            return;
        }

        // Vérification que la quantité soit strictement supérieure à 0
        if (quantite <= 0) {
            showAlert(Alert.AlertType.WARNING, "La quantité doit être supérieure à 0.");
            return;
        }

        if (!verifyPrescription(selectedMedicament)) {
            return;
        }

        // Création de la ligne de vente
        ligneVente ligne = new ligneVente();
        ligne.setMedicamentId(selectedMedicament.getId());
        ligne.setQuantite(quantite);
        ligne.setPrixUnitaire(selectedMedicament.getPrixVente());

        if (venteLibreRadio.isSelected()) {
            ligne.setTypeVente("Libre");
            ligne.setPrescriptionId(0);
        } else if (ordonanceRadio.isSelected()) {
            ligne.setTypeVente("Prescrite");
            ligne.setPrescriptionId(prescriptionController.confirmedPrescriptionId);
        }
        ligneVenteTableView.getItems().add(ligne);
        quantiteTextField.clear();
    }

    // Envoie/valide la vente en insérant la vente et les lignes associées
    public void addButtonVenteOnAction(ActionEvent actionEvent) {
        ObservableList<ligneVente> lignes = ligneVenteTableView.getItems();
        if (lignes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune ligne de vente à envoyer.");
            return;
        }
        double prixTotal = lignes.stream().mapToDouble(l -> l.getPrixUnitaire() * l.getQuantite()).sum();
        int noVente = (int) (System.currentTimeMillis() % 100000);
        Date dateVente = Date.valueOf(LocalDate.now());
        String typeVente = lignes.get(0).getTypeVente();
        String statutVente = "EN COURS";
        int utilisateurId = UserSession.getUserId(); // Utilisation de l'ID de l'utilisateur connecté

        int venteId = modele.insererVente(noVente, dateVente, typeVente, prixTotal, statutVente, utilisateurId);
        if (venteId == -1) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'insertion de la vente.");
            return;
        }

        boolean allInserted = true;
        for (ligneVente ligne : lignes) {
            ligne.setVenteId(venteId);
            boolean inserted = modele.insererLigneVente(ligne);
            if (!inserted) {
                allInserted = false;
                break;
            }
        }

        if (allInserted) {
            showAlert(Alert.AlertType.INFORMATION, "Vente validée avec succès !");
            ligneVenteTableView.getItems().clear();
            // Réinitialiser les boutons radio
            ToggleGroup group = venteLibreRadio.getToggleGroup();
            group.selectToggle(null);
            medicamentsTableView.setDisable(true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'insertion d'une ou plusieurs lignes de vente.");
        }
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void deleteMedocButtonOnClick(ActionEvent actionEvent) {
        ligneVente selectedLigne = ligneVenteTableView.getSelectionModel().getSelectedItem();
        if (selectedLigne == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une ligne à supprimer.");
            return;
        }
        
        ligneVenteTableView.getItems().remove(selectedLigne);
        showAlert(Alert.AlertType.INFORMATION, "Ligne supprimée avec succès.");
    }

    public void confirmerQuantitesButtonOnClick(ActionEvent actionEvent) {
        ligneVente selectedLigne = ligneVenteTableView.getSelectionModel().getSelectedItem();
        if (selectedLigne == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une ligne à modifier.");
            return;
        }

        String quantiteStr = quantiteTextField.getText();
        if (quantiteStr == null || quantiteStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer une quantité.");
            return;
        }

        try {
            int nouvelleQuantite = Integer.parseInt(quantiteStr);
            if (nouvelleQuantite <= 0) {
                showAlert(Alert.AlertType.WARNING, "La quantité doit être supérieure à 0.");
                return;
            }

            selectedLigne.setQuantite(nouvelleQuantite);
            ligneVenteTableView.refresh();
            quantiteTextField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Quantité mise à jour avec succès.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "La quantité doit être un nombre valide.");
        }
    }

    // Efface la vente en vidant la TableView des lignes de vente
    public void clearLigneVenteButtonOnClick(ActionEvent actionEvent) {
        ligneVenteTableView.getItems().clear();
        showAlert(Alert.AlertType.INFORMATION, "Vente effacée.");
    }
}
