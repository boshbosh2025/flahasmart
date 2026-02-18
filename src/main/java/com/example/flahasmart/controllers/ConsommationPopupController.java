package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.ConsommationProduit;
import com.example.flahasmart.services.ConsommationProduitService;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ConsommationPopupController {

    @FXML private Label productNameLabel;
    @FXML private TextField surfaceField;
    @FXML private TextField quantiteField;
    @FXML private ComboBox<String> uniteCombo;
    @FXML private DatePicker dateRecoltePicker;
    @FXML private DatePicker dateUtilisationPicker;
    @FXML private TextArea notesArea;
    @FXML private Label surfaceErrorLabel;
    @FXML private Label quantiteErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private ListView<ConsommationProduit> consumptionListView;
    @FXML private Label totalQuantiteLabel;
    @FXML private Label totalEntriesLabel;
    @FXML private Label totalSurfaceLabel;
    @FXML private Button saveBtn;
    @FXML private Button clearBtn;
    @FXML private Button closeBtn;

    private int productId;
    private String productName;
    private ObservableList<ConsommationProduit> consommationList = FXCollections.observableArrayList();
    private ConsommationProduitService service = new ConsommationProduitService();
    private ConsommationProduit selectedConsommation;

    private static final double SURFACE_MIN = 0.01;
    private static final double SURFACE_MAX = 10000.0;
    private static final double QUANTITE_MIN = 0.001;
    private static final double QUANTITE_MAX = 100000.0;

    @FXML
    public void initialize() {
        uniteCombo.setValue("kg");
        dateRecoltePicker.setValue(LocalDate.now());
        dateUtilisationPicker.setValue(LocalDate.now());

        setupValidators();
        setupListView();
        hideErrorLabels();
    }

    private void hideErrorLabels() {
        if (surfaceErrorLabel != null) surfaceErrorLabel.setVisible(false);
        if (quantiteErrorLabel != null) quantiteErrorLabel.setVisible(false);
        if (dateErrorLabel != null) dateErrorLabel.setVisible(false);
    }

    private void setupValidators() {
        surfaceField.textProperty().addListener((obs, oldVal, newVal) -> validateSurface());
        quantiteField.textProperty().addListener((obs, oldVal, newVal) -> validateQuantite());
        dateRecoltePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates());
        dateUtilisationPicker.valueProperty().addListener((obs, oldVal, newVal) -> validateDates());

        restrictToNumbers(surfaceField, true);
        restrictToNumbers(quantiteField, true);
    }

    private void restrictToNumbers(TextField field, boolean allowDecimal) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            String regex = allowDecimal ? "\\d*\\.?\\d*" : "\\d*";
            if (!newVal.matches(regex)) field.setText(oldVal);
        });
    }

    private boolean validateSurface() {
        String text = surfaceField.getText();
        if (text == null || text.trim().isEmpty()) {
            showFieldError(surfaceField, surfaceErrorLabel, "Surface obligatoire");
            return false;
        }
        try {
            double val = Double.parseDouble(text.trim());
            if (val < SURFACE_MIN) {
                showFieldError(surfaceField, surfaceErrorLabel, "Surface min " + SURFACE_MIN + " m²");
                return false;
            }
            if (val > SURFACE_MAX) {
                showFieldError(surfaceField, surfaceErrorLabel, "Surface max " + SURFACE_MAX + " m²");
                return false;
            }
            clearFieldError(surfaceField, surfaceErrorLabel);
            return true;
        } catch (NumberFormatException e) {
            showFieldError(surfaceField, surfaceErrorLabel, "Nombre invalide");
            return false;
        }
    }

    private boolean validateQuantite() {
        String text = quantiteField.getText();
        if (text == null || text.trim().isEmpty()) {
            showFieldError(quantiteField, quantiteErrorLabel, "Quantité obligatoire");
            return false;
        }
        try {
            double val = Double.parseDouble(text.trim());
            if (val < QUANTITE_MIN) {
                showFieldError(quantiteField, quantiteErrorLabel, "Quantité min " + QUANTITE_MIN);
                return false;
            }
            if (val > QUANTITE_MAX) {
                showFieldError(quantiteField, quantiteErrorLabel, "Quantité max " + QUANTITE_MAX);
                return false;
            }
            clearFieldError(quantiteField, quantiteErrorLabel);
            return true;
        } catch (NumberFormatException e) {
            showFieldError(quantiteField, quantiteErrorLabel, "Nombre invalide");
            return false;
        }
    }

    private boolean validateDates() {
        LocalDate recolte = dateRecoltePicker.getValue();
        LocalDate utilisation = dateUtilisationPicker.getValue();

        if (recolte == null) {
            showDateError("Date récolte manquante");
            return false;
        }
        if (utilisation == null) {
            showDateError("Date utilisation manquante");
            return false;
        }
        if (recolte.isAfter(LocalDate.now())) {
            showDateError("La date de récolte ne peut pas être future");
            return false;
        }
        if (utilisation.isBefore(recolte)) {
            showDateError("L'utilisation doit être après la récolte");
            return false;
        }
        if (utilisation.isAfter(LocalDate.now())) {
            showDateError("La date d'utilisation ne peut pas être future");
            return false;
        }
        clearDateError();
        return true;
    }

    private boolean validateUnite() {
        if (uniteCombo.getValue() == null || uniteCombo.getValue().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez choisir une unité");
            return false;
        }
        return true;
    }

    private boolean validateAll() {
        return validateSurface() && validateQuantite() && validateDates() && validateUnite();
    }

    private void showFieldError(TextField field, Label label, String msg) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        if (label != null) {
            label.setText(msg);
            label.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> label.setVisible(false));
            pause.play();
        }
    }

    private void clearFieldError(TextField field, Label label) {
        field.setStyle("");
        if (label != null) label.setVisible(false);
    }

    private void showDateError(String msg) {
        dateRecoltePicker.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        dateUtilisationPicker.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        if (dateErrorLabel != null) {
            dateErrorLabel.setText(msg);
            dateErrorLabel.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                dateErrorLabel.setVisible(false);
                dateRecoltePicker.setStyle("");
                dateUtilisationPicker.setStyle("");
            });
            pause.play();
        }
    }

    private void clearDateError() {
        dateRecoltePicker.setStyle("");
        dateUtilisationPicker.setStyle("");
        if (dateErrorLabel != null) dateErrorLabel.setVisible(false);
    }

    private void setupListView() {
        consumptionListView.setCellFactory(listView -> new ListCell<ConsommationProduit>() {
            @Override
            protected void updateItem(ConsommationProduit c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(8);
                    card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 12; -fx-border-color: #f0f0f0; -fx-border-radius: 10;");

                    HBox header = new HBox(10);
                    header.setStyle("-fx-alignment: CENTER_LEFT;");

                    FontIcon icon = new FontIcon("fas-box");
                    icon.setIconSize(20);
                    icon.setIconColor(javafx.scene.paint.Color.valueOf("#9b59b6"));

                    Label dates = new Label(String.format("Récolte: %s | Utilisation: %s",
                            c.getDateRecolte().toString(), c.getDateUtilisation().toString()));
                    dates.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                    header.getChildren().addAll(icon, dates);

                    HBox details = new HBox(15);
                    details.setStyle("-fx-alignment: CENTER_LEFT;");
                    Label qty = new Label(String.format("%.2f %s", c.getQuantiteUtilisee(), c.getUnite()));
                    qty.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    Label surf = new Label(String.format("%.2f m²", c.getSurface()));
                    surf.setStyle("-fx-font-size: 13px; -fx-text-fill: #27ae60;");
                    details.getChildren().addAll(qty, surf);

                    HBox actions = new HBox(8);
                    actions.setStyle("-fx-alignment: CENTER_RIGHT;");

                    Button viewBtn = new Button();
                    viewBtn.setGraphic(new FontIcon("fas-eye"));
                    viewBtn.getStyleClass().add("btn-edit");
                    viewBtn.setOnAction(e -> viewConsommation(c));

                    Button editBtn = new Button();
                    editBtn.setGraphic(new FontIcon("fas-edit"));
                    editBtn.getStyleClass().add("btn-edit");
                    editBtn.setOnAction(e -> editConsommation(c));

                    Button deleteBtn = new Button();
                    deleteBtn.setGraphic(new FontIcon("fas-trash"));
                    deleteBtn.getStyleClass().add("btn-delete");
                    deleteBtn.setOnAction(e -> deleteConsommation(c));

                    actions.getChildren().addAll(viewBtn, editBtn, deleteBtn);

                    card.getChildren().addAll(header, details, actions);
                    setGraphic(card);
                }
            }
        });
    }

    public void setProductId(int id) {
        this.productId = id;
        loadConsommations();
    }

    public void setProductName(String name) {
        this.productName = name;
        productNameLabel.setText(name);
    }

    private void loadConsommations() {
        try {
            List<ConsommationProduit> list = service.afficher();
            consommationList.clear();
            for (ConsommationProduit c : list) {
                if (c.getIdStockProduit() == productId) {
                    consommationList.add(c);
                }
            }
            consumptionListView.setItems(consommationList);
            updateStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les consommations");
        }
    }

    private void updateStatistics() {
        double totalQ = consommationList.stream().mapToDouble(ConsommationProduit::getQuantiteUtilisee).sum();
        double totalS = consommationList.stream().mapToDouble(ConsommationProduit::getSurface).sum();
        totalQuantiteLabel.setText(String.format("%.1f", totalQ));
        totalEntriesLabel.setText(String.valueOf(consommationList.size()));
        totalSurfaceLabel.setText(String.format("%.1f", totalS));
    }

    @FXML
    private void saveConsommation() {
        if (!validateAll()) {
            showAlert("Erreur de saisie", "Veuillez corriger les champs en rouge");
            return;
        }
        try {
            double surface = Double.parseDouble(surfaceField.getText().trim());
            double quantite = Double.parseDouble(quantiteField.getText().trim());
            String unite = uniteCombo.getValue();
            Date dateR = Date.valueOf(dateRecoltePicker.getValue());
            Date dateU = Date.valueOf(dateUtilisationPicker.getValue());

            if (selectedConsommation == null) {
                ConsommationProduit c = new ConsommationProduit();
                c.setIdStockProduit(productId);
                c.setSurface(surface);
                c.setQuantiteUtilisee(quantite);
                c.setUnite(unite);
                c.setDateRecolte(dateR);
                c.setDateUtilisation(dateU);
                service.ajouter(c);
                showSuccess("Consommation ajoutée");
            } else {
                selectedConsommation.setSurface(surface);
                selectedConsommation.setQuantiteUtilisee(quantite);
                selectedConsommation.setUnite(unite);
                selectedConsommation.setDateRecolte(dateR);
                selectedConsommation.setDateUtilisation(dateU);
                service.modifier(selectedConsommation);
                showSuccess("Consommation modifiée");
                selectedConsommation = null;
            }
            loadConsommations();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'enregistrement");
        }
    }

    @FXML
    private void clearForm() {
        surfaceField.clear();
        quantiteField.clear();
        uniteCombo.setValue("kg");
        dateRecoltePicker.setValue(LocalDate.now());
        dateUtilisationPicker.setValue(LocalDate.now());
        selectedConsommation = null;

        clearFieldError(surfaceField, surfaceErrorLabel);
        clearFieldError(quantiteField, quantiteErrorLabel);
        clearDateError();

        saveBtn.setText("Enregistrer");
        saveBtn.setGraphic(new FontIcon("fas-save"));
    }

    private void viewConsommation(ConsommationProduit c) {
        surfaceField.setText(String.valueOf(c.getSurface()));
        quantiteField.setText(String.valueOf(c.getQuantiteUtilisee()));
        uniteCombo.setValue(c.getUnite());
        dateRecoltePicker.setValue(c.getDateRecolte().toLocalDate());
        dateUtilisationPicker.setValue(c.getDateUtilisation().toLocalDate());
        setFieldsDisabled(true);
        saveBtn.setText("Modifier");
        saveBtn.setGraphic(new FontIcon("fas-edit"));
        saveBtn.setOnAction(e -> {
            setFieldsDisabled(false);
            editConsommation(c);
        });
        selectedConsommation = c;
    }

    private void editConsommation(ConsommationProduit c) {
        surfaceField.setText(String.valueOf(c.getSurface()));
        quantiteField.setText(String.valueOf(c.getQuantiteUtilisee()));
        uniteCombo.setValue(c.getUnite());
        dateRecoltePicker.setValue(c.getDateRecolte().toLocalDate());
        dateUtilisationPicker.setValue(c.getDateUtilisation().toLocalDate());
        setFieldsDisabled(false);
        saveBtn.setText("Mettre à jour");
        saveBtn.setGraphic(new FontIcon("fas-save"));
        selectedConsommation = c;
    }

    private void deleteConsommation(ConsommationProduit c) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer cette consommation ?");
        alert.setContentText(String.format("Quantité: %.2f %s | Surface: %.2f m²\n\nAction irréversible !",
                c.getQuantiteUtilisee(), c.getUnite(), c.getSurface()));

        ButtonType confirm = new ButtonType("Confirmer", ButtonType.OK.getButtonData());
        ButtonType cancel = new ButtonType("Annuler", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(confirm, cancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == confirm) {
                try {
                    service.supprimer(c.getidProduit());  // CORRECT : getidProduit()
                    loadConsommations();
                    showSuccess("Consommation supprimée");
                    if (selectedConsommation != null && selectedConsommation.getidProduit() == c.getidProduit()) {
                        clearForm();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Suppression impossible");
                }
            }
        });
    }

    private void setFieldsDisabled(boolean disabled) {
        surfaceField.setDisable(disabled);
        quantiteField.setDisable(disabled);
        uniteCombo.setDisable(disabled);
        dateRecoltePicker.setDisable(disabled);
        dateUtilisationPicker.setDisable(disabled);
        notesArea.setDisable(disabled);
    }

    @FXML
    private void closePopup() {
        if (!surfaceField.getText().isEmpty() || !quantiteField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Fermer ?");
            alert.setContentText("Des données non sauvegardées. Fermer quand même ?");
            alert.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    ((Stage) closeBtn.getScene().getWindow()).close();
                }
            });
        } else {
            ((Stage) closeBtn.getScene().getWindow()).close();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        FontIcon icon = new FontIcon("fas-exclamation-circle");
        icon.setIconSize(48);
        icon.setIconColor(javafx.scene.paint.Color.valueOf("#e74c3c"));
        alert.setGraphic(icon);
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        FontIcon icon = new FontIcon("fas-check-circle");
        icon.setIconSize(48);
        icon.setIconColor(javafx.scene.paint.Color.valueOf("#2ecc71"));
        alert.setGraphic(icon);
        alert.showAndWait();
    }
}