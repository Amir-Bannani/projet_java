package com.pharmacy.ui;

import com.pharmacy.dao.ProductDAO;
import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Product;
import com.pharmacy.model.Supplier;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descArea;
    private JTextField priceField;
    private JTextField stockField;
    private JTextField thresholdField;
    private JComboBox<Supplier> supplierCombo;
    
    private boolean succeeded = false;
    private Product product; // If editing, this is the product. If adding, this is null.

    public ProductDialog(Frame parent, Product productToEdit) {
        super(parent, productToEdit == null ? "Add New Product" : "Edit Product", true);
        this.product = productToEdit;
        
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rows
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descArea));

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stock Quantity:"));
        stockField = new JTextField();
        formPanel.add(stockField);
        
        formPanel.add(new JLabel("Min Threshold:"));
        thresholdField = new JTextField("5");
        formPanel.add(thresholdField);

        formPanel.add(new JLabel("Supplier:"));
        supplierCombo = new JComboBox<>();
        loadSuppliers();
        formPanel.add(supplierCombo);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // If editing, fill fields
        if (product != null) {
            fillFields();
        }
    }

    private void loadSuppliers() {
        SupplierDAO dao = new SupplierDAO();
        List<Supplier> suppliers = dao.getAllSuppliers();
        for (Supplier s : suppliers) {
            supplierCombo.addItem(s);
        }
    }

    private void fillFields() {
        nameField.setText(product.getName());
        descArea.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
        thresholdField.setText(String.valueOf(product.getMinThreshold()));
        
        // Select the correct supplier
        for (int i = 0; i < supplierCombo.getItemCount(); i++) {
            Supplier s = supplierCombo.getItemAt(i);
            if (s.getId() == product.getSupplierId()) {
                supplierCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
            
            if (name.isEmpty() || selectedSupplier == null) {
                JOptionPane.showMessageDialog(this, "Name and Supplier are required.");
                return;
            }

            ProductDAO dao = new ProductDAO();
            
            if (product == null) {
                // Add New
                Product newP = new Product(0, name, desc, price, stock, threshold, selectedSupplier.getId());
                dao.addProduct(newP);
            } else {
                // Update
                product.setName(name);
                product.setDescription(desc);
                product.setPrice(price);
                product.setStockQuantity(stock);
                product.setMinThreshold(threshold);
                product.setSupplierId(selectedSupplier.getId());
                dao.updateProduct(product);
            }

            succeeded = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format for Price, Stock, or Threshold.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving product: " + e.getMessage());
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
