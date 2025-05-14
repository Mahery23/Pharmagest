package com.example.pharmagest.fournisseurs.fournisseurController;

import com.example.pharmagest.fournisseurs.Fournisseur;
import com.example.pharmagest.fournisseurs.fournisseursModele.FournisseurModele;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FournisseurController implements Initializable {
    @FXML private TextField nomFournisseurTextField, adresseFournisseurTextField, contactFournisseurTextField, newValueTextField, idDeleteTextField;
    @FXML private TableView<Fournisseur> tableView;
    @FXML private TableColumn<Fournisseur, Integer> colId;
    @FXML private TableColumn<Fournisseur, String> colNom, colAdresse, colContact;
    @FXML private MenuButton choixMenu;
    @FXML private MenuItem menuItemNom, menuItemAdresse, menuItemContact;
    @FXML private MenuButton fournisseurMenuButton;
    @FXML private Button deleteButton;

    private final FournisseurModele fournisseurModele = new FournisseurModele();
    private Fournisseur selectedFournisseur;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Gestion du MenuButton
        menuItemNom.setOnAction(e -> {
            choixMenu.setText(menuItemNom.getText());
            newValueTextField.setPromptText("Nouveau nom...");
        });

        menuItemAdresse.setOnAction(e -> {
            choixMenu.setText(menuItemAdresse.getText());
            newValueTextField.setPromptText("Nouvelle adresse...");
        });

        menuItemContact.setOnAction(e -> {
            choixMenu.setText(menuItemContact.getText());
            newValueTextField.setPromptText("Nouveau contact...");
        });

        // Ajout d'un listener pour la sélection dans le tableau
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
                deleteButton.setDisable(false);
                idDeleteTextField.setText(String.valueOf(newSelection.getId()));
                deleteButton.setText("Supprimer " + newSelection.getNom());
            } else {
                deleteButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: white;");
                deleteButton.setDisable(true);
                idDeleteTextField.clear();
                deleteButton.setText("Supprimer");
            }
        });

        loadFournisseurs();
        updateFournisseurMenuButton();
    }

    private void updateFournisseurMenuButton() {
        fournisseurMenuButton.getItems().clear();
        for (Fournisseur fournisseur : tableView.getItems()) {
            MenuItem item = new MenuItem(fournisseur.getNom());
            item.setOnAction(e -> {
                selectedFournisseur = fournisseur;
                fournisseurMenuButton.setText(fournisseur.getNom());
            });
            fournisseurMenuButton.getItems().add(item);
        }
    }

    @FXML
    private void addButtonOnAction() {
        String nom = nomFournisseurTextField.getText().trim();
        String adresse = adresseFournisseurTextField.getText().trim();
        String contact = contactFournisseurTextField.getText().trim();

        if (nom.isEmpty() || adresse.isEmpty() || contact.isEmpty()) {
            showAlert("Veuillez remplir tous les champs !", Alert.AlertType.WARNING);
            return;
        }

        if (fournisseurModele.ajouterFournisseur(nom, adresse, contact)) {
            showAlert("Fournisseur ajouté !", Alert.AlertType.INFORMATION);
            clearAddFields();
            loadFournisseurs();
            updateFournisseurMenuButton();
        } else {
            showAlert("Erreur d'ajout !", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateButtonOnAction() {
        if (selectedFournisseur == null) {
            showAlert("Veuillez sélectionner un fournisseur à modifier.", Alert.AlertType.WARNING);
            return;
        }

        String champ = choixMenu.getText();
        String newValue = newValueTextField.getText().trim();

        if (champ.equals("Champ à modifier") || newValue.isEmpty()) {
            showAlert("Veuillez remplir tous les champs !", Alert.AlertType.WARNING);
            return;
        }

        if (fournisseurModele.modifierFournisseur(String.valueOf(selectedFournisseur.getId()), champ.toLowerCase(), newValue)) {
            showAlert("Modification réussie !", Alert.AlertType.INFORMATION);
            clearUpdateFields();
            loadFournisseurs();
            updateFournisseurMenuButton();
        } else {
            showAlert("Erreur lors de la modification", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void deleteButtonOnAction() {
        String id = idDeleteTextField.getText();

        if (id.isEmpty()) {
            showAlert("Veuillez sélectionner un fournisseur à supprimer !", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce fournisseur ?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (fournisseurModele.supprimerFournisseur(id)) {
                showAlert("Suppression réussie !", Alert.AlertType.INFORMATION);
                clearDeleteFields();
                loadFournisseurs();
                updateFournisseurMenuButton();
            } else {
                showAlert("Erreur de suppression !", Alert.AlertType.ERROR);
            }
        }
    }

    private void loadFournisseurs() {
        tableView.setItems(fournisseurModele.getFournisseurs());
    }

    private void clearAddFields() {
        nomFournisseurTextField.clear();
        adresseFournisseurTextField.clear();
        contactFournisseurTextField.clear();
    }

    private void clearUpdateFields() {
        selectedFournisseur = null;
        fournisseurMenuButton.setText("Choisir un fournisseur");
        newValueTextField.clear();
        choixMenu.setText("Champ à modifier");
    }

    private void clearDeleteFields() {
        idDeleteTextField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourButtonOnClick() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-maintenance/Maintenance.fxml")));
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}