package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.ConsommationProduit;
import com.example.flahasmart.services.ConsommationProduitService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.List;

public class ConsommationDetailController {

    @FXML private Label productNameLabel;
    @FXML private FlowPane cardsContainer;
    @FXML private Button closeBtn;

    private ConsommationProduitService service = new ConsommationProduitService();
    private int productId;

    public void setProductId(int id) {
        this.productId = id;
        loadConsommations();
    }

    public void setProductName(String name) {
        productNameLabel.setText(name);
    }

    private void loadConsommations() {
        try {
            List<ConsommationProduit> list = service.afficher();
            cardsContainer.getChildren().clear();

            for (ConsommationProduit c : list) {
                if (c.getIdStockProduit() == productId) {
                    // Créer une carte pour chaque consommation
                    VBox card = createConsommationCard(c);
                    cardsContainer.getChildren().add(card);
                }
            }

            if (cardsContainer.getChildren().isEmpty()) {
                VBox emptyCard = new VBox(10);
                emptyCard.setAlignment(javafx.geometry.Pos.CENTER);
                emptyCard.setPrefWidth(200);
                emptyCard.setStyle("-fx-background-color: #04850f; -fx-background-radius: 10; -fx-padding: 20;");
                emptyCard.getChildren().addAll(
                        new FontIcon("fas-box-open"),
                        new Label("Aucune consommation")
                );
                cardsContainer.getChildren().add(emptyCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createConsommationCard(ConsommationProduit c) {
        VBox card = new VBox(8);
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // En-tête avec dates
        Label datesLabel = new Label(String.format("Récolte: %s\nUtilisation: %s",
                c.getDateRecolte().toString(),
                c.getDateUtilisation().toString()));
        datesLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

        // Détails
        Label quantiteLabel = new Label(String.format("%.2f %s", c.getQuantiteUtilisee(), c.getUnite()));
        quantiteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label surfaceLabel = new Label(String.format("%.2f m²", c.getSurface()));
        surfaceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #27ae60;");

        card.getChildren().addAll(datesLabel, quantiteLabel, surfaceLabel);
        return card;
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}