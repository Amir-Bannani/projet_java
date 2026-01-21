package com.pharmacy.ui;

import com.pharmacy.dao.ClientDAO;
import com.pharmacy.model.Client;

import javax.swing.*;
import java.awt.*;

public class ClientDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    
    private boolean succeeded = false;
    private Client client;

    public ClientDialog(Frame parent, Client clientToEdit) {
        super(parent, clientToEdit == null ? "Add New Client" : "Edit Client", true);
        this.client = clientToEdit;

        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

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

        if (client != null) {
            fillFields();
        }
    }

    private void fillFields() {
        nameField.setText(client.getName());
        phoneField.setText(client.getPhone());
        emailField.setText(client.getEmail());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        ClientDAO dao = new ClientDAO();
        if (client == null) {
            // Add
            Client newClient = new Client(name, phone, email);
            dao.addClient(newClient);
        } else {
            // Update
            client.setName(name);
            client.setPhone(phone);
            client.setEmail(email);
            dao.updateClient(client);
        }

        succeeded = true;
        dispose();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
