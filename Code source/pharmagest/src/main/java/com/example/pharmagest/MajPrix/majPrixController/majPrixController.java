package com.example.pharmagest.MajPrix.majPrixController;

import com.example.pharmagest.MajPrix.majPrix;
import com.example.pharmagest.MajPrix.majPrixModele.majPrixModele;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

public class majPrixController implements Initializable {

    @FXML
    private TableView<majPrix> medicamentsTableView;
    @FXML
    private TableColumn<majPrix, Integer> colID;
    @FXML
    private TableColumn<majPrix, String> colNom;
    @FXML
    private TableColumn<majPrix, Double> colPrixAchat;
    @FXML
    private TableColumn<majPrix, Double> colPrixVente;
    @FXML
    private TableColumn<majPrix, Integer> colStock;
    @FXML
    private TableColumn<majPrix, Integer> colSeuilAlerte;
    @FXML
    private TableColumn<majPrix, Boolean> colPrescription;
    @FXML
    private TableColumn<majPrix, Integer> colFournisseur;
    @FXML
    private TableColumn<majPrix, String> colFamille;
    @FXML
    private TableColumn<majPrix, String> colDosage;
    @FXML
    private TableColumn<majPrix, String> colFormeMedicament;

    @FXML
    private TextField newValueTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private MenuButton medicamentMenuButton;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem prixAchatMenuItem;
    @FXML
    private MenuItem prixVenteMenuItem;

    private majPrixModele model = new majPrixModele();
    private majPrix selectedMedicament;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des colonnes du tableau
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colFamille.setCellValueFactory(new PropertyValueFactory<>("famille"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colFormeMedicament.setCellValueFactory(new PropertyValueFactory<>("formeMedicament"));

        loadMedicaments();
        populateMedicamentMenuButton();

        // Lors de la sélection d'un champ à modifier, on met à jour le texte du MenuButton
        prixAchatMenuItem.setOnAction(event -> menuButton.setText("Prix Achat"));
        prixVenteMenuItem.setOnAction(event -> menuButton.setText("Prix Vente"));
    }

    // Charger les médicaments depuis la base de données dans la TableView
    private void loadMedicaments() {
        ObservableList<majPrix> medicaments = model.getAllMedicaments();
        medicamentsTableView.setItems(medicaments);
    }

    // Remplir le MenuButton avec la liste des médicaments
    private void populateMedicamentMenuButton() {
        ObservableList<majPrix> medicaments = model.getAllMedicaments();
        medicamentMenuButton.getItems().clear();
        
        for (majPrix med : medicaments) {
            MenuItem item = new MenuItem(med.getNom());
            item.setOnAction(event -> {
                selectedMedicament = med;
                medicamentMenuButton.setText(med.getNom());
            });
            medicamentMenuButton.getItems().add(item);
        }
    }

    // Méthode appelée lors du clic sur le bouton "Mettre à jour"
    @FXML
    public void uptdateButtonOnAction(ActionEvent actionEvent) {
        // Vérifier qu'un médicament a été sélectionné
        if (selectedMedicament == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un médicament.");
            return;
        }

        // Vérifier qu'un champ a bien été sélectionné
        String selectedField = menuButton.getText();
        if (selectedField.equals("Choisir le type")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner le type de prix à mettre à jour.");
            return;
        }

        // Récupérer et valider la nouvelle valeur saisie
        String newValueStr = newValueTextField.getText();
        double newPrice;
        try {
            newPrice = Double.parseDouble(newValueStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide pour le nouveau prix.");
            return;
        }

        // Déterminer le nom de la colonne à mettre à jour
        String columnName = selectedField.equals("Prix Achat") ? "prix_achat" : "prix_vente";

        boolean success = model.updatePrix(selectedMedicament.getId(), newPrice, columnName);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le prix a été mis à jour avec succès.");
            loadMedicaments();
            // Réinitialisation des champs après update
            newValueTextField.clear();
            medicamentMenuButton.setText("Choisir un médicament");
            menuButton.setText("Choisir le type");
            selectedMedicament = null;
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La mise à jour a échoué.");
        }
    }

    // Méthode pour revenir à la page de maintenance
    @FXML
    public void retourButtonOnClick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/pharmagest/view-maintenance/Maintenance.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Méthode utilitaire pour afficher des boîtes de dialogue
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
