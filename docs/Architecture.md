# Architecture & Technical Guide

This document explains the internal structure of the Pharmacy Management System. It is designed to help developers (and beginners!) understand how the parts fit together.

## 🏗️ Architectural Pattern: MVC

The project follows the **Model-View-Controller (MVC)** pattern, which separates the application into three main logical components:

1.  **Model (Data)**: Represents the data structure (e.g., `User`, `Product`, `Sale`).
2.  **View (UI)**: The visual interface where users interact (e.g., `SalesPanel`, `LoginFrame`).
3.  **Controller (Logic)**: Managed here primarily by **DAOs** (Data Access Objects) which handle the communication between the App and the Database.

### 📁 Package Structure

*   `com.pharmacy.config`: Configuration classes.
    *   `DBConnection.java`: Handles the connection to MySQL. It uses the **Singleton Pattern** (via static methods) to ensure we don't open too many connections.
*   `com.pharmacy.model`: The "Blueprints" for our data.
    *   `User.java`, `Product.java`, `Client.java`, etc. are simple classes with fields (variables), getters, and setters.
*   `com.pharmacy.dao`: The "Workers" that talk to the Database.
    *   `UserDAO.java`: Handles login verification, adding users.
    *   `ProductDAO.java`: Fetches products, updates stock counts.
    *   `OrderDAO.java`: Manages supplier orders.
    *   `SaleDAO.java`: Records sales transactions.
*   `com.pharmacy.ui`: The "Screens".
    *   `MainFrame.java`: The main window window with tabs.
    *   `LoginFrame.java`: The first window you see.
    *   `SalesPanel.java`, `ProductPanel.java`: specialized screens for specific tasks.

## 🧠 Key Concepts Explained

### 1. Database Connection (JDBC)
In `DBConnection.java`, we use `DriverManager.getConnection()` to talk to MySQL.
```java
// Example of how we get a connection
Connection conn = DBConnection.getConnection();
```

### 2. Data Access Objects (DAO)
Instead of writing SQL inside our UI code (which is messy), we hide it in DAOs.
**Example (`ProductDAO`):**
```java
public List<Product> getAllProducts() {
    // 1. Write SQL
    String sql = "SELECT * FROM products";
    // 2. Execute
    // 3. Convert 'ResultSet' (Database rows) into Java 'Product' objects
}
```

### 3. Transactions (Important!)
For **Sales** and **Orders**, we use database transactions to ensure data safety.
When a sale happens:
1.  We create a `Sale` record.
2.  We create multiple `SaleItem` records.
3.  We **Decrease** the stock of the product.

All these must happen together. If one fails, **everything rolls back** (undoes) so the data stays correct.
```java
conn.setAutoCommit(false); // Start Transaction
try {
    // ... insert sale ...
    // ... insert items ...
    // ... update stock ...
    conn.commit(); // Save everything
} catch (Exception e) {
    conn.rollback(); // Undo if error
}
```

## 🔌 Database Schema Relationship

*   **Users**: Can be admins or employees.
*   **Suppliers**: Companies who sell products to the pharmacy.
*   **Products**: Items sold. Linked to a `Supplier`.
*   **Orders**: Requests to Suppliers for more stock.
*   **Sales**: Transactions with Clients.
