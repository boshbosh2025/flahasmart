package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.StockProduit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CardStockFrontController {

    @FXML private Label typeLabel;
    @FXML private Label varieteLabel;
    @FXML private Label dateLabel;
    @FXML private Label statutLabel;
    @FXML private Button viewConsommationBtn;

    private StockProduit produit;

    public void setData(StockProduit p) {
        this.produit = p;
        typeLabel.setText(p.getTypeProduit());
        varieteLabel.setText(p.getVariete());
        dateLabel.setText(p.getDateDebut().toString());
        statutLabel.setText(p.getStatut());
    }

    @FXML
    private void openConsommationDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flahasmart/ConsommationDetailPopup.fxml"));
            Parent root = loader.load();
            ConsommationDetailController controller = loader.getController();
            controller.setProductId(produit.getIdProduit());
            controller.setProductName(produit.getTypeProduit() + " " + produit.getVariete());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Détails de consommation - " + produit.getTypeProduit());

            Scene scene = new Scene(root);
            scene.setFill(null);
            scene.getStylesheets().add(getClass().getResource("/com/example/flahasmart/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}