package com.example.pharmagest.utilisateurs.utilisateurController;

import com.example.pharmagest.utilisateurs.Utilisateur;
import com.example.pharmagest.utilisateurs.utilisateurModele.UtilisateurModele;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class UtilisateurController {

    @FXML
    private TextField idTextField, nomTextField, prenomTextField, identifiantTextField, emailTextField, numeroTextField, newValueTextField;

    @FXML
    private PasswordField passwordTextField, passwordDeleteField;

    @FXML
    private Button addButton, updateButton, deleteButton, retourButton;

    @FXML
    private Label errorLabel;

    @FXML
    private TableView<Utilisateur> tableView;

    @FXML
    private TableColumn<Utilisateur, Integer> colID;

    @FXML
    private TableColumn<Utilisateur, String> colNom, colPrenom, colIdentifiant, colEmail, colNumero, colStatut;

    @FXML
    private MenuButton menuButton, statutMenuButton, userMenuButton, newStatutMenuButton;

    @FXML
    private MenuItem menuItemNom, menuItemPrenom, menuItemIdentifiant, menuItemEmail, menuItemNumero, menuItemStatut,
            menuItemAdmin, menuItemVendeur, menuItemNewAdmin, menuItemNewVendeur;

    private UtilisateurModele utilisateurModele = new UtilisateurModele();
    private Utilisateur selectedUserForUpdate;

    @FXML
    public void initialize() {
        // Configuration du MenuButton pour sélectionner le champ à modifier.
        menuItemNom.setOnAction(e -> menuButton.setText("nom"));
        menuItemPrenom.setOnAction(e -> menuButton.setText("prenom"));
        menuItemIdentifiant.setOnAction(e -> menuButton.setText("identifiant"));
        menuItemEmail.setOnAction(e -> menuButton.setText("email"));
        menuItemNumero.setOnAction(e -> menuButton.setText("numero"));
        menuItemStatut.setOnAction(e -> {
            menuButton.setText("statut");
            newValueTextField.setVisible(false);
            newStatutMenuButton.setVisible(true);
        });

        // Configuration du MenuButton pour le statut
        menuItemAdmin.setOnAction(e -> statutMenuButton.setText("Admin"));
        menuItemVendeur.setOnAction(e -> statutMenuButton.setText("Vendeur"));

        // Configuration du MenuButton pour le nouveau statut
        menuItemNewAdmin.setOnAction(e -> newStatutMenuButton.setText("Admin"));
        menuItemNewVendeur.setOnAction(e -> newStatutMenuButton.setText("Vendeur"));
        
        // Configuration des colonnes de la TableView.
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colIdentifiant.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Ajout d'un listener pour la sélection dans le tableau
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                deleteButton.setDisable(false);
            } else {
                deleteButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: white;");
                deleteButton.setDisable(true);
            }
        });

        // Configuration du MenuButton pour la sélection de l'utilisateur
        menuButton.setOnShowing(e -> {
            if (menuButton.getText().equals("statut")) {
                newValueTextField.setVisible(false);
                newStatutMenuButton.setVisible(true);
            } else {
                newValueTextField.setVisible(true);
                newStatutMenuButton.setVisible(false);
            }
        });

        loadUtilisateurs();
        updateUserMenuButton();
    }

    private void updateUserMenuButton() {
        userMenuButton.getItems().clear();
        for (Utilisateur user : tableView.getItems()) {
            MenuItem item = new MenuItem(user.getNom() + " " + user.getPrenom());
            item.setOnAction(e -> {
                userMenuButton.setText(user.getNom() + " " + user.getPrenom());
                selectedUserForUpdate = user;
            });
            userMenuButton.getItems().add(item);
        }
    }

    @FXML
    public void addButtonOnAction() {
        String nom = nomTextField.getText().trim();
        String prenom = prenomTextField.getText().trim();
        String identifiant = identifiantTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String numero = numeroTextField.getText().trim();
        String motDePasse = passwordTextField.getText().trim();
        String statut = statutMenuButton.getText().trim();

        // Vérification : si l'un des champs est vide, afficher un message d'erreur.
        if (nom.isEmpty() || prenom.isEmpty() || identifiant.isEmpty() || email.isEmpty() || 
            numero.isEmpty() || motDePasse.isEmpty() || statut.equals("Choisir le statut")) {
            showAlert("Veuillez remplir tous les champs pour l'ajout.", AlertType.WARNING);
            return;
        }

        boolean success = utilisateurModele.ajouterUtilisateur(nom, prenom, identifiant, email, numero, motDePasse, statut);

        if (success) {
            showAlert("Utilisateur ajouté avec succès !", AlertType.INFORMATION);
            clearAddFields();
            loadUtilisateurs();
        } else {
            showAlert("Erreur lors de l'ajout de l'utilisateur.", AlertType.ERROR);
        }
    }

    @FXML
    public void updateButtonOnAction() {
        if (selectedUserForUpdate == null) {
            showAlert("Veuillez sélectionner un utilisateur à modifier.", AlertType.WARNING);
            return;
        }

        String champ = menuButton.getText().trim();
        String newValue;

        if (champ.equals("statut")) {
            newValue = newStatutMenuButton.getText().trim();
            if (newValue.equals("Choisir le statut")) {
                showAlert("Veuillez choisir un nouveau statut.", AlertType.WARNING);
                return;
            }
        } else {
            newValue = newValueTextField.getText().trim();
            if (newValue.isEmpty()) {
                showAlert("La nouvelle valeur est vide.", AlertType.WARNING);
                return;
            }
        }

        if (champ.equals("choix menu")) {
            showAlert("Veuillez choisir un champ à modifier.", AlertType.WARNING);
            return;
        }

        boolean success = utilisateurModele.modifierChampUtilisateur(String.valueOf(selectedUserForUpdate.getId()), champ, newValue);

        if (success) {
            showAlert("Utilisateur modifié avec succès !", AlertType.INFORMATION);
            clearUpdateFields();
            loadUtilisateurs();
            updateUserMenuButton();
        } else {
            showAlert("Erreur lors de la modification.", AlertType.ERROR);
        }
    }

    @FXML
    public void deleteButtonOnAction() {
        Utilisateur selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Veuillez sélectionner un utilisateur à supprimer.", AlertType.WARNING);
            return;
        }

        String motDePasse = passwordDeleteField.getText().trim();
        if (motDePasse.isEmpty()) {
            showAlert("Veuillez entrer le mot de passe de l'utilisateur.", AlertType.WARNING);
            return;
        }

        // Vérifier si le mot de passe est correct
        if (utilisateurModele.verifierMotDePasse(String.valueOf(selectedUser.getId()), motDePasse)) {
            boolean success = utilisateurModele.supprimerUtilisateur(String.valueOf(selectedUser.getId()), motDePasse);

            if (success) {
                showAlert("Utilisateur supprimé avec succès !", AlertType.INFORMATION);
                clearDeleteFields();
                loadUtilisateurs();
            } else {
                showAlert("Attention : Cet utilisateur ne peut pas être supprimé car il est associé à des commandes.", AlertType.WARNING);
            }
        } else {
            showAlert("Mot de passe incorrect. La suppression a été annulée.", AlertType.ERROR);
        }
    }

    @FXML
    public void retourButtonOnClick() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-maintenance/Maintenance.fxml")));
        Stage stage = (Stage) retourButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void clearAddFields() {
        nomTextField.clear();
        prenomTextField.clear();
        identifiantTextField.clear();
        emailTextField.clear();
        numeroTextField.clear();
        passwordTextField.clear();
    }

    private void clearUpdateFields() {
        newValueTextField.clear();
        menuButton.setText("choix menu");
        newStatutMenuButton.setText("Choisir le statut");
        userMenuButton.setText("Choisir utilisateur");
        selectedUserForUpdate = null;
        newValueTextField.setVisible(true);
        newStatutMenuButton.setVisible(false);
    }

    private void clearDeleteFields() {
        passwordDeleteField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void loadUtilisateurs() {
        tableView.setItems(utilisateurModele.getUtilisateurs());
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
