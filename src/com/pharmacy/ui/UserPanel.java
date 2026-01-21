package com.pharmacy.ui;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    public UserPanel() {
        userDAO = new UserDAO();
        setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add New User");
        JButton deleteBtn = new JButton("Delete Selected");

        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        toolbar.add(deleteBtn);
        add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Listeners
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> onAdd());
        deleteBtn.addActionListener(e -> onDelete());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<User> list = userDAO.getAllUsers();
        for (User u : list) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
        }
    }

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        UserDialog dialog = new UserDialog(parent);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadData();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        
        // Prevent deleting yourself (optional safety check, but checking role/id is harder without context of current user here. 
        // We'll trust the admin for now or they can delete themselves if they want.)
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userDAO.deleteUser(id);
            loadData();
        }
    }
}
