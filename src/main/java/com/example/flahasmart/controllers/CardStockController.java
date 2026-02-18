package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.StockProduit;
import com.example.flahasmart.services.StockProduitService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class CardStockController {

    @FXML
    private Label typeLabel;
    @FXML
    private Label varieteLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label statutLabel;

    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button consumptionBtn; // NOUVEAU BOUTON

    private StockProduit produit;
    private StockBackController parent;

    public void setData(StockProduit p, StockBackController parentController) {
        this.produit = p;
        this.parent = parentController;

        typeLabel.setText(p.getTypeProduit());
        varieteLabel.setText(p.getVariete());
        dateLabel.setText(p.getDateDebut().toString());
        statutLabel.setText(p.getStatut());

        // Gérer la visibilité des boutons si parent est null
        if (parent == null) {
            deleteBtn.setVisible(false);
            editBtn.setVisible(false);
            consumptionBtn.setVisible(false);
        } else {
            // Ajouter l'icône au bouton consommation si ce n'est pas déjà fait dans le FXML
            if (consumptionBtn.getGraphic() == null) {
                FontIcon icon = new FontIcon("fas-chart-line");
                icon.setIconSize(14);
                icon.setIconColor(javafx.scene.paint.Color.WHITE);
                consumptionBtn.setGraphic(icon);
            }
        }
    }

    // Méthode pour ouvrir le popup de consommation
    @FXML
    private void openConsumption() {
        try {
            // Charger le FXML du popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flahasmart/ConsommationPopup.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer les données du produit
            ConsommationPopupController controller = loader.getController();
            controller.setProductId(produit.getIdProduit());
            controller.setProductName(produit.getTypeProduit() + " " + produit.getVariete());

            // Créer et configurer la nouvelle fenêtre
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Gestion Consommation - " + produit.getTypeProduit());

            Scene scene = new Scene(root);
            scene.setFill(null); // Fond transparent
            scene.getStylesheets().add(getClass().getResource("/com/example/flahasmart/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de consommation");
        }
    }

    @FXML
    private void editItem() {
        if (parent != null) {
            parent.editProduit(produit);
        } else {
            showAlert("Information", "Fonction d'édition non disponible");
        }
    }

    @FXML
    private void deleteItem() {
        if (parent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le produit");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer " + produit.getTypeProduit() + " ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    parent.deleteProduit(produit);
                }
            });
        } else {
            showAlert("Information", "Fonction de suppression non disponible");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
