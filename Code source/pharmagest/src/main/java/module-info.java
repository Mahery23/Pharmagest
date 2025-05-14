module com.example.pharmagest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;
    requires java.desktop;

    exports com.example.pharmagest;
    exports com.example.pharmagest.commande.commandeController;
    exports com.example.pharmagest.dashboard.dashboardController;
    exports com.example.pharmagest.DatabaseConnexion;
    exports com.example.pharmagest.fournisseurs.fournisseurController;
    exports com.example.pharmagest.ligneVente.ligneVenteController;
    exports com.example.pharmagest.maintenance.maintenanceController;
    exports com.example.pharmagest.MajPrix.majPrixController;
    exports com.example.pharmagest.medicaments.medicamentsController;
    exports com.example.pharmagest.receptionCommande.receptionCommandeController;
    exports com.example.pharmagest.utilisateurs.utilisateurController;
    exports com.example.pharmagest.vente.venteController;
    exports com.example.pharmagest.prescription.prescriptionController to javafx.fxml;


    opens com.example.pharmagest.login to javafx.fxml;
    exports com.example.pharmagest.login;
    opens com.example.pharmagest.prescription.prescriptionController to javafx.fxml;
    opens com.example.pharmagest.login.LoginController to javafx.fxml;
    opens com.example.pharmagest.commande.commandeController to javafx.fxml;
    opens com.example.pharmagest.dashboard.dashboardController to javafx.fxml;
    opens com.example.pharmagest.fournisseurs.fournisseurController to javafx.fxml;
    opens com.example.pharmagest.ligneVente.ligneVenteController to javafx.fxml;
    opens com.example.pharmagest.vente.venteController to javafx.fxml;
    opens com.example.pharmagest.maintenance.maintenanceController to javafx.fxml;
    opens com.example.pharmagest.MajPrix.majPrixController to javafx.fxml;
    opens com.example.pharmagest.receptionCommande.receptionCommandeController to javafx.fxml;
    opens com.example.pharmagest.utilisateurs.utilisateurController to javafx.fxml;
    opens com.example.pharmagest.utilisateurs to javafx.base, javafx.fxml;
    opens com.example.pharmagest.fournisseurs to javafx.base, javafx.fxml;
    opens com.example.pharmagest.medicaments to javafx.base, javafx.fxml;
    opens com.example.pharmagest.MajPrix to javafx.base, javafx.fxml;
    opens com.example.pharmagest.commande to javafx.base;
    opens com.example.pharmagest.receptionCommande to javafx.base;
    opens com.example.pharmagest.prescription to javafx.base;
    opens com.example.pharmagest.ligneVente to javafx.base;
    opens com.example.pharmagest.vente to javafx.base;
    exports com.example.pharmagest.commande;
    exports com.example.pharmagest.vente;
    exports com.example.pharmagest.vente.venteModele;
    exports com.example.pharmagest.commande.commandeModele;
    opens com.example.pharmagest.medicaments.medicamentsController to javafx.fxml;



}
