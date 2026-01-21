package com.pharmacy.dao;

import com.pharmacy.config.DBConnection;
import com.pharmacy.model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Client(
                    rs.getInt("client_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getDouble("total_purchases")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addClient(Client client) {
        String sql = "INSERT INTO clients (name, phone, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getPhone());
            stmt.setString(3, client.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClient(Client client) {
        String sql = "UPDATE clients SET name=?, phone=?, email=? WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getPhone());
            stmt.setString(3, client.getEmail());
            stmt.setInt(4, client.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClient(int id) {
        String sql = "DELETE FROM clients WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
