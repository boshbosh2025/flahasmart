package com.example.flahasmart.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class MyDB {

    private static Connection connection;

    public static Connection getInstance() {

        try {

            if(connection == null || connection.isClosed()) {

                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/flahasmart?useSSL=false&serverTimezone=UTC",
                        "root",
                        ""
                );

                System.out.println("Connexion DB OK");
            }

        } catch (Exception e) {
            System.out.println("Erreur DB : " + e.getMessage());
        }

        return connection;
    }
}
