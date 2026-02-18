package com.example.flahasmart.entities;

import java.time.LocalDate;

public class StockProduit {

    private int idProduit;
    private String typeProduit;
    private String variete;
    private LocalDate dateDebut;
    private LocalDate dateFinEstimee;
    private String statut;
    private int idUser;

    public StockProduit() {}

    public StockProduit(int idProduit, String typeProduit, String variete,
                        LocalDate dateDebut, LocalDate dateFinEstimee,
                        String statut, int idUser) {
        this.idProduit = idProduit;
        this.typeProduit = typeProduit;
        this.variete = variete;
        this.dateDebut = dateDebut;
        this.dateFinEstimee = dateFinEstimee;
        this.statut = statut;
        this.idUser = idUser;
    }

    // getters setters
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public String getTypeProduit() { return typeProduit; }
    public void setTypeProduit(String typeProduit) { this.typeProduit = typeProduit; }

    public String getVariete() { return variete; }
    public void setVariete(String variete) { this.variete = variete; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFinEstimee() { return dateFinEstimee; }
    public void setDateFinEstimee(LocalDate dateFinEstimee) { this.dateFinEstimee = dateFinEstimee; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
}
