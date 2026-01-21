package com.pharmacy.ui;

import com.pharmacy.dao.ClientDAO;
import com.pharmacy.dao.ProductDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Client;
import com.pharmacy.model.Product;
import com.pharmacy.model.Sale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel {

    private ProductDAO productDAO = new ProductDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private ClientDAO clientDAO = new ClientDAO();

    public ReportsPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Stock Alerts", createStockPanel());
        tabbedPane.addTab("Financials & Suppliers", createFinancialsPanel());
        tabbedPane.addTab("Client History", createClientHistoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Low Stock Table
        String[] columns = {"ID", "Name", "Stock", "Min Threshold", "Supplier ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        List<Product> lowStock = productDAO.getLowStockProducts();
        for (Product p : lowStock) {
            model.addRow(new Object[]{
                p.getId(), p.getName(), p.getStockQuantity(), p.getMinThreshold(), p.getSupplierId()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Low Stock Alerts"));
        
        // Refresh Button
        JButton refreshBtn = new JButton("Refresh Alerts");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Product p : productDAO.getLowStockProducts()) {
                model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getStockQuantity(), p.getMinThreshold(), p.getSupplierId()
                });
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFinancialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // --- Top: Turnover ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        double revenue = saleDAO.getTotalRevenue();
        JLabel revenueLabel = new JLabel("Total Turnover (CA): $" + String.format("%.2f", revenue));
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(revenueLabel);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
             double r = saleDAO.getTotalRevenue();
             revenueLabel.setText("Total Turnover (CA): $" + String.format("%.2f", r));
             // Also refresh table below... but for simplicity handling simpler here
        });
        topPanel.add(refreshBtn);
        
        // --- Center: Supplier Performance ---
        String[] columns = {"Supplier Name", "Orders Count", "Total Spent"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        List<Map<String, Object>> performance = supplierDAO.getSupplierPerformance();
        for (Map<String, Object> row : performance) {
            model.addRow(new Object[]{
                row.get("name"), row.get("order_count"), row.get("total_spent")
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Supplier Performance"));
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createClientHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top: Selection
        JPanel topPanel = new JPanel();
        JComboBox<ClientItem> clientCombo = new JComboBox<>();
        List<Client> clients = clientDAO.getAllClients();
        for (Client c : clients) {
            clientCombo.addItem(new ClientItem(c));
        }
        
        JButton viewBtn = new JButton("View History");
        topPanel.add(new JLabel("Select Client:"));
        topPanel.add(clientCombo);
        topPanel.add(viewBtn);
        
        // Center: Sales Table
        String[] columns = {"Sale ID", "Date", "Total Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        viewBtn.addActionListener(e -> {
            ClientItem selected = (ClientItem) clientCombo.getSelectedItem();
            if (selected != null) {
                model.setRowCount(0);
                List<Sale> sales = saleDAO.getSalesByClientId(selected.client.getId());
                for (Sale s : sales) {
                    model.addRow(new Object[]{
                        s.getId(), s.getSaleDate(), s.getTotalAmount()
                    });
                }
            }
        });
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }

    // Helper class for ComboBox
    private static class ClientItem {
        Client client;
        public ClientItem(Client c) { this.client = c; }
        @Override
        public String toString() { return client.getName() + " (ID: " + client.getId() + ")"; }
    }
}
