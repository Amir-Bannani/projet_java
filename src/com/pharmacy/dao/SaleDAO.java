package com.pharmacy.dao;

import com.pharmacy.config.DBConnection;
import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;

import java.sql.*;

public class SaleDAO {

    public boolean createSale(Sale sale) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement saleStmt = null;
        PreparedStatement itemStmt = null;
        ProductDAO productDAO = new ProductDAO();

        try {
            // 1. Disable Auto-Commit for Transaction
            conn.setAutoCommit(false);

            // 2. Insert Sale Record
            String saleSql = "INSERT INTO sales (client_id, user_id, total_amount) VALUES (?, ?, ?)";
            saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            
            if (sale.getClientId() > 0) {
                saleStmt.setInt(1, sale.getClientId());
            } else {
                saleStmt.setNull(1, Types.INTEGER);
            }
            saleStmt.setInt(2, sale.getUserId());
            saleStmt.setDouble(3, sale.getTotalAmount());
            
            int affectedRows = saleStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }

            // Get generated Sale ID
            int saleId;
            try (ResultSet generatedKeys = saleStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    saleId = generatedKeys.getInt(1);
                    sale.setId(saleId);
                } else {
                    throw new SQLException("Creating sale failed, no ID obtained.");
                }
            }

            // 3. Insert Sale Items & Update Stock
            String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);

            for (SaleItem item : sale.getItems()) {
                // Add to batch
                itemStmt.setInt(1, saleId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.addBatch();

                // Update Stock (Decrease) using the SAME connection
                productDAO.updateStock(conn, item.getProductId(), -item.getQuantity());
            }

            // Execute Batch
            itemStmt.executeBatch();

            // 4. Commit Transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            // Restore Auto-Commit and close resources
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (saleStmt != null) saleStmt.close();
                if (itemStmt != null) itemStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public java.util.List<Sale> getAllSales() {
        java.util.List<Sale> list = new java.util.ArrayList<>();
        String sql = "SELECT s.*, c.name as client_name FROM sales s " +
                     "LEFT JOIN clients c ON s.client_id = c.client_id " +
                     "ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Sale s = new Sale(
                    rs.getInt("sale_id"),
                    rs.getInt("client_id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("sale_date"),
                    rs.getDouble("total_amount")
                );
                // Set the transient client name
                String cName = rs.getString("client_name");
                s.setClientName(cName != null ? cName : "Walk-in");
                
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public java.util.List<SaleItem> getItemsBySaleId(int saleId) {
        java.util.List<SaleItem> list = new java.util.ArrayList<>();
        String sql = "SELECT si.*, p.name as p_name FROM sale_items si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "WHERE si.sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SaleItem item = new SaleItem(
                    rs.getInt("sale_item_id"),
                    rs.getInt("sale_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price")
                );
                item.setProductName(rs.getString("p_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public java.util.List<Sale> getSalesByClientId(int clientId) {
        java.util.List<Sale> list = new java.util.ArrayList<>();
        String sql = "SELECT s.*, c.name as client_name FROM sales s " +
                     "LEFT JOIN clients c ON s.client_id = c.client_id " +
                     "WHERE s.client_id = ? " +
                     "ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale s = new Sale(
                    rs.getInt("sale_id"),
                    rs.getInt("client_id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("sale_date"),
                    rs.getDouble("total_amount")
                );
                String cName = rs.getString("client_name");
                s.setClientName(cName != null ? cName : "Walk-in");
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM sales";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
