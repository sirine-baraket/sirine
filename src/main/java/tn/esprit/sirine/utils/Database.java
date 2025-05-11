package tn.esprit.sirine.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {


    private static final String URL = "jdbc:mysql://localhost:3306/publication_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
