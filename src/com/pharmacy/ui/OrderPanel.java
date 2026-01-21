package com.pharmacy.ui;

import com.pharmacy.dao.OrderDAO;
import com.pharmacy.dao.ProductDAO;
import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.model.Order;
import com.pharmacy.model.OrderItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {
    private JTabbedPane innerTabs;
    
    // DAO
    private OrderDAO orderDAO;
    private SupplierDAO supplierDAO;
    private ProductDAO productDAO;

    // --- Tab 1: New Order Components ---
    private JComboBox<Supplier> supplierCombo;
    private JComboBox<Product> productCombo;
    private JTextField quantityField;
    private JTextField costField;
    private DefaultTableModel newOrderCartModel;
    private List<OrderItem> cartItems;
    private double currentTotal = 0.0;
    private JLabel totalLabel;

    // --- Tab 2: History Components ---
    private JTable historyTable;
    private DefaultTableModel historyModel;

    public OrderPanel() {
        orderDAO = new OrderDAO();
        supplierDAO = new SupplierDAO();
        productDAO = new ProductDAO();
        cartItems = new ArrayList<>();

        setLayout(new BorderLayout());
        innerTabs = new JTabbedPane();

        // Initialize Tabs
        JPanel newOrderPanel = createNewOrderPanel();
        JPanel historyPanel = createHistoryPanel();

        innerTabs.addTab("New Order", newOrderPanel);
        innerTabs.addTab("Order History", historyPanel);
        
        innerTabs.addChangeListener(e -> {
            if (innerTabs.getSelectedIndex() == 1) {
                loadHistory();
            }
        });

        add(innerTabs, BorderLayout.CENTER);
    }

    private JPanel createNewOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT: Selection
        JPanel leftPanel = new JPanel(new GridLayout(9, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Order Setup"));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // Initialize components
        supplierCombo = new JComboBox<>();
        productCombo = new JComboBox<>();
        quantityField = new JTextField("10");
        costField = new JTextField("0.00");

        // Add to UI in order
        leftPanel.add(new JLabel("Select Supplier:"));
        leftPanel.add(supplierCombo);

        leftPanel.add(new JLabel("Select Product:"));
        leftPanel.add(productCombo);

        leftPanel.add(new JLabel("Quantity:"));
        leftPanel.add(quantityField);
        
        leftPanel.add(new JLabel("Unit Cost:"));
        leftPanel.add(costField);

        // Setup Logic
        loadSuppliers();
        supplierCombo.addActionListener(e -> loadSupplierProducts());
        loadSupplierProducts(); // Initial load

        JButton addBtn = new JButton("Add to Order");
        addBtn.addActionListener(e -> addToCart());
        leftPanel.add(addBtn);

        panel.add(leftPanel, BorderLayout.WEST);

        // CENTER: Cart
        JPanel centerPanel = new JPanel(new BorderLayout());
        String[] columns = {"Product ID", "Name", "Unit Cost", "Qty"};
        newOrderCartModel = new DefaultTableModel(columns, 0);
        JTable cartTable = new JTable(newOrderCartModel);
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        // BOTTOM: Checkout
        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total (Est): $0.00");
        bottom.add(totalLabel, BorderLayout.WEST);
        
        JButton placeOrderBtn = new JButton("Place Order (Pending)");
        placeOrderBtn.setBackground(new Color(50, 100, 200));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.addActionListener(e -> placeOrder());
        bottom.add(placeOrderBtn, BorderLayout.EAST);
        
        centerPanel.add(bottom, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton receiveBtn = new JButton("Mark Received (Update Stock)");
        JButton deleteBtn = new JButton("Delete (Pending Only)");
        
        toolBar.add(refreshBtn);
        toolBar.add(receiveBtn);
        toolBar.add(deleteBtn);
        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Order ID", "Supplier ID", "Date", "Total", "Status"};
        historyModel = new DefaultTableModel(cols, 0) {
             @Override
             public boolean isCellEditable(int row, int col) { return false; }
        };
        historyTable = new JTable(historyModel);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Actions
        refreshBtn.addActionListener(e -> loadHistory());
        receiveBtn.addActionListener(e -> onMarkReceived());
        deleteBtn.addActionListener(e -> onDeleteOrder());
        
        JButton detailsBtn = new JButton("View Details");
        detailsBtn.addActionListener(e -> onViewDetails());
        toolBar.add(detailsBtn);

        return panel;
    }
    
    // --- Logic for New Order ---

    private void loadSuppliers() {
        supplierCombo.removeAllItems();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            supplierCombo.addItem(s);
        }
    }

    private void loadSupplierProducts() {
        productCombo.removeAllItems();
        Supplier s = (Supplier) supplierCombo.getSelectedItem();
        if (s == null) return;

        List<Product> products = productDAO.getAllProducts(); 
        for (Product p : products) {
            if (p.getSupplierId() == s.getId()) {
                productCombo.addItem(p);
            }
        }
    }

    private void addToCart() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p == null) return;
        
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) {
                 JOptionPane.showMessageDialog(this, "Quantity must be > 0");
                 return;
            }
            
            double cost = Double.parseDouble(costField.getText().trim());
            if (cost < 0) {
                 JOptionPane.showMessageDialog(this, "Cost cannot be negative");
                 return;
            }
            
            OrderItem item = new OrderItem(p.getId(), qty, cost);
            cartItems.add(item);
            
            // Add to model
            newOrderCartModel.addRow(new Object[]{p.getId(), p.getName(), cost, qty});
            
            // Calc Total
            currentTotal += cost * qty; 
            totalLabel.setText(String.format("Total (Est): $%.2f", currentTotal));
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity or Cost");
        }
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        Supplier s = (Supplier) supplierCombo.getSelectedItem();
        Order order = new Order();
        order.setSupplierId(s.getId());
        order.setTotalAmount(currentTotal);
        order.setItems(cartItems);

        if (orderDAO.createOrder(order)) {
            JOptionPane.showMessageDialog(this, "Order Placed! Status: PENDING.\nStock not updated yet.");
            cartItems.clear();
            newOrderCartModel.setRowCount(0);
            currentTotal = 0;
            totalLabel.setText("Total (Est): $0.00");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to place order.");
        }
    }
    
    // --- Logic for History ---

    private void loadHistory() {
        historyModel.setRowCount(0);
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            historyModel.addRow(new Object[]{
                o.getId(),
                o.getSupplierId(), 
                o.getOrderDate(),
                o.getTotalAmount(),
                o.getStatus()
            });
        }
    }

    private void onMarkReceived() {
        int row = historyTable.getSelectedRow();
        if (row == -1) return;
        
        String status = (String) historyModel.getValueAt(row, 4);
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING orders can be received.");
            return;
        }

        int orderId = (int) historyModel.getValueAt(row, 0);
        if (orderDAO.markOrderReceived(orderId)) {
            JOptionPane.showMessageDialog(this, "Order Received! Stock Updated.");
            loadHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update order.");
        }
    }

    private void onDeleteOrder() {
        int row = historyTable.getSelectedRow();
        if (row == -1) return;

        String status = (String) historyModel.getValueAt(row, 4);
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING orders can be deleted.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this order?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
             int orderId = (int) historyModel.getValueAt(row, 0);
             if (orderDAO.deleteOrder(orderId)) {
                 loadHistory();
             } else {
                 JOptionPane.showMessageDialog(this, "Failed to delete.");
             }
        }
    }

    private void onViewDetails() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int orderId = (int) historyModel.getValueAt(row, 0);
        List<OrderItem> items = orderDAO.getItemsByOrderId(orderId);

        String[] cols = {"Product", "Unit Cost", "Qty", "Subtotal"};
        DefaultTableModel detailsModel = new DefaultTableModel(cols, 0);
        
        for (OrderItem item : items) {
             double sub = item.getUnitCost() * item.getQuantity();
             detailsModel.addRow(new Object[]{
                 item.getProductName(), // Requires join in DAO
                 item.getUnitCost(),
                 item.getQuantity(),
                 sub
             });
        }

        JTable detailsTable = new JTable(detailsModel);
        JScrollPane scroll = new JScrollPane(detailsTable);
        scroll.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scroll, "Order Details #" + orderId, JOptionPane.PLAIN_MESSAGE);
    }
}
