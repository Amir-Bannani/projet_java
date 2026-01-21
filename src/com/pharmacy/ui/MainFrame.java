package com.pharmacy.ui;

import com.pharmacy.model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Pharmacy Management System | Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Products Tab
        tabbedPane.addTab("Products", new ProductPanel());

        // 2. Suppliers Tab
        tabbedPane.addTab("Suppliers", new SupplierPanel());

        // 3. Clients Tab
        tabbedPane.addTab("Clients", new ClientPanel());
        
        // 4. Supplier Orders (Restock)
        tabbedPane.addTab("Restock", new OrderPanel());
        
        // 5. Sales Tab (Point of Sale)
        tabbedPane.addTab("Point of Sale", new SalesPanel(user));
        
        // 4. Admin Only Tabs
        // 4. Admin Only Tabs
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("Manage Users", new UserPanel());
            tabbedPane.addTab("Reports & Alerts", new ReportsPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
        
        // Logout Button in Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        menu.add(logoutItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }
}
