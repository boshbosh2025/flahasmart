package com.example.flahasmart.services;

import com.example.flahasmart.entities.ConsommationProduit;
import com.example.flahasmart.utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsommationProduitService {

    private final Connection cnx = MyDB.getInstance();

    public void ajouter(ConsommationProduit c) throws SQLException {

        String sql = "INSERT INTO consommation_produit (id_stock_produit, surface, quantite_utilisee, unite, date_recolte, date_utilisation) VALUES (?,?,?,?,?,?)";

        PreparedStatement ps = cnx.prepareStatement(sql);

        ps.setInt(1, c.getIdStockProduit());
        ps.setDouble(2, c.getSurface());
        ps.setDouble(3, c.getQuantiteUtilisee());
        ps.setString(4, c.getUnite());
        ps.setDate(5, c.getDateRecolte());
        ps.setDate(6, c.getDateUtilisation());

        ps.executeUpdate();
    }

    public List<ConsommationProduit> afficher() throws SQLException {

        List<ConsommationProduit> list = new ArrayList<>();

        String sql = "SELECT * FROM consommation_produit";
        ResultSet rs = cnx.createStatement().executeQuery(sql);

        while (rs.next()) {
            list.add(new ConsommationProduit(
                    rs.getInt("id_produit"),
                    rs.getInt("id_stock_produit"),
                    rs.getDouble("surface"),
                    rs.getDouble("quantite_utilisee"),
                    rs.getString("unite"),
                    rs.getDate("date_recolte"),
                    rs.getDate("date_utilisation")
            ));
        }
        return list;
    }

    public void supprimer(int id) throws SQLException {

        PreparedStatement ps = cnx.prepareStatement(
                "DELETE FROM consommation_produit WHERE id_produit=?"
        );
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void modifier(ConsommationProduit c) throws SQLException {

        String sql = "UPDATE consommation_produit SET   id_stock_produit=?,surface=?, quantite_utilisee=?, unite=?,date_recolte=?, date_utilisation=? WHERE id_produit=?";

        PreparedStatement ps = cnx.prepareStatement(sql);

        ps.setInt(1, c.getIdStockProduit());
        ps.setDouble(2, c.getSurface());
        ps.setDouble(3, c.getQuantiteUtilisee());
        ps.setString(4, c.getUnite());
        ps.setDate(5, c.getDateRecolte());
        ps.setDate(6, c.getDateUtilisation());
        ps.setInt(7, c.getidProduit());

        ps.executeUpdate();
    }
}
