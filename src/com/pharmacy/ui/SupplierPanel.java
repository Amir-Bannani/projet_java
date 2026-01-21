package com.pharmacy.ui;

import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDAO supplierDAO;

    public SupplierPanel() {
        supplierDAO = new SupplierDAO();
        setLayout(new BorderLayout());

        // Toolbar
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

        // Table
        String[] columns = {"ID", "Name", "Contact Info", "Address"};
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
        List<Supplier> list = supplierDAO.getAllSuppliers();
        for (Supplier s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo(), s.getAddress()});
        }
    }

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        SupplierDialog dialog = new SupplierDialog(parent, null);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadData();
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Supplier s = supplierDAO.getSupplierById(id);
        
        if (s != null) {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            SupplierDialog dialog = new SupplierDialog(parent, s);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                loadData();
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this supplier? This may affect products linked to it.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            supplierDAO.deleteSupplier(id);
            loadData();
        }
    }
}
