package com.pharmacy.dao;

import com.pharmacy.config.DBConnection;
import com.pharmacy.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("min_threshold"),
                    rs.getInt("supplier_id")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void addProduct(Product p) {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, min_threshold, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStockQuantity());
            stmt.setInt(5, p.getMinThreshold());
            if (p.getSupplierId() > 0) stmt.setInt(6, p.getSupplierId());
            else stmt.setNull(6, Types.INTEGER);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateProduct(Product p) {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock_quantity=?, min_threshold=?, supplier_id=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getPrice());
            stmt.setInt(4, p.getStockQuantity());
            stmt.setInt(5, p.getMinThreshold());
            stmt.setInt(6, p.getSupplierId());
            stmt.setInt(7, p.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Critical for transactions
    public void updateStock(int productId, int delta) throws SQLException {
         // Default behavior: get new connection, auto-close
         try (Connection conn = DBConnection.getConnection()) {
             updateStock(conn, productId, delta);
         }
    }

    // Transactional version: uses existing connection, does NOT close it
    public void updateStock(Connection conn, int productId, int delta) throws SQLException {
         String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, delta);
             stmt.setInt(2, productId);
             stmt.executeUpdate();
         }
    }
    
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("min_threshold"),
                    rs.getInt("supplier_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock_quantity <= min_threshold";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getInt("min_threshold"),
                    rs.getInt("supplier_id")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
