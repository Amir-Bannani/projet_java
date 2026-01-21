package com.pharmacy.ui;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.model.User;

import javax.swing.*;
import java.awt.*;

public class UserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    
    private boolean succeeded = false;

    public UserDialog(Frame parent) {
        super(parent, "Add New User", true);
        
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        String[] roles = {"EMPLOYEE", "ADMIN"};
        roleCombo = new JComboBox<>(roles);
        formPanel.add(roleCombo);

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
    }

    private void onSave() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        UserDAO dao = new UserDAO();
        // ID 0 because it's auto-increment
        User newUser = new User(0, username, password, role);
        
        if (dao.register(newUser)) {
            succeeded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user. Name might be taken.");
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
