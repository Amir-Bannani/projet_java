# Features Walkthrough (User Guide)

This guide walks you through the main features of the Pharmacy Management System.

## 1. Logging In
*   **Launch**: Run the application.
*   **Input**: Enter username `admin` and password `admin123`.
*   **Role**: The system checks if you are an Admin or Employee. Admins see all tabs; Employees might have restricted access (if configured).

## 2. Managing Products (Inventory)
*   **Navigate**: Click the **"Products"** tab.
*   **View**: See a list of all current products, prices, and stock levels.
*   **Add**: Click "Add New" to create a product. You must select a Supplier (so make sure Suppliers exist first!).
*   **Edit/Delete**: Select a row to modify or remove it.

## 3. Managing Suppliers
*   **Navigate**: Click the **"Suppliers"** tab.
*   **Purpose**: Maintain a list of companies you buy from.
*   **Action**: Add names, contacts, and addresses. These suppliers populate the dropdown when creating products.

## 4. Re-Stocking (Supplier Orders)
*   **Navigate**: Click the **"Supplier Orders"** tab.
*   **New Order**:
    1.  Select a Supplier.
    2.  Select a Product (filtered to that supplier).
    3.  Enter Quantity and Cost.
    4.  Click "Add to Order".
    5.  Click **"Place Order (Pending)"**.
    *   *Note*: This creates a "PENDING" order. Stock is NOT increased yet.
*   **Receive Order**:
    1.  Go to **"Order History"** sub-tab.
    2.  Select a Pending order.
    3.  Click **"Mark Received"**.
    4.  **Result**: The order status changes to RECEIVED and the Product Stock increases automatically.

## 5. Making Sales (Point of Sale)
*   **Navigate**: Click the **"Sales (POS)"** tab.
*   **Process**:
    1.  (Optional) Select a Client.
    2.  Select a Product and Quantity.
    3.  Click "Add to Cart".
    4.  Repeat for other items.
    5.  Click **"Checkout"**.
    *   **Result**: Stock is decreased immediately. A sale record is created.
*   **History**:
    1.  Go to **"Sales History"** sub-tab.
    2.  See past transactions.
    3.  Click **"View Details"** to see exactly what items were sold in that transaction.

## 6. Managing Clients
*   **Navigate**: Click the **"Clients"** tab.
*   **Purpose**: specific customers to track their purchases.
*   **Function**: Add/Edit client details (Phone, Email).
