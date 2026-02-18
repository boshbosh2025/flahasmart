package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.StockProduit;
import com.example.flahasmart.services.StockProduitService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;

public class StockBackController {

    @FXML private TextField typeField;
    @FXML private TextField varieteField;
    @FXML private ComboBox<String> statutBox;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Label errorLabel;
    @FXML private FlowPane container;
    @FXML private StackPane toastContainer;

    private final StockProduitService service = new StockProduitService();
    private StockProduit selectedProduit = null;

    /* =========================================================
                            INITIALIZE
       ========================================================= */

    @FXML
    public void initialize() {

        statutBox.getItems().addAll("en cours", "terminé", "en croissance");
        statutBox.setValue("en cours");

        // Interdire dates passées
        dateDebutPicker.setDayCellFactory(dp -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        loadProduits();
    }

    /* =========================================================
                            TOAST MODERNE
       ========================================================= */

    private void showToast(String message, String styleClass){

        if(toastContainer == null) return;

        Label toast = new Label(message);
        toast.getStyleClass().addAll("toast", styleClass);

        toastContainer.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> toastContainer.getChildren().remove(toast));

        new SequentialTransition(fadeIn, stay, fadeOut).play();
    }

    /* =========================================================
                            VALIDATION
       ========================================================= */

    private boolean validateFields(){

        resetStyles();
        errorLabel.setText("");
        boolean valid = true;

        String type = typeField.getText().trim();
        String variete = varieteField.getText().trim();

        if(type.length() < 3){
            markError(typeField, "Type min 3 caractères");
            valid = false;
        }
        else if(!type.matches("[a-zA-ZÀ-ÿ ]+")){
            markError(typeField, "Type invalide (lettres uniquement)");
            valid = false;
        }

        if(variete.length() < 2){
            markError(varieteField, "Variété trop courte");
            valid = false;
        }
        else if(!variete.matches("[a-zA-ZÀ-ÿ0-9 ]+")){
            markError(varieteField, "Variété invalide");
            valid = false;
        }

        if(dateDebutPicker.getValue() == null){
            markError(dateDebutPicker, "Date début obligatoire");
            valid = false;
        }

        if(dateFinPicker.getValue() != null && dateDebutPicker.getValue() != null){

            if(dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())){
                markError(dateFinPicker, "Date fin > date début");
                valid = false;
            }

            long days = dateFinPicker.getValue().toEpochDay() -
                    dateDebutPicker.getValue().toEpochDay();

            if(days > 365){
                markError(dateFinPicker, "Durée max 365 jours");
                valid = false;
            }
        }

        return valid;
    }

    /* =========================================================
                            ADD / UPDATE
       ========================================================= */

    @FXML
    public void add(){

        try{

            if(!validateFields()) return;

            if(selectedProduit == null){

                StockProduit s = new StockProduit();
                fillProduitFromForm(s);
                service.ajouter(s);

                showToast("Produit ajouté avec succès", "toast-success");
            }
            else{

                fillProduitFromForm(selectedProduit);
                service.modifier(selectedProduit);

                showToast("Produit modifié avec succès", "toast-success");
                selectedProduit = null;
            }

            clearForm();
            loadProduits();

        }catch(Exception e){
            showToast("Erreur : " + e.getMessage(), "toast-error");
        }
    }

    /* =========================================================
                            DELETE - CORRIGÉ
       ========================================================= */

    public void deleteProduit(StockProduit produit) {  // Renommé le paramètre pour plus de clarté

        // Ajouter une confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer ce produit ?");
        alert.setContentText(produit.getTypeProduit() + " - " + produit.getVariete());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // CORRECTION : utiliser getIdProduit() au lieu de passer l'objet
                    service.supprimer(produit.getIdProduit());
                    showToast("Produit supprimé avec succès", "toast-success");
                    loadProduits();

                    // Si le produit supprimé était sélectionné, vider le formulaire
                    if (selectedProduit != null && selectedProduit.getIdProduit() == produit.getIdProduit()) {
                        clearForm();
                        selectedProduit = null;
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    showToast("Suppression impossible : " + e.getMessage(), "toast-error");
                }
            }
        });
    }

    /* =========================================================
                            EDIT
       ========================================================= */

    public void editProduit(StockProduit p){

        selectedProduit = p;

        typeField.setText(p.getTypeProduit());
        varieteField.setText(p.getVariete());
        statutBox.setValue(p.getStatut());
        dateDebutPicker.setValue(p.getDateDebut());
        dateFinPicker.setValue(p.getDateFinEstimee());

        showToast("Mode modification activé pour : " + p.getTypeProduit(), "toast-warning");
    }

    /* =========================================================
                            LOAD
       ========================================================= */

    public void loadProduits(){

        try{
            container.getChildren().clear();

            List<StockProduit> list = service.afficher();

            for(StockProduit p : list){

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/flahasmart/CardStock.fxml")
                );

                Parent card = loader.load();
                CardStockController controller = loader.getController();
                controller.setData(p, this);

                container.getChildren().add(card);
            }

        }catch(Exception e){
            e.printStackTrace();
            showToast("Erreur chargement : " + e.getMessage(), "toast-error");
        }
    }

    /* =========================================================
                            HELPERS
       ========================================================= */

    private void fillProduitFromForm(StockProduit s){

        s.setTypeProduit(typeField.getText());
        s.setVariete(varieteField.getText());
        s.setDateDebut(dateDebutPicker.getValue());
        s.setDateFinEstimee(dateFinPicker.getValue());
        s.setStatut(statutBox.getValue());
        s.setIdUser(1);
    }

    private void clearForm(){
        typeField.clear();
        varieteField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        statutBox.setValue("en cours");
        selectedProduit = null;  // Important : réinitialiser la sélection
    }

    private void markError(Control field, String message){
        field.setStyle("-fx-border-color:#e53935;-fx-border-width:2;");
        errorLabel.setText(message);
    }

    private void resetStyles(){
        typeField.setStyle("");
        varieteField.setStyle("");
        dateDebutPicker.setStyle("");
        dateFinPicker.setStyle("");
    }
}