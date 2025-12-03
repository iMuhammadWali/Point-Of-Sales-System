package com.pos.database;

import java.sql.*;

public class DBConnection {
    private Connection conn;

    public DBConnection() {
        try {
            initConnection();
            createAllTables();
        } catch (SQLException e) {
            System.err.println("Error while initializing database connection: " + e.getMessage());
        }
    }

    public void initConnection() throws SQLException {
        try {
            String connectionUrl = "jdbc:sqlite:point_of_sale.db";
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("Database connection established.");

        } catch (SQLException e) {
            System.out.println("Error in initDBConnection function: " + e.getMessage());
            throw new SQLException("SQLite JDBC driver is missing.", e);
            // System.err.println("SQLite JDBC driver is missing.");
        }

    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
                // Do Nothing for now.
            }
        }
    }

    public PreparedStatement getPreparedStatement(String preparedStatementQuery) throws SQLException {
        try {
            return conn.prepareStatement(preparedStatementQuery, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            System.err.println("Error while creating prepared statement: " + e.getMessage());
            throw new SQLException("Error while creating prepared statement", e);
        }
    }
    public void createAllTables() throws SQLException {
        String[] tableSQLs = {
                "CREATE TABLE IF NOT EXISTS people (" +
                        "personID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "firstName VARCHAR(20), " +
                        "lastName VARCHAR(20), " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");",

                "CREATE TABLE IF NOT EXISTS personContactDetails (" +
                        "personID INTEGER NOT NULL, " +
                        "email VARCHAR(255), " +
                        "phone VARCHAR(20), " +
                        "FOREIGN KEY (personID) REFERENCES people(personID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS users (" +
                        "userID INTEGER PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "passwordHash VARCHAR(50) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'CASHIER')), " +
                        "currentlyActive BOOLEAN DEFAULT 1, " +
                        "FOREIGN KEY (userID) REFERENCES people(personID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS customers (" +
                        "customerID INTEGER PRIMARY KEY, " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (customerID) REFERENCES people(personID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS customerLoyaltyPoints (" +
                        "customerID INTEGER PRIMARY KEY NOT NULL, " +
                        "loyaltyPoints INTEGER CHECK (loyaltyPoints >= 0), " +
                        "FOREIGN KEY (customerID) REFERENCES customers(customerID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS Sessions (" +
                        "sessionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER NOT NULL, " +
                        "loginTime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "logoutTime DATETIME, " +
                        "FOREIGN KEY (userID) REFERENCES users(userID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS shifts (" +
                        "shiftId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER NOT NULL, " +
                        "startTime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "endTime DATETIME, " +
                        "FOREIGN KEY (userID) REFERENCES users(userID)" +
                        ");",


                "CREATE TABLE IF NOT EXISTS products (" +
                        "productID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "supplierName INTEGER, " +
                        "barcode VARCHAR(50) UNIQUE, " +
                        "name VARCHAR(200) NOT NULL, " +
                        "description TEXT, " +
                        "price DECIMAL(10,2) NOT NULL, " +
                        "cost_price DECIMAL(10,2), " +
                        "currentStock INTEGER NOT NULL DEFAULT 0, " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");",

                "CREATE TABLE IF NOT EXISTS discounts (" +
                        "discountId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "type VARCHAR(10) NOT NULL CHECK (type IN ('PERCENT', 'AMOUNT')), " +
                        "value DECIMAL(10,2) NOT NULL, " +
                        "code VARCHAR(50) UNIQUE, " +
                        "startDate DATETIME NOT NULL, " +
                        "endDate DATETIME, " +
                        "isActive BOOLEAN DEFAULT 1" +
                        ");",

                "CREATE TABLE IF NOT EXISTS salesTransactions (" +
                        "saleID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "customerID INTEGER NOT NULL, " +
                        "cashierID INTEGER NOT NULL, " +
                        "shiftID INTEGER NOT NULL, " +
                        "totalAmount DECIMAL(10,2) NOT NULL, " +
                        "taxAmount DECIMAL(10,2) NOT NULL, " +
                        "discountAmount DECIMAL(10,2) NOT NULL, " +
                        "finalAmount DECIMAL(10,2) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'COMPLETED' CHECK (status IN ('COMPLETED', 'VOIDED', 'REFUNDED')), " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (customerID) REFERENCES customers(customerID), " +
                        "FOREIGN KEY (cashierID) REFERENCES users(userID), " +
                        "FOREIGN KEY (shiftID) REFERENCES shifts(shiftID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS saleItems (" +
                        "saleItemID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "saleId INTEGER NOT NULL, " +
                        "productID INTEGER NOT NULL, " +
                        "quantity INTEGER NOT NULL, " +
                        "unitPrice DECIMAL(10,2) NOT NULL, " +
                        "lineTotal DECIMAL(10,2) NOT NULL, " +
                        "FOREIGN KEY (saleId) REFERENCES salesTransactions(saleID), " +
                        "FOREIGN KEY (productID) REFERENCES products(productID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS payments (" +
                        "paymentID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "saleId INTEGER NOT NULL, " +
                        "paymentMethod VARCHAR(20) NOT NULL CHECK (paymentMethod IN ('CASH', 'CARD', 'LOYALTY')), " +
                        "amount DECIMAL(20,2) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'COMPLETED' CHECK (status IN ('COMPLETED','VOIDED','REFUNDED')), " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (saleId) REFERENCES salesTransactions(saleID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS refunds (" +
                        "refundID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "originalSaleId INTEGER NOT NULL, " +
                        "reason TEXT NOT NULL, " +
                        "processedBy INTEGER NOT NULL, " +
                        "FOREIGN KEY (processedBy) REFERENCES users(userID), " +
                        "FOREIGN KEY (originalSaleId) REFERENCES salesTransactions(saleID)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS auditLogs (" +
                        "logID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER NOT NULL, " +
                        "actionType VARCHAR(50) NOT NULL CHECK (actionType IN (" +
                        "'LOGIN','LOGOUT','SHIFT_STARTED','SHIFT_ENDED','SALE_COMPLETED','SALE_REFUNDED'," +
                        "'PAYMENT_PROCESSED','PAYMENT_REFUNDED','STOCK_UPDATED','STOCK_ADJUSTED','STOCK_RESTOCKED'," +
                        "'PRODUCT_ADDED','PRODUCT_DISABLED','CUSTOMER_CREATED','CUSTOMER_UPDATED'," +
                        "'USER_CREATED','USER_UPDATED','USER_ROLE_CHANGED','USER_DISABLED','REPORT_GENERATED'" +
                        ")), " +
                        "description TEXT NOT NULL, " +
                        "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (userID) REFERENCES users(userID)" +
                        ");"
        };


        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (String sql : tableSQLs) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new SQLException("Error while creating tables", e.getMessage());
        } finally {
            closeStatement(stmt);
        }
    }

    public void setAutoCommitToValue(Boolean val) throws SQLException {
        try {
            conn.setAutoCommit(val);
        }
        catch (SQLException e){
            System.err.println("Error while setting auto commit to value");
            throw new SQLException("An Error occurred while changing the value of autoCommit",e);
        }
    }

    public void commit() throws SQLException {
        try {
            conn.commit();
        }
        catch (SQLException e){
            System.err.println("An Error occurred while committing the transaction");
            throw new SQLException("An error occurred while commiting the transaction", e);
        }
    }

    public void rollback() throws SQLException {
        try{
            conn.rollback();
        }
        catch (SQLException e){
            System.err.println("An Error occurred while rolling back the transaction");
            throw new SQLException("An error occurred while rolling back the transaction", e);
        }
    }

    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
