package com.pharmacy.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database URL, Username, and Password
    // ADJUST THESE IF YOUR MYSQL CONFIGURATION IS DIFFERENT
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacie_db";
    
    private static final String USER = "root"; 
    private static final String PASSWORD = "amir"; // Default XAMPP/WAMP password is empty. Change if yours is different.

    private static Connection connection = null;

    // Private constructor to enforce Singleton pattern
    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    // Load MySQL JDBC Driver
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Connection successful!");
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL JDBC Driver not found. Add the library to your classpath.");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.err.println("Connection failed. Check your URL, User, and Password.");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    // Optional: Method to close connection explicitly if needed
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
