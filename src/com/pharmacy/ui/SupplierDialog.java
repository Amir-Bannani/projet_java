package com.pharmacy.ui;

import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Supplier;

import javax.swing.*;
import java.awt.*;

public class SupplierDialog extends JDialog {
    private JTextField nameField;
    private JTextField contactField;
    private JTextField addressField;
    
    private boolean succeeded = false;
    private Supplier supplier; // null for add, non-null for edit

    public SupplierDialog(Frame parent, Supplier supplierToEdit) {
        super(parent, supplierToEdit == null ? "Add New Supplier" : "Edit Supplier", true);
        this.supplier = supplierToEdit;

        setSize(350, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

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

        if (supplier != null) {
            fillFields();
        }
    }

    private void fillFields() {
        nameField.setText(supplier.getName());
        contactField.setText(supplier.getContactInfo());
        addressField.setText(supplier.getAddress());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        SupplierDAO dao = new SupplierDAO();
        if (supplier == null) {
            // Add
            Supplier newS = new Supplier(0, name, contact, address);
            dao.addSupplier(newS);
        } else {
            // Update
            supplier.setName(name);
            supplier.setContactInfo(contact);
            supplier.setAddress(address);
            dao.updateSupplier(supplier);
        }

        succeeded = true;
        dispose();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
