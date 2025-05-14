package com.example.pharmagest.commande.commandeController;

import com.example.pharmagest.DatabaseConnexion.DatabaseConnection;
import com.example.pharmagest.commande.commande;
import com.example.pharmagest.commande.commandeModele.commandeModele;
import com.example.pharmagest.fournisseurs.Fournisseur;
import com.example.pharmagest.medicaments.Medicament;
import com.example.pharmagest.login.UserSession;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.awt.Desktop;
import java.util.Arrays;
import java.util.stream.Stream;

public class commandeController implements Initializable {

    @FXML
    private MenuButton fournisseurMenu;

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
    private TableColumn<Medicament, Double> colStock;
    @FXML
    private TableColumn<Medicament, Double> colSeuilAlerte;
    @FXML
    private TableColumn<Medicament, Double> colQuantite_max;
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
    private TableView<commande> ligneCommandeTableView;
    @FXML
    private TableColumn<commande, Integer> colIDCommande;
    // Utilisation de "noCommande" qui correspond au getter getNoCommande()
    @FXML
    private TableColumn<commande, Integer> colCommande_id;
    // Modification pour afficher le nom du médicament au lieu de son id
    @FXML
    private TableColumn<commande, String> colmedicament_id;
    @FXML
    private TableColumn<commande, Integer> colquantites;
    @FXML
    private TableColumn<commande, Double> colPrixUnitaire;

    private commandeModele modele = new commandeModele();
    private ObservableList<commande> lignesCommandeTemporaires = FXCollections.observableArrayList();
    // Les valeurs fournisseurId et fournisseurNom seront définies via le menu
    private int fournisseurId = -1;
    private String fournisseurNom = "";
    private double prixTotalCommande = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Medicament> allMedicaments = modele.getMedicaments();
        FilteredList<Medicament> filteredMedicaments = new FilteredList<>(allMedicaments, p -> true);
        medicamentsTableView.setItems(filteredMedicaments);
        ligneCommandeTableView.setItems(lignesCommandeTemporaires);

        ObservableList<Fournisseur> fournisseurs = modele.getAllFournisseurs();

        MenuItem allItem = new MenuItem("Tous les fournisseurs");
        allItem.setOnAction(e -> {
            fournisseurMenu.setText("Tous les fournisseurs");
            filteredMedicaments.setPredicate(m -> true);
            fournisseurId = -1;
            fournisseurNom = "";
        });
        fournisseurMenu.getItems().add(allItem);

        for (Fournisseur f : fournisseurs) {
            MenuItem item = new MenuItem(f.getNom());
            item.setOnAction(e -> {
                fournisseurMenu.setText(f.getNom());
                filteredMedicaments.setPredicate(m -> f.getNom().equals(m.getFournisseur()));
                if (lignesCommandeTemporaires.isEmpty()) {
                    fournisseurId = f.getId();
                    fournisseurNom = f.getNom();
                } else {
                    if (!f.getNom().equals(fournisseurNom)) {
                        showAlert("Erreur", "Tous les médicaments d'une commande doivent provenir du même fournisseur.");
                    }
                }
            });
            fournisseurMenu.getItems().add(item);
        }

