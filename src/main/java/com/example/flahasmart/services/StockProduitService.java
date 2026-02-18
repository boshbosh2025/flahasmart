package com.example.flahasmart.services;

import com.example.flahasmart.entities.StockProduit;
import com.example.flahasmart.utils.MyDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockProduitService {

    private final Connection cnx = MyDB.getInstance();

    public void ajouter(StockProduit s) throws SQLException {

        String sql = " INSERT INTO stock_produit (type_produit,variete,date_debut,date_fin_estimee,statut,id_user) VALUES (?,?,?,?,?,?) ";
        PreparedStatement ps = cnx.prepareStatement(sql);

        ps.setString(1, s.getTypeProduit());
        ps.setString(2, s.getVariete());

        // date debut obligatoire
        ps.setDate(3, Date.valueOf(s.getDateDebut()));

        // date fin nullable
        if (s.getDateFinEstimee() != null)
            ps.setDate(4, Date.valueOf(s.getDateFinEstimee()));
        else
            ps.setNull(4, Types.DATE);

        ps.setString(5, s.getStatut());
        ps.setInt(6, 1);

        ps.executeUpdate();
    }

    /* ================= READ ================= */
    public List<StockProduit> afficher() throws SQLException {

        List<StockProduit> list = new ArrayList<>();

        String sql = "SELECT * FROM stock_produit ORDER BY id_produit DESC";
        ResultSet rs = cnx.createStatement().executeQuery(sql);

        while (rs.next()) {

            LocalDate fin = null;
            Date sqlDateFin = rs.getDate("date_fin_estimee");
            if (sqlDateFin != null)
                fin = sqlDateFin.toLocalDate();

            list.add(new StockProduit(
                    rs.getInt("id_produit"),
                    rs.getString("type_produit"),
                    rs.getString("variete"),
                    rs.getDate("date_debut").toLocalDate(),
                    fin,
                    rs.getString("statut"),
                    rs.getInt("id_user")
            ));
        }

        return list;
    }

    /* ================= DELETE ================= */
    public void supprimer(int id) throws SQLException {
        PreparedStatement ps = cnx.prepareStatement(
                "DELETE FROM stock_produit WHERE id_produit=?"
        );
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    /* ================= UPDATE ================= */
    public void modifier(StockProduit s) throws SQLException {

        String sql = "UPDATE stock_produit SET type_produit=?,variete=?,date_debut=?,date_fin_estimee=?, statut=?, id_user=? WHERE id_produit=? ";

        PreparedStatement ps = cnx.prepareStatement(sql);

        ps.setString(1, s.getTypeProduit());
        ps.setString(2, s.getVariete());
        ps.setDate(3, Date.valueOf(s.getDateDebut()));

        if (s.getDateFinEstimee() != null)
            ps.setDate(4, Date.valueOf(s.getDateFinEstimee()));
        else
            ps.setNull(4, Types.DATE);

        ps.setString(5, s.getStatut());
        ps.setInt(6, s.getIdUser());
        ps.setInt(7, s.getIdProduit());

        ps.executeUpdate();
    }
}
