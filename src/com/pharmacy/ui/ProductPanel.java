package com.pharmacy.ui;

import com.pharmacy.dao.ProductDAO;
import com.pharmacy.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;

    public ProductPanel() {
        productDAO = new ProductDAO();
        setLayout(new BorderLayout());

        // 1. Toolbar containing Buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add New");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        add(toolbar, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {"ID", "Name", "Desc", "Price", "Stock", "Min", "SupplierID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Action Listeners
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());

        // Initial Load
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0); // Clear existing data
        List<Product> list = productDAO.getAllProducts();
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getMinThreshold(),
                p.getSupplierId()
            });
        }
    }

    private void onAdd() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ProductDialog dialog = new ProductDialog(parentFrame, null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadData();
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            return;
        }

        // Reconstruct Product object from row data
        // Note: Faster to just get by ID if we trusted the ID column, or store Objects in table
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Product p = productDAO.getProductById(id);
        
        if (p != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ProductDialog dialog = new ProductDialog(parentFrame, p);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            productDAO.deleteProduct(id);
            loadData();
        }
    }
}
