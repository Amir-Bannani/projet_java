package com.pharmacy.dao;

import com.pharmacy.config.DBConnection;
import com.pharmacy.model.Order;
import com.pharmacy.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean createOrder(Order order) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;

        try {
            conn.setAutoCommit(false);

            // 1. Insert Order (Status PENDING)
            String orderSql = "INSERT INTO orders (supplier_id, order_date, total_amount, status) VALUES (?, NOW(), ?, 'PENDING')";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, order.getSupplierId());
            orderStmt.setDouble(2, order.getTotalAmount());
            
            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating order failed.");

            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // 2. Insert Items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_cost) VALUES (?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);

            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, order.getId());
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitCost());
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("order_id"));
                o.setSupplierId(rs.getInt("supplier_id"));
                o.setOrderDate(rs.getTimestamp("order_date"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean markOrderReceived(int orderId) {
        Connection conn = DBConnection.getConnection();
        ProductDAO productDAO = new ProductDAO();
        try {
            conn.setAutoCommit(false);

            // 1. Validations: Check if already received
            String checkSql = "SELECT status FROM orders WHERE order_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, orderId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    if (!"PENDING".equals(rs.getString("status"))) {
                        conn.rollback();
                        return false; // Already received or cancelled
                    }
                } else {
                    conn.rollback(); 
                    return false; // Order not found
                }
            }

            // 2. Fetch Items
            List<OrderItem> items = new ArrayList<>();
            String itemSql = "SELECT * FROM order_items WHERE order_id = ?";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                itemStmt.setInt(1, orderId);
                ResultSet rs = itemStmt.executeQuery();
                while (rs.next()) {
                    items.add(new OrderItem(
                        rs.getInt("order_item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_cost")
                    ));
                }
            }

            // 3. Update Stock for each item
            for (OrderItem item : items) {
                // POSITIVE quantity adds to stock
                productDAO.updateStock(conn, item.getProductId(), item.getQuantity());
            }

            // 4. Update Status
            String updateSql = "UPDATE orders SET status = 'RECEIVED' WHERE order_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, orderId);
                updateStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean deleteOrder(int orderId) {
         // Can only delete PENDING orders
         String sql = "DELETE FROM orders WHERE order_id = ? AND status = 'PENDING'";
         try (Connection conn = DBConnection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, orderId);
             int rows = stmt.executeUpdate();
             return rows > 0;
         } catch (SQLException e) {
             e.printStackTrace();
             return false;
         }
    }
    public List<OrderItem> getItemsByOrderId(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        // JOIN to get product Name
        String sql = "SELECT oi.*, p.name as p_name FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_cost")
                );
                item.setProductName(rs.getString("p_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
