package com.example.flahasmart.entities;

import java.sql.Date;

public class ConsommationProduit {

    private int idProduit;
    private int idStockProduit; // FK vers stock_produit

    private double surface;
    private double quantiteUtilisee;
    private String unite;

    private Date dateRecolte;
    private Date dateUtilisation;

    public ConsommationProduit() {}

    public ConsommationProduit(int idProduit, int idStockProduit, double surface,
                               double quantiteUtilisee, String unite,
                               Date dateRecolte, Date dateUtilisation) {
        this.idProduit = idProduit;
        this.idStockProduit = idStockProduit;
        this.surface = surface;
        this.quantiteUtilisee = quantiteUtilisee;
        this.unite = unite;
        this.dateRecolte = dateRecolte;
        this.dateUtilisation = dateUtilisation;
    }

    public int getidProduit() { return idProduit; }
    public void setidProduit(int idProduit) { this.idProduit = idProduit; }

    public int getIdStockProduit() { return idStockProduit; }
    public void setIdStockProduit(int idStockProduit) { this.idStockProduit = idStockProduit; }

    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }

    public double getQuantiteUtilisee() { return quantiteUtilisee; }
    public void setQuantiteUtilisee(double quantiteUtilisee) { this.quantiteUtilisee = quantiteUtilisee; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public Date getDateRecolte() { return dateRecolte; }
    public void setDateRecolte(Date dateRecolte) { this.dateRecolte = dateRecolte; }

    public Date getDateUtilisation() { return dateUtilisation; }
    public void setDateUtilisation(Date dateUtilisation) { this.dateUtilisation = dateUtilisation; }
}
