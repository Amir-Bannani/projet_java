package com.pharmacy.ui;

import com.pharmacy.dao.ClientDAO;
import com.pharmacy.dao.ProductDAO;
import com.pharmacy.dao.SaleDAO;
import com.pharmacy.model.Client;
import com.pharmacy.model.Product;
import com.pharmacy.model.Sale;
import com.pharmacy.model.SaleItem;
import com.pharmacy.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SalesPanel extends JPanel {
    private JTabbedPane innerTabs;
    private User currentUser;
    
    // DAOs
    private ProductDAO productDAO;
    private SaleDAO saleDAO;
    private ClientDAO clientDAO;
    
    // --- Tab 1: New Sale Components ---
    private JComboBox<Product> productCombo;
    private JComboBox<Client> clientCombo;
    private JTextField quantityField;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    
    private List<SaleItem> cartItems;
    private double grandTotal = 0.0;

    // --- Tab 2: Sales History Components ---
    private JTable historyTable;
    private DefaultTableModel historyModel;

    public SalesPanel(User user) {
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.saleDAO = new SaleDAO();
        this.clientDAO = new ClientDAO();
        this.cartItems = new ArrayList<>();

        setLayout(new BorderLayout());
        innerTabs = new JTabbedPane();

        JPanel newSalePanel = createNewSalePanel();
        JPanel historyPanel = createHistoryPanel();

        innerTabs.addTab("New Sale (POS)", newSalePanel);
        innerTabs.addTab("Sales History", historyPanel);
        
        innerTabs.addChangeListener(e -> {
            if (innerTabs.getSelectedIndex() == 1) {
                loadHistory();
            }
        });

        add(innerTabs, BorderLayout.CENTER);
    }

    private JPanel createNewSalePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT: Input
        JPanel leftPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Add to Cart"));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        leftPanel.add(new JLabel("Select Client (Optional):"));
        clientCombo = new JComboBox<>();
        loadClients();
        leftPanel.add(clientCombo);

        leftPanel.add(new JLabel("Select Product:"));
        productCombo = new JComboBox<>();
        loadProducts();
        leftPanel.add(productCombo);

        leftPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField("1");
        leftPanel.add(quantityField);

        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.addActionListener(e -> addToCart());
        leftPanel.add(addToCartBtn);

        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.addActionListener(e -> clearCart());
        leftPanel.add(clearCartBtn);
        
        panel.add(leftPanel, BorderLayout.WEST);

        // CENTER: Cart Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        String[] columns = {"Product ID", "Name", "Price", "Qty", "Subtotal"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // BOTTOM: Checkout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setBackground(new Color(50, 200, 50));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> performCheckout());
        bottomPanel.add(checkoutBtn, BorderLayout.EAST);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadHistory());
        toolBar.add(refreshBtn);
        
        JButton detailsBtn = new JButton("View Details");
        detailsBtn.addActionListener(e -> onViewDetails());
        toolBar.add(detailsBtn);

        panel.add(toolBar, BorderLayout.NORTH);

        String[] cols = {"Sale ID", "Date", "Client", "Total ($)"};
        historyModel = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        historyTable = new JTable(historyModel);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        return panel;
    }

    // --- POS Logic ---

    private void loadClients() {
        clientCombo.removeAllItems();
        // Add a dummy "Walk-in Customer"
        Client dummy = new Client(0, "Walk-in Customer", "N/A", "", 0.0);
        clientCombo.addItem(dummy);
        
        List<Client> clients = clientDAO.getAllClients();
        for (Client c : clients) {
            clientCombo.addItem(c);
        }
    }

    private void loadProducts() {
        productCombo.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            productCombo.addItem(p);
        }
    }

    private void addToCart() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        String qtyStr = quantityField.getText().trim();
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
            if (qty > selectedProduct.getStockQuantity()) {
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + selectedProduct.getStockQuantity());
                return;
            }

            SaleItem item = new SaleItem(selectedProduct.getId(), qty, selectedProduct.getPrice());
            cartItems.add(item);
            
            double subtotal = selectedProduct.getPrice() * qty;
            cartModel.addRow(new Object[]{
                selectedProduct.getId(),
                selectedProduct.getName(),
                selectedProduct.getPrice(),
                qty,
                subtotal
            });
            
            updateTotal();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
        }
    }

    private void updateTotal() {
        grandTotal = 0.0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            grandTotal += (double) cartModel.getValueAt(i, 4);
        }
        totalLabel.setText(String.format("Total: $%.2f", grandTotal));
    }

    private void clearCart() {
        cartItems.clear();
        cartModel.setRowCount(0);
        updateTotal();
    }

    private void performCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Finalize Sale?", "Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Sale sale = new Sale();
        sale.setUserId(currentUser.getId());
        
        Client client = (Client) clientCombo.getSelectedItem();
        if (client != null && client.getId() > 0) {
            sale.setClientId(client.getId());
        } else {
            sale.setClientId(0); // Anonymous
        }

        sale.setTotalAmount(grandTotal);
        sale.setItems(cartItems);

        boolean success = saleDAO.createSale(sale);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Sale Completed Successfully!");
            clearCart();
            loadProducts(); // Stock changed, refresh combo
        } else {
            JOptionPane.showMessageDialog(this, "Sale Failed. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- History Logic ---

    private void loadHistory() {
        historyModel.setRowCount(0);
        List<Sale> sales = saleDAO.getAllSales();
        for (Sale s : sales) {
            historyModel.addRow(new Object[]{
                s.getId(),
                s.getSaleDate(),
                s.getClientName(), 
                s.getTotalAmount()
            });
        }
    }

    private void onViewDetails() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sale first.");
            return;
        }

        int saleId = (int) historyModel.getValueAt(row, 0);
        List<SaleItem> items = saleDAO.getItemsBySaleId(saleId);

        String[] cols = {"Product", "Unit Price", "Qty", "Subtotal"};
        DefaultTableModel detailsModel = new DefaultTableModel(cols, 0);
        
        for (SaleItem item : items) {
             double sub = item.getUnitPrice() * item.getQuantity();
             detailsModel.addRow(new Object[]{
                 item.getProductName(), 
                 item.getUnitPrice(),
                 item.getQuantity(),
                 sub
             });
        }

        JTable detailsTable = new JTable(detailsModel);
        JScrollPane scroll = new JScrollPane(detailsTable);
        scroll.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scroll, "Sale Details #" + saleId, JOptionPane.PLAIN_MESSAGE);
    }
}
