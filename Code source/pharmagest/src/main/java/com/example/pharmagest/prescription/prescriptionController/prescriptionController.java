package com.example.pharmagest.prescription.prescriptionController;

import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.prescription.prescription;
import com.example.pharmagest.prescription.prescriptionModele.prescriptionModele;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class prescriptionController implements Initializable {

    @FXML
    private TextField medicamentsTextField;
    @FXML
    private TextField nomPatientTextField;
    @FXML
    private TextField nomMedecinTextField;
    @FXML
    private TextField numeroprescriptionTextField;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private Button addButtonPrescription;
    @FXML
    private Button addMedicamentListeButton;
    @FXML
    private Button confirmerPrescriptionButton;
    @FXML
    private Button retourButton;

    @FXML
    private TextField rechercheMedicamentTexteField;

    // Table des médicaments
    @FXML
    private TableView<Medicament> medicamentsTableView;
    @FXML
    private TableColumn<Medicament, Integer> colIDMedicament;
    @FXML
    private TableColumn<Medicament, String> colMedicament;

    // Table des prescriptions
    @FXML
    private TableView<prescription> prescriptionTableView;
    @FXML
    private TableColumn<prescription, Integer> colID;
    @FXML
    private TableColumn<prescription, String> colNomMedecin;
    @FXML
    private TableColumn<prescription, LocalDate> colDate;
    @FXML
    private TableColumn<prescription, String> colNumero;
    @FXML
    private TableColumn<prescription, String> colPatient;
    @FXML
    private TableColumn<prescription, String> colMedicaments;

    private prescriptionModele modele;
    private ObservableList<Medicament> fullMedicamentsList;
    private FilteredList<Medicament> filteredMedicaments;

    // Liste des médicaments sélectionnés
    private ObservableList<Medicament> selectedMedicaments = FXCollections.observableArrayList();

    // Variable statique pour stocker l'id de la prescription confirmée
    public static int confirmedPrescriptionId = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modele = new prescriptionModele();

        // Initialiser la date à aujourd'hui
        dateDatePicker.setValue(LocalDate.now());

        // Configurer les colonnes de la table des médicaments
        colIDMedicament.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMedicament.setCellValueFactory(new PropertyValueFactory<>("nom"));

        // Charger la liste complète des médicaments
        fullMedicamentsList = modele.getMedicamentsSimple();
        // Créer une liste filtrable
        filteredMedicaments = new FilteredList<>(fullMedicamentsList, p -> true);
        medicamentsTableView.setItems(filteredMedicaments);

        // Ajouter un listener sur le champ de recherche pour filtrer la liste
        rechercheMedicamentTexteField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMedicaments.setPredicate(med -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return med.getNom().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Configurer les colonnes de la table des prescriptions
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNomMedecin.setCellValueFactory(new PropertyValueFactory<>("nom_medecin"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_prescription"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero_prescription"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("nom_patient"));
        colMedicaments.setCellValueFactory(new PropertyValueFactory<>("medicament"));

        loadPrescriptions();

        // Sélection d’un médicament (pour l'ajouter à la prescription)
        medicamentsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Actions supplémentaires possibles lors de la sélection
        });
    }

    // Remplir les champs à partir d'une prescription existante
    private void fillFieldsWithPrescription(prescription p) {
        nomMedecinTextField.setText(p.getNom_medecin());
        nomPatientTextField.setText(p.getNom_patient());
        numeroprescriptionTextField.setText(p.getNumero_prescription());
        dateDatePicker.setValue(p.getDate_prescription());
        medicamentsTextField.setText(p.getMedicament());
        selectedMedicaments.clear();
    }

    // Charger la liste des prescriptions
    private void loadPrescriptions() {
        ObservableList<prescription> list = modele.getAllPrescriptions();
        prescriptionTableView.setItems(list);
    }

    // Ajouter un médicament à la liste sélectionnée
    @FXML
    public void addMedicamentListeButtonOnCick(ActionEvent actionEvent) {
        Medicament med = medicamentsTableView.getSelectionModel().getSelectedItem();
        if (med != null) {
            // Vérifier si le médicament est déjà dans la liste sélectionnée
            boolean alreadyAdded = selectedMedicaments.stream().anyMatch(m -> m.getId() == med.getId());
            if (alreadyAdded) {
                showAlert("Information", "Médicament déjà ajouté", "Ce médicament a déjà été sélectionné.");
            } else {
                selectedMedicaments.add(med);
                updateMedicamentsTextField();
            }
        } else {
            showAlert("Erreur", "Aucun médicament sélectionné", "Veuillez sélectionner un médicament dans la liste.");
        }
    }

    // Mettre à jour le champ des médicaments sélectionnés
    private void updateMedicamentsTextField() {
        StringBuilder sb = new StringBuilder();
        for (Medicament med : selectedMedicaments) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(med.getNom());
        }
        medicamentsTextField.setText(sb.toString());
    }

    // Ajouter ou mettre à jour une prescription
    @FXML
    public void addButtonPrescriptionOnClick(ActionEvent actionEvent) {
        try {
            if (nomMedecinTextField.getText().isEmpty() ||
                    nomPatientTextField.getText().isEmpty() ||
                    numeroprescriptionTextField.getText().isEmpty() ||
                    dateDatePicker.getValue() == null ||
                    medicamentsTextField.getText().isEmpty()) {
                showAlert("Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }
            String numPresc = numeroprescriptionTextField.getText();
            if (modele.prescriptionNumberExists(numPresc)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Numéro existant");
                alert.setHeaderText("Ce numéro de prescription existe déjà.");
                alert.setContentText("Voulez-vous mettre à jour l'existant ?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    updateExistingPrescription(numPresc);
                }
            } else {
                prescription newPresc = new prescription(
                        numPresc,
                        nomMedecinTextField.getText(),
                        dateDatePicker.getValue(),
                        nomPatientTextField.getText(),
                        medicamentsTextField.getText()
                );
                if (modele.addPrescription(newPresc)) {
                    showAlert("Succès", "Ajout effectué", "La prescription a été ajoutée.");
                    clearFields();
                    loadPrescriptions();
                } else {
                    showAlert("Erreur", "Echec", "Impossible d'ajouter la prescription.");
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Exception", "Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateExistingPrescription(String numPresc) {
        prescription updated = new prescription(
                numPresc,
                nomMedecinTextField.getText(),
                dateDatePicker.getValue(),
                nomPatientTextField.getText(),
                medicamentsTextField.getText()
        );
        if (modele.updatePrescription(updated)) {
            showAlert("Succès", "Mise à jour", "La prescription a été mise à jour.");
            clearFields();
            loadPrescriptions();
        } else {
            showAlert("Erreur", "Echec", "La mise à jour a échoué.");
        }
    }

    // Lors de la confirmation, on récupère l'id de la prescription sélectionnée
    @FXML
    public void confirmerPrescriptionButtonOnClick(ActionEvent actionEvent) {
        prescription presc = prescriptionTableView.getSelectionModel().getSelectedItem();
        if (presc != null) {
            confirmedPrescriptionId = presc.getId(); // Mise à jour de la variable statique
            showAlert("Information", "Prescription confirmée", "La prescription " + presc.getNumero_prescription() + " est confirmée.");

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) confirmerPrescriptionButton.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Erreur", "Aucune sélection", "Sélectionnez une prescription dans la liste.");
        }
    }


    private void clearFields() {
        nomMedecinTextField.clear();
        nomPatientTextField.clear();
        numeroprescriptionTextField.clear();
        dateDatePicker.setValue(LocalDate.now());
        medicamentsTextField.clear();
        selectedMedicaments.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour réinitialiser l'état de la prescription
    public void resetPrescriptionState() {
        confirmedPrescriptionId = 0;
        // Vous pouvez également réinitialiser d'autres éléments de l'interface ici si besoin
    }
}