        // Configuration des colonnes des médicaments
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colSeuilAlerte.setCellValueFactory(new PropertyValueFactory<>("seuilAlerte"));
        colQuantite_max.setCellValueFactory(new PropertyValueFactory<>("quantiteMax"));
        colPrescription.setCellValueFactory(new PropertyValueFactory<>("necessitePrescription"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));
        colFamille.setCellValueFactory(new PropertyValueFactory<>("famille"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colFormeMedicament.setCellValueFactory(new PropertyValueFactory<>("formeMedicament"));

        colIDCommande.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCommande_id.setCellValueFactory(new PropertyValueFactory<>("noCommande"));
        // Utiliser la nouvelle propriété medicamentNom pour l'affichage
        colmedicament_id.setCellValueFactory(new PropertyValueFactory<>("medicamentNom"));
        colquantites.setCellValueFactory(new PropertyValueFactory<>("quantites"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));

        // Appliquer un style pour griser les médicaments déjà en commande
        medicamentsTableView.setRowFactory(tv -> new TableRow<Medicament>() {
            @Override
            protected void updateItem(Medicament medicament, boolean empty) {
                super.updateItem(medicament, empty);

                if (medicament == null || empty) {
                    setStyle("");
                    setDisable(false);
                } else {
                    boolean estEnCommande = estMedicamentEnCommande(medicament.getId());
                    if (estEnCommande) {
                        setStyle("-fx-background-color: #d3d3d3;");
                        setDisable(true);
                    } else {
                        setStyle("");
                        setDisable(false);
                    }
                    for (commande ligne : lignesCommandeTemporaires) {
                        if (ligne.getMedicamentId() == medicament.getId()) {
                            setStyle("-fx-background-color: #d3d3d3;");
                            setDisable(true);
                            break;
                        }
                    }
                }
            }
        });
    }

    private boolean estMedicamentEnCommande(int medicamentId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM lignecommande lc " +
                             "JOIN commande c ON lc.commande_id = c.id " +
                             "WHERE lc.medicament_id = ? AND c.statut != 'LIVRÉ'")) {

            pstmt.setInt(1, medicamentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleMedicamentSelection(Medicament medicament) {
        if (fournisseurId == -1) {
            showAlert("Erreur", "Veuillez sélectionner un fournisseur avant d'ajouter des médicaments.");
            return;
        }
        if (!lignesCommandeTemporaires.isEmpty() && !medicament.getFournisseur().equals(fournisseurNom)) {
            showAlert("Erreur", "Tous les médicaments d'une commande doivent provenir du même fournisseur : " + fournisseurNom);
            return;
        }

        for (commande ligne : lignesCommandeTemporaires) {
            if (ligne.getMedicamentId() == medicament.getId()) {
                showAlert("Information", "Ce médicament est déjà dans votre commande. Vous ne pouvez pas l'ajouter deux fois.");
                return;
            }
        }

        int stockActuel = medicament.getStock();
        int quantiteMax = medicament.getQuantiteMax();
        int quantiteACommander = quantiteMax - stockActuel;

        if (quantiteACommander <= 0) {
            showAlert("Information", "Le stock actuel est déjà au maximum pour ce médicament.");
            return;
        }

        fournisseurNom = medicament.getFournisseur();

        commande ligneCommande = new commande();
        ligneCommande.setMedicamentId(medicament.getId());
        ligneCommande.setQuantites(quantiteACommander);
        ligneCommande.setPrixUnitaire(medicament.getPrixAchat());
        // Définir la propriété medicamentNom pour affichage dans la TableView
        ligneCommande.setMedicamentNom(medicament.getNom());

        lignesCommandeTemporaires.add(ligneCommande);
        ligneCommandeTableView.refresh();

        prixTotalCommande += quantiteACommander * medicament.getPrixAchat();
    }

    public void retourButtonOnclick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/pharmagest/view-dashboard/Dashboard.fxml")));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void ajouterLigneCommandeButtonOnClick(ActionEvent actionEvent) {
        Medicament selectedMedicament = medicamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedMedicament != null) {
            handleMedicamentSelection(selectedMedicament);
            medicamentsTableView.refresh();
        } else {
            showAlert("Erreur", "Veuillez sélectionner un médicament.");
        }
    }

    public void ajouterCommandeButtonOnClick(ActionEvent actionEvent) {
        if (lignesCommandeTemporaires.isEmpty()) {
            showAlert("Erreur", "Aucun médicament n'a été ajouté à la commande.");
            return;
        }

        if (fournisseurId == -1) {
            showAlert("Erreur", "Veuillez sélectionner un fournisseur.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                Random random = new Random();
                int orderNumber = 10000 + random.nextInt(90000);

                String insertCommandeQuery = "INSERT INTO commande (no_commande, fournisseur_id, utilisateur_id, prix_total, date_commande, statut) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmtCommande = conn.prepareStatement(insertCommandeQuery, Statement.RETURN_GENERATED_KEYS);

                pstmtCommande.setInt(1, orderNumber);
                pstmtCommande.setInt(2, fournisseurId);
                pstmtCommande.setInt(3, UserSession.getUserId());
                pstmtCommande.setDouble(4, prixTotalCommande);
                pstmtCommande.setDate(5, Date.valueOf(LocalDate.now()));
                pstmtCommande.setString(6, "EN ATTENTE");

                int affectedRows = pstmtCommande.executeUpdate();
                if (affectedRows == 0) throw new SQLException("Échec de la création de la commande.");

                int commandeId;
                try (ResultSet generatedKeys = pstmtCommande.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commandeId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Aucun ID de commande généré.");
                    }
                }

                String insertLigneQuery = "INSERT INTO lignecommande (commande_id, medicament_id, quantites, prix_achat) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtLigne = conn.prepareStatement(insertLigneQuery);

                for (commande ligne : lignesCommandeTemporaires) {
                    pstmtLigne.setInt(1, commandeId);
                    pstmtLigne.setInt(2, ligne.getMedicamentId());
                    pstmtLigne.setInt(3, ligne.getQuantites());
                    pstmtLigne.setDouble(4, ligne.getPrixUnitaire());
                    pstmtLigne.addBatch();
                }

                pstmtLigne.executeBatch();
                conn.commit();

                showAlert("Succès", "Commande n°" + orderNumber + " créée avec succès !");
                resetCommande();

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la création de la commande : " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur de connexion à la base de données.");
        }
    }

    public void resetCommandeButtonOnClick(ActionEvent actionEvent) {
        resetCommande();
    }

    private void resetCommande() {
        lignesCommandeTemporaires.clear();
        ligneCommandeTableView.refresh();
        prixTotalCommande = 0.0;
        fournisseurId = -1;
        fournisseurNom = "";
        fournisseurMenu.setText("Sélectionner un fournisseur");
        FilteredList<Medicament> filteredMedicaments = (FilteredList<Medicament>) medicamentsTableView.getItems();
        filteredMedicaments.setPredicate(m -> true);
        medicamentsTableView.refresh();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
