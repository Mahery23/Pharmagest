package com.example.pharmagest.receptionCommande.receptionCommandeController;

import com.example.pharmagest.commande.commande;
import com.example.pharmagest.receptionCommande.receptionCommande;
import com.example.pharmagest.receptionCommande.receptionCommandeModele.receptionCommandeModele;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class receptionCommandeController {

    // Boutons
    @FXML
    private Button retourButton;
    @FXML
    private Button confirmerLivraisonButton;
    @FXML
    private Button ajouterLivraisonButton; // Vous pouvez désactiver ou masquer ce bouton désormais

    // TableView pour les commandes
    @FXML
    private TableView<commande> commandeTableView;
    @FXML
    private TableColumn<commande, Integer> colID;
    @FXML
    private TableColumn<commande, Integer> colNoCommande;
    @FXML
    private TableColumn<commande, java.sql.Date> colDateCommande;
    @FXML
    private TableColumn<commande, Double> colPrixTotal;
    @FXML
    private TableColumn<commande, Integer> colFournisseurID;
    @FXML
    private TableColumn<commande, Integer> colUtilisateurID;
    @FXML
    private TableColumn<commande, String> colStatut;

    // TableView pour les livraisons
    @FXML
    private TableView<receptionCommande> livraisonTableView;
    @FXML
    private TableColumn<receptionCommande, Integer> colID_livraison;
    @FXML
    private TableColumn<receptionCommande, java.sql.Date> colDateLivraison;
    @FXML
    private TableColumn<receptionCommande, String> colStatus;
    @FXML
    private TableColumn<receptionCommande, Integer> colCommandeID;

    private final receptionCommandeModele model = new receptionCommandeModele();

    @FXML
    public void initialize() {
        // Initialiser les colonnes de la table des commandes avec les bons noms de propriété
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNoCommande.setCellValueFactory(new PropertyValueFactory<>("noCommande"));
        colDateCommande.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colPrixTotal.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        colFournisseurID.setCellValueFactory(new PropertyValueFactory<>("fournisseurId"));
        colUtilisateurID.setCellValueFactory(new PropertyValueFactory<>("utilisateurId"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Initialiser les colonnes de la table des livraisons
        colID_livraison.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDateLivraison.setCellValueFactory(new PropertyValueFactory<>("dateLivraison"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCommandeID.setCellValueFactory(new PropertyValueFactory<>("commandeId"));

        // Charger les données dans les tables
        loadData();

        // Ajouter un style pour les lignes livrées
        commandeTableView.setRowFactory(tv -> new TableRow<commande>() {
            @Override
            protected void updateItem(commande item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    setDisable(false);
                } else {
                    if ("LIVRÉ".equals(item.getStatut())) {
                        setStyle("-fx-background-color: #cccccc;");
                        setDisable(true);
                    } else {
                        setStyle("");
                        setDisable(false);
                    }
                }
            }
        });
    }

    private void loadData() {
        ObservableList<commande> commandes = model.getcommande();
        commandeTableView.setItems(commandes);

        ObservableList<receptionCommande> livraisons = model.getreceptionCommande();
        livraisonTableView.setItems(livraisons);
    }

    public void retourButtonOnclick(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/pharmagest/view-dashboard/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // Bouton CONFIRMER : met à jour le stock, insère dans Livraison (avec statut "LIVRÉ")
    // et met à jour le statut de la commande (en "LIVRÉ")
    public void confirmerLivraisonButtonOnClick(ActionEvent actionEvent) {
        commande selectedCommande = commandeTableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null && !"LIVRÉ".equals(selectedCommande.getStatut())) {
            // Récupérer les détails du médicament lié à la commande via la requête donnée
            List<String> details = model.getMedicamentDetails(selectedCommande.getNoCommande());
            StringJoiner joiner = new StringJoiner("\n");
            for (String detail : details) {
                joiner.add(detail);
            }

            // Afficher une boîte de confirmation avec les détails
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de livraison");
            confirmation.setHeaderText("Voulez-vous confirmer la livraison et mettre à jour le stock du médicament ?");
            confirmation.setContentText(joiner.toString());
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Mettre à jour le stock du médicament
                model.updateMedicamentStock(selectedCommande.getNoCommande());

                // Insérer une nouvelle livraison dans la table Livraison avec le statut "LIVRÉ"
                receptionCommande livraison = new receptionCommande();
                livraison.setDateLivraison(new Date(System.currentTimeMillis()));
                livraison.setStatus("LIVRÉ");
                livraison.setCommandeId(selectedCommande.getNoCommande());
                model.addReceptionCommande(livraison);

                // Mettre à jour le statut de la commande en "LIVRÉ"
                model.updateCommandeStatus(selectedCommande.getId());

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Livraison confirmée");
                info.setHeaderText(null);
                info.setContentText("La livraison a été confirmée et le stock mis à jour.");
                info.showAndWait();

                // Recharger les données dans les tableaux
                loadData();

                // Rafraîchir la vue
                commandeTableView.refresh();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sélection invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une commande non livrée.");
            alert.showAndWait();
        }
    }
}
