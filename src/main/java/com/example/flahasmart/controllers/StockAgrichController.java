package com.example.flahasmart.controllers;

import com.example.flahasmart.entities.StockProduit;
import com.example.flahasmart.services.StockProduitService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class  StockAgrichController{

    @FXML private TextField typeField;
    @FXML private TextField varieteField;
    @FXML private ComboBox<String> statutBox;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Label errorLabel;
    @FXML private FlowPane container;

    private final StockProduitService service = new StockProduitService();
    private StockProduit selectedProduit = null;
    @FXML
    public void initialize() {

        statutBox.getItems().addAll("en cours","terminé" ,"en croissance");
        statutBox.setValue("en cours");

        // Empêcher dates passées
        dateDebutPicker.setDayCellFactory(dp -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });

        loadProduits();
    }
    private boolean validateFields(){

        errorLabel.setText("");
        resetStyles();
        boolean valid = true;

        String type = typeField.getText().trim();
        String variete = varieteField.getText().trim();

        if(type.length() < 3){
            markError(typeField,"Type min 3 caractères");
            valid = false;
        }
        else if(!type.matches("[a-zA-ZÀ-ÿ ]+")){
            markError(typeField,"Type invalide (lettres uniquement)");
            valid = false;
        }

        if(variete.length() < 2){
            markError(varieteField,"Variété trop courte");
            valid = false;
        }
        else if(!variete.matches("[a-zA-ZÀ-ÿ0-9 ]+")){
            markError(varieteField,"Variété invalide");
            valid = false;
        }

        if(dateDebutPicker.getValue()==null){
            markError(dateDebutPicker,"Date début obligatoire");
            valid = false;
        }
        else if(dateDebutPicker.getValue().isBefore(LocalDate.now())){
            markError(dateDebutPicker,"Date doit être aujourd'hui ou future");
            valid = false;
        }

        if(dateFinPicker.getValue()!=null && dateDebutPicker.getValue()!=null){

            if(dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())){
                markError(dateFinPicker,"Date fin > date début");
                valid = false;
            }

            Period period = Period.between(dateDebutPicker.getValue(), dateFinPicker.getValue());
            int totalDays = period.getYears()*365 + period.getMonths()*30 + period.getDays();

            if(totalDays > 365){
                markError(dateFinPicker,"Durée max = 365 jours");
                valid = false;
            }
        }

        return valid;
    }

    @FXML
    public void add(){

        try{

            if(!validateFields()) return;

            if(selectedProduit == null){
                StockProduit s = new StockProduit();
                fillProduitFromForm(s);
                service.ajouter(s);
                errorLabel.setText("✔ Produit ajouté");
            }
            else{
                fillProduitFromForm(selectedProduit);
                service.modifier(selectedProduit);
                errorLabel.setText("✔ Produit modifié");
                selectedProduit = null;
            }

            clearForm();
            loadProduits();

        }catch(Exception e){
            errorLabel.setText("Erreur : "+e.getMessage());
        }
    }

    public void deleteProduit(int id){

        try{
            service.supprimer(id);
            loadProduits();
        }catch(Exception e){
            errorLabel.setText("Suppression impossible");
        }
    }

    public void editProduit(StockProduit p){

        selectedProduit = p;

        typeField.setText(p.getTypeProduit());
        varieteField.setText(p.getVariete());
        statutBox.setValue(p.getStatut());
        dateDebutPicker.setValue(p.getDateDebut());
        dateFinPicker.setValue(p.getDateFinEstimee());

        errorLabel.setText("Mode modification actif");
    }

    public void loadProduits(){

        try{
            container.getChildren().clear();

            List<StockProduit> list = service.afficher();




            for(StockProduit p : list){

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/flahasmart/CardStockAgri.fxml")
                );

                Parent card = loader.load();
                CardStockAchriController controller = loader.getController();
                controller.setData(p,this);

                container.getChildren().add(card);
            }

        }catch(Exception e){
            errorLabel.setText("Erreur chargement");
            e.printStackTrace();
        }
    }

    private void fillProduitFromForm(StockProduit s){

        s.setTypeProduit(typeField.getText());
        s.setVariete(varieteField.getText());
        s.setDateDebut(dateDebutPicker.getValue());
        s.setDateFinEstimee(dateFinPicker.getValue());
        s.setIdUser(1);
        if(dateFinPicker.getValue()!=null && dateFinPicker.getValue().isBefore(LocalDate.now()))
            s.setStatut("terminé");
        else
            s.setStatut("en cours");
    }

    private void clearForm(){
        typeField.clear();
        varieteField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        statutBox.setValue("en cours");
    }

    private void markError(Control field,String message){
        field.setStyle("-fx-border-color:red;-fx-border-width:2;");
        errorLabel.setText(message);
    }

    private void resetStyles(){
        typeField.setStyle("");
        varieteField.setStyle("");
        dateDebutPicker.setStyle("");
        dateFinPicker.setStyle("");
    }
}
