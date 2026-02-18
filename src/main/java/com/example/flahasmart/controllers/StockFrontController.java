package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.StockProduit;
import com.example.flahasmart.services.StockProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StockFrontController {

    @FXML private FlowPane container;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterBox;
    @FXML private ComboBox<String> sortBox;
    @FXML private Label activeCountLabel;
    @FXML private Label growthCountLabel;
    @FXML private Label doneCountLabel;

    private final StockProduitService service = new StockProduitService();
    private List<StockProduit> data = new ArrayList<>();

    @FXML
    public void initialize() {
        filterBox.getItems().addAll("Tous", "en cours", "en croissance", "terminé");
        filterBox.setValue("Tous");

        sortBox.getItems().addAll("A-Z", "Z-A", "Date récente", "Date ancienne");
        sortBox.setValue("A-Z");

        load();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> refresh());
        filterBox.setOnAction(e -> refresh());
        sortBox.setOnAction(e -> refresh());
    }

    private void load() {
        try {
            data = service.afficher();
            updateStats();
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStats() {
        long active = data.stream()
                .filter(p -> {
                    String s = p.getStatut();
                    return s != null && (s.equalsIgnoreCase("en cours") || s.equalsIgnoreCase("en croissance"));
                })
                .count();

        long growth = data.stream()
                .filter(p -> p.getStatut().equalsIgnoreCase("en croissance"))
                .count();

        long done = data.stream()
                .filter(p -> p.getStatut().equalsIgnoreCase("terminé"))
                .count();

        activeCountLabel.setText(String.valueOf(active));
        growthCountLabel.setText(String.valueOf(growth));
        doneCountLabel.setText(String.valueOf(done));
    }

    private void refresh() {
        container.getChildren().clear();

        Stream<StockProduit> stream = data.stream();

        String filter = filterBox.getValue();
        if (filter != null && !filter.equals("Tous")) {
            stream = stream.filter(p -> p.getStatut().equalsIgnoreCase(filter));
        }

        String keyword = searchField.getText();
        if (keyword != null && !keyword.isEmpty()) {
            String lower = keyword.toLowerCase();
            stream = stream.filter(p ->
                    p.getTypeProduit().toLowerCase().contains(lower) ||
                            p.getVariete().toLowerCase().contains(lower)
            );
        }

        List<StockProduit> list = stream.collect(Collectors.toList());

        String sort = sortBox.getValue();
        if (sort != null) {
            switch (sort) {
                case "A-Z":
                    list.sort(Comparator.comparing(StockProduit::getTypeProduit));
                    break;
                case "Z-A":
                    list.sort(Comparator.comparing(StockProduit::getTypeProduit).reversed());
                    break;
                case "Date récente":
                    list.sort(Comparator.comparing(StockProduit::getDateDebut).reversed());
                    break;
                case "Date ancienne":
                    list.sort(Comparator.comparing(StockProduit::getDateDebut));
                    break;
            }
        }

            try {
                for (StockProduit p : list) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/example/flahasmart/CardStockFront.fxml") // <- chemin correct
                    );
                    Parent card = loader.load();
                    CardStockFrontController controller = loader.getController();  // <- changer ici
                    controller.setData(p);
                    container.getChildren().add(card);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }}


}