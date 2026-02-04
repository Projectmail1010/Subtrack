# 🏦 SubTrack: Personal Subscription & Utility Billing Manager

![Java](https://img.shields.io/badge/Language-Java-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-red)
![Architecture](https://img.shields.io/badge/Pattern-DAO-green)

## 📌 Project Overview
SubTrack is a professional-grade desktop application designed to solve the "hidden cost" problem of modern digital life. It allows users to track recurring subscriptions (streaming, software) and utility bills (electricity, water) in one central dashboard with automated financial logic.

## 🛠️ Technical Architecture
This project demonstrates professional software engineering standards and design patterns:

* **DAO (Data Access Object) Pattern:** Completely decouples high-level business logic from low-level SQL operations for high maintainability.
* **Service-Oriented Logic:** Centralized services like `SubscriptionRenewalService` handle complex date arithmetic and automated payment windows.
* **Relational Database Design:** Built on a normalized MySQL schema using primary and foreign keys to ensure data integrity across payments and bills.
* **Event-Driven UI:** A custom Swing interface featuring animated sidebars, dynamic stat cards, and real-time validation.

## 🚀 Key Features
* **Automated Renewal Engine:** Automatically records payments for "Auto-Pay" services and extends due dates based on billing cycles (Daily, Weekly, Monthly, Yearly).
* **Financial Insights:** A dashboard that aggregates total spending across all services and highlights bills due within a 10-day window.
* **Utility Bill Reconciler:** Automatically matches payment records to unpaid utility bills via reference number tracking.
* **Advanced Data Management:** Comprehensive CRUD operations and specialized filtering for platform, service type, and payment status.

## ⚙️ Installation & Setup
1.  **Database Configuration:**
    * Create a MySQL database named `subtrack_db`.
    * Execute the schema found in `SQLQuries.txt`.
    * Update `DBConnection.java` with your local credentials.
2.  **Environment:**
    * Ensure Java 8+ and the MySQL Connector/J driver are configured in your classpath.
3.  **Run:**
    * Execute `com.subtrack.ui.MainFrame` to launch the entry portal.

## 👤 Author
**Ayush Vajpayee**
*Final Year BCA Student | Python & Java Developer*
