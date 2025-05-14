package com.example.pharmagest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ApplicationMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("view-login/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 400);
            stage.setTitle("PharmaGest");
        // Retirer la ligne suivante pour avoir les boutons de réduction et de fermeture
        // stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);  
        stage.show();
    }

            public static void main(String[] args) {
                launch();
            }
        }
