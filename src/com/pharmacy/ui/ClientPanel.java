package com.pharmacy.ui;

import com.pharmacy.dao.ClientDAO;
import com.pharmacy.model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ClientDAO clientDAO;

    public ClientPanel() {
        clientDAO = new ClientDAO();
        setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Client");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Phone", "Email", "Total Purchases"};
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
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Client> list = clientDAO.getAllClients();
        for (Client c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail(), c.getTotalPurchases()});
        }
    }

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        ClientDialog dialog = new ClientDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadData();
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to edit.");
            return;
        }

        // Ideally fetch by ID again, but for now using table data for simplicity or construct temporary object
        // Better pattern: fetch from DAO or list. Here we reconstruct for simplicity since table has all data needed for now except full object
        // Actually let's fetch strictly correct approach if ClientDAO had getClientById.
        // For now, I'll reconstruct from table model to save a DAO call, as all editable fields are there.
        // Wait, Client object needs ID for update.
        
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String phone = (String) tableModel.getValueAt(row, 2);
        String email = (String) tableModel.getValueAt(row, 3);
        double total = (double) tableModel.getValueAt(row, 4);
        
        Client c = new Client(id, name, phone, email, total);
        
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        ClientDialog dialog = new ClientDialog(parent, c);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadData();
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this client?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            clientDAO.deleteClient(id);
            loadData();
        }
    }
}
