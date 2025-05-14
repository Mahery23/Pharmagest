package com.example.pharmagest.medicaments.medicamentsController;

import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.medicaments.medicamentsModele.MedicamentsModele;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class medicamentsController implements Initializable {

    @FXML
    private TextField nomMedocTextField;
    @FXML
    private TextField prixAchatMedocTextField;
    @FXML
    private TextField prixVenteMedocTextField;
    @FXML
    private TextField stockMedocTextField;
    @FXML
    private TextField seuilCommandeMedocTextField;
    @FXML
    private TextField quantiteMaxTextField;
    @FXML
    private TextField dosageMedocTextField;
    @FXML
    private TextField familleMedocTextField;
    @FXML
    private TextField formeMedocTextField;
    @FXML
    private MenuButton prescriptionMenuButton;
    @FXML
    private MenuButton fournisseurMenuButton;
    @FXML
    private TextField idMedocTextField;
    @FXML
    private TextField newValueMedocTextField;
    @FXML
    private MenuButton choixMenuButton;
    @FXML
    private TextField idDeleteMedocTextField;

    @FXML
    private Button retourButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Medicament> medicamentsTableView;
    @FXML
    private TableColumn<Medicament, Integer> colID;
    @FXML
    private TableColumn<Medicament, String> colNom;
    @FXML
    private TableColumn<Medicament, Double> colPrixAchat;
    @FXML
    private TableColumn<Medicament, Double> colPrixVente;
    @FXML
    private TableColumn<Medicament, Integer> colStock;
    @FXML
    private TableColumn<Medicament, Integer> colSeuilAlerte;
    @FXML
    private TableColumn<Medicament, Integer> colQuantiteMax;
    @FXML
    private TableColumn<Medicament, Boolean> colPrescription;
    @FXML
    private TableColumn<Medicament, String> colFournisseur;
    @FXML
    private TableColumn<Medicament, String> colFamille;
    @FXML
    private TableColumn<Medicament, String> colDosage;
    @FXML
    private TableColumn<Medicament, String> colFormeMedicament;

    @FXML
    private MenuButton medicamentMenuButton;

    // Instance du modèle
    private MedicamentsModele medicamentsModele = new MedicamentsModele();

    private Medicament selectedMedicament;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des colonnes de la TableView
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colSeuilAlerte.setCellValueFactory(new PropertyValueFactory<>("seuilAlerte"));
        colQuantiteMax.setCellValueFactory(new PropertyValueFactory<>("quantiteMax"));
        colPrescription.setCellValueFactory(new PropertyValueFactory<>("necessitePrescription"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));
        colFamille.setCellValueFactory(new PropertyValueFactory<>("famille"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colFormeMedicament.setCellValueFactory(new PropertyValueFactory<>("formeMedicament"));

        // Ajout d'un listener pour la sélection dans le tableau
        medicamentsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
                deleteButton.setDisable(false);
                idDeleteMedocTextField.setText(String.valueOf(newSelection.getId()));
                deleteButton.setText("Supprimer " + newSelection.getNom());
            } else {
                deleteButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: white;");
                deleteButton.setDisable(true);
                idDeleteMedocTextField.clear();
                deleteButton.setText("Supprimer");
            }
        });

        // Chargement initial des médicaments
        loadMedicaments();
        updateMedicamentMenuButton();

        // Remplir le MenuButton des fournisseurs
        fournisseurMenuButton.getItems().clear();
        for (String fournisseur : medicamentsModele.getFournisseurs()) {
            MenuItem item = new MenuItem(fournisseur);
            item.setOnAction(e -> fournisseurMenuButton.setText(fournisseur));
            fournisseurMenuButton.getItems().add(item);
        }
        // Remplir le MenuButton des choix de modification
        for (MenuItem item : choixMenuButton.getItems()) {
            item.setOnAction(e -> choixMenuButton.setText(item.getText()));
        }
        // Remplir le MenuButton des choix de prescription
        for (MenuItem item : prescriptionMenuButton.getItems()) {
            item.setOnAction(e -> prescriptionMenuButton.setText(item.getText()));
        }
    }

    private void updateMedicamentMenuButton() {
        medicamentMenuButton.getItems().clear();
        for (Medicament medicament : medicamentsTableView.getItems()) {
            MenuItem item = new MenuItem(medicament.getNom());
            item.setOnAction(e -> {
                selectedMedicament = medicament;
                medicamentMenuButton.setText(medicament.getNom());
            });
            medicamentMenuButton.getItems().add(item);
        }
    }

    @FXML
    private void addbuttonOnAction(ActionEvent event) throws SQLException {
        String nom = nomMedocTextField.getText().trim();
        double prixAchat, prixVente;
        int stock, seuilAlerte;
        int quantiteMax = Integer.parseInt(quantiteMaxTextField.getText().trim());

        try {
            prixAchat = Double.parseDouble(prixAchatMedocTextField.getText().trim());
            prixVente = Double.parseDouble(prixVenteMedocTextField.getText().trim());
            stock = Integer.parseInt(stockMedocTextField.getText().trim());
            seuilAlerte = Integer.parseInt(seuilCommandeMedocTextField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Veuillez vérifier les valeurs numériques.", AlertType.WARNING);
            return;
        }

        // Conversion de la valeur de prescription en booléen : "oui" => true, sinon false
        boolean prescriptionValue = prescriptionMenuButton.getText().equalsIgnoreCase("oui");

        // Récupérer le nom du fournisseur depuis le MenuButton (sans conversion en int)
        String fournisseurName = fournisseurMenuButton.getText().trim();
        if (fournisseurName.equals("Fournisseur")) {
            showAlert("Fournisseur invalide !", AlertType.ERROR);
            return;
        }

        String famille = familleMedocTextField.getText().trim();
        String dosage = dosageMedocTextField.getText().trim();
        String forme = formeMedocTextField.getText().trim();

        if (nom.isEmpty()) {
            showAlert("Veuillez remplir le champ Nom.", AlertType.WARNING);
            return;
        }

        if (medicamentsModele.ajouterMedicament(nom, prixAchat, prixVente, stock, seuilAlerte, quantiteMax,
                prescriptionValue, fournisseurName, famille, dosage, forme)) {
            showAlert("Médicament ajouté !", AlertType.INFORMATION);
            clearAddFields();
            loadMedicaments();
        } else {
            showAlert("Erreur d'ajout !", AlertType.ERROR);
        }
    }

    @FXML
    private void updateButtonOnAction(ActionEvent event) {
        if (selectedMedicament == null) {
            showAlert("Veuillez sélectionner un médicament à modifier.", AlertType.WARNING);
            return;
        }

        String champ = choixMenuButton.getText().trim();
        String newValue = newValueMedocTextField.getText().trim();

        if (champ.equals("Champ à modifier") || newValue.isEmpty()) {
            showAlert("Veuillez remplir tous les champs pour la modification.", AlertType.WARNING);
            return;
        }

        // Transformation du nom du champ tel qu'affiché en nom de colonne de la base de données
        String mappedChamp = mapChamp(champ);
        boolean success = medicamentsModele.modifierMedicament(String.valueOf(selectedMedicament.getId()), mappedChamp, newValue);
        if (success) {
            showAlert("Médicament modifié avec succès !", AlertType.INFORMATION);
            clearUpdateFields();
            loadMedicaments();
            updateMedicamentMenuButton();
        } else {
            showAlert("Erreur lors de la modification.", AlertType.ERROR);
        }
    }

    @FXML
    private void deleteButtonOnAction(ActionEvent event) {
        String id = idDeleteMedocTextField.getText().trim();
        if (id.isEmpty()) {
            showAlert("Veuillez sélectionner un médicament à supprimer.", AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce médicament ?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            boolean success = medicamentsModele.supprimerMedicament(id);
            if (success) {
                showAlert("Médicament supprimé avec succès !", AlertType.INFORMATION);
                clearDeleteFields();
                loadMedicaments();
            } else {
                showAlert("Attention : Ce médicament ne peut pas être supprimé car il est associé à des ventes.", AlertType.WARNING);
            }
        }
    }

    @FXML
    private void retourButtonOnClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/pharmagest/view-maintenance/Maintenance.fxml"));
        Stage stage = (Stage) retourButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /**
     * Méthode utilitaire pour transformer le nom affiché d'un champ en nom de colonne dans la BDD.
     *
     * @param champSelectionne Le nom tel qu'affiché dans l'interface.
     * @return Le nom de la colonne correspondant.
     */
    private String mapChamp(String champSelectionne) {
        switch (champSelectionne) {
            case "Nom":
                return "nom";
            case "Prix achat":
                return "prix_achat";
            case "Prix vente":
                return "prix_vente";
            case "Stock":
                return "stock";
            case "Seuil":
                return "seuil_alerte";
            case "Quantite Max":
                return "quantite_max";
            case "Prescription":
                return "necessite_prescription";
            case "Fournisseur":
                return "fournisseur_id";
            case "Famille":
                return "famille";
            case "Dosage":
                return "dosage";
            case "Forme":
                return "forme_medicament";
            default:
                return champSelectionne;
        }
    }

    private void clearAddFields() {
        nomMedocTextField.clear();
        prixAchatMedocTextField.clear();
        prixVenteMedocTextField.clear();
        stockMedocTextField.clear();
        seuilCommandeMedocTextField.clear();
        quantiteMaxTextField.clear();
        dosageMedocTextField.clear();
        familleMedocTextField.clear();
        formeMedocTextField.clear();
        prescriptionMenuButton.setText("Préscription");
        fournisseurMenuButton.setText("Fournisseur");
    }

    private void clearUpdateFields() {
        selectedMedicament = null;
        medicamentMenuButton.setText("Choisir un médicament");
        newValueMedocTextField.clear();
        choixMenuButton.setText("Champ à modifier");
    }

    private void clearDeleteFields() {
        idDeleteMedocTextField.clear();
        medicamentsTableView.getSelectionModel().clearSelection();
    }

    private void loadMedicaments() {
        medicamentsTableView.setItems(medicamentsModele.getMedicaments());
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
