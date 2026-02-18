package com.example.flahasmart.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RoleSelectionController {

    @FXML private Button adminBtn;
    @FXML private Button agriculteurBtn;
    @FXML private Button clientBtn;

    @FXML
    private void openAdmin() {
        openWindow("/com/example/flahasmart/BackStock.fxml", "Administration - Gestion des stocks");
    }

    @FXML
    private void openAgriculteur() {
        openWindow("/com/example/flahasmart/AgrichStock.fxml", "Agriculteur - Suivi des cultures");
    }

    @FXML
    private void openClient() {
        openWindow("/com/example/flahasmart/FrontStock.fxml", "Client - Consultation");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED); // ou UNDECORATED selon votre style
            stage.setTitle(title);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/flahasmart/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

            // Fermer la fenêtre de sélection (optionnel)
            // ((Stage) adminBtn.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}