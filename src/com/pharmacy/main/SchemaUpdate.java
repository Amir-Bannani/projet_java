package com.pharmacy.main;

import com.pharmacy.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdate {
    public static void main(String[] args) {
        String sql = "ALTER TABLE order_items ADD COLUMN unit_cost DECIMAL(10, 2) DEFAULT 0.00";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Schema updated successfully: unit_cost added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
