package com.pharmacy.dao;

import com.pharmacy.config.DBConnection;
import com.pharmacy.model.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void addSupplier(Supplier s) {
        String sql = "INSERT INTO suppliers (name, contact_info, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getContactInfo());
            stmt.setString(3, s.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier s) {
        String sql = "UPDATE suppliers SET name=?, contact_info=?, address=? WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getContactInfo());
            stmt.setString(3, s.getAddress());
            stmt.setInt(4, s.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Supplier getSupplierById(int id) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.util.List<java.util.Map<String, Object>> getSupplierPerformance() {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT s.name, COUNT(o.order_id) as order_count, SUM(o.total_amount) as total_spent " +
                     "FROM suppliers s " +
                     "LEFT JOIN orders o ON s.supplier_id = o.supplier_id " +
                     "GROUP BY s.supplier_id, s.name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("name", rs.getString("name"));
                map.put("order_count", rs.getInt("order_count"));
                map.put("total_spent", rs.getDouble("total_spent"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
