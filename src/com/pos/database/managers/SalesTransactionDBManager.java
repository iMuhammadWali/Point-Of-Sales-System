package com.pos.database.managers;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.pos.database.*;
import com.pos.database.managers.*;
import com.pos.models.*;


//TODO: Change Sales Items productID to product object.
public class SalesTransactionDBManager {

    private final DBConnection dbConnection;

    public SalesTransactionDBManager(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
//
//    /**
//     * Creates a new sales transaction with global discount application
//     * Real-world global discount flow:
//     * 1. Calculate total from all items
//     * 2. Apply global discount to total
//     * 3. Calculate tax on discounted amount
//     * 4. Calculate final amount
//     */
    public Integer createSalesTransaction(SalesTransaction transaction) throws SQLException {
        String insertSalesSQL = "INSERT INTO salesTransactions (customerID, cashierID, shiftID, " +
                "totalAmount, taxAmount, discountAmount, finalAmount, status, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertItemSQL = "INSERT INTO saleItems (saleId, productID, quantity, unitPrice, lineTotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        String insertPaymentSQL = "INSERT INTO payments (saleId, paymentMethod, amount, status, createdAt) " +
                "VALUES (?, ?, ?, ?, ?)";

        String updateStockSQL = "UPDATE products SET currentStock = currentStock - ? WHERE productID = ?";

        PreparedStatement salesStmt = null;
        PreparedStatement itemsStmt = null;
        PreparedStatement paymentsStmt = null;
        PreparedStatement stockStmt = null;
        ResultSet generatedKeys = null;
        Integer saleID = null;

        try {
            dbConnection.setAutoCommitToValue(false);

            // 1. Insert into salesTransactions
            salesStmt = dbConnection.getPreparedStatement(insertSalesSQL);
            salesStmt.setInt(1, transaction.getCustomerID());
            salesStmt.setInt(2, transaction.getCashierID());
            salesStmt.setInt(3, transaction.getShiftID());
            salesStmt.setBigDecimal(4, transaction.getTotalAmount());
            salesStmt.setBigDecimal(5, transaction.getTaxAmount());
            salesStmt.setBigDecimal(6, transaction.getDiscountAmount());
            salesStmt.setBigDecimal(7, transaction.getFinalAmount());
            salesStmt.setString(8, transaction.getStatus());
            salesStmt.setTimestamp(9, Timestamp.valueOf(transaction.getCreatedAt()));

            int affectedRows = salesStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating sales transaction failed, no rows affected.");
            }

            // Get generated saleID because we need it for other purposes.
            generatedKeys = salesStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Creating sales transaction failed, no ID obtained.");
            }
            saleID = generatedKeys.getInt(1);
            transaction.setSaleID(saleID);

            // 2. Insert sale items and update product stock in the DB 'products' table.
            itemsStmt = dbConnection.getPreparedStatement(insertItemSQL);
            stockStmt = dbConnection.getPreparedStatement(updateStockSQL);

            for (SaleItem item : transaction.getItems()) {
                // Insert sale item
                itemsStmt.setInt(1, saleID);
                itemsStmt.setInt(2, item.getProductID());
                itemsStmt.setInt(3, item.getQuantity());
                itemsStmt.setBigDecimal(4, item.getUnitPrice());
                itemsStmt.setBigDecimal(5, item.getLineTotal());
                itemsStmt.addBatch();

                // Update product stock and since we are subtracting it, it will be reduced.
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getProductID());
                stockStmt.addBatch();
            }

            itemsStmt.executeBatch();
            stockStmt.executeBatch();

            // 3. Insert payments
            paymentsStmt = dbConnection.getPreparedStatement(insertPaymentSQL);
            for (Payment payment : transaction.getPayments()) {
                paymentsStmt.setInt(1, saleID);
                paymentsStmt.setString(2, payment.getPaymentMethod());
                paymentsStmt.setBigDecimal(3, payment.getAmount());
                paymentsStmt.setString(4, payment.getStatus());
                paymentsStmt.setTimestamp(5, Timestamp.valueOf(payment.getCreatedAt()));
                paymentsStmt.addBatch();
            }
            paymentsStmt.executeBatch();

            dbConnection.commit();
            return saleID;

        } catch (SQLException e) {
            dbConnection.rollback();
            throw new SQLException("Error creating sales transaction", e);
        } finally {
            dbConnection.closeResultSet(generatedKeys);
            dbConnection.closeStatement(salesStmt);
            dbConnection.closeStatement(itemsStmt);
            dbConnection.closeStatement(paymentsStmt);
            dbConnection.closeStatement(stockStmt);
            dbConnection.setAutoCommitToValue(true);
        }
    }

    public List<SalesTransaction> getAllSalesTransactions() throws SQLException {
        String sql = "SELECT * FROM salesTransactions ORDER BY createdAt DESC";
        List<SalesTransaction> transactions = new ArrayList<>();

        PreparedStatement p = null;
        ResultSet rs = null;

        try {
            p = dbConnection.getPreparedStatement(sql);
            rs = p.executeQuery();

            while (rs.next()) {
                SalesTransaction transaction = new SalesTransaction();
                transaction.setSaleID(rs.getInt("saleID"));
                transaction.setCustomerID(rs.getInt("customerID"));
                transaction.setCashierID(rs.getInt("cashierID"));
                transaction.setShiftID(rs.getInt("shiftID"));
                transaction.setTotalAmount(rs.getBigDecimal("totalAmount"));
                transaction.setTaxAmount(rs.getBigDecimal("taxAmount"));
                transaction.setDiscountAmount(rs.getBigDecimal("discountAmount"));
                transaction.setFinalAmount(rs.getBigDecimal("finalAmount"));
                transaction.setStatus(rs.getString("status"));
                transaction.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

                // Load sale items if needed (optional)
                // transaction.setItems(getSaleItemsForTransaction(transaction.getSaleID()));

                // Load payments if needed (optional)
                // transaction.setPayments(getPaymentsForTransaction(transaction.getSaleID()));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching sales transactions", e);
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }

        return transactions;
    }


    public List<SalesTransaction> getSalesTransactionsOfToday() throws SQLException {
        String sql =
                "SELECT * " +
                        "FROM salesTransactions " +
                        "WHERE date(createdAt / 1000, 'unixepoch', 'localtime') = date('now','localtime') " +
                        "ORDER BY createdAt DESC";

        List<SalesTransaction> transactions = new ArrayList<>();

        PreparedStatement p = null;
        ResultSet rs = null;

        try {
            p = dbConnection.getPreparedStatement(sql);
            rs = p.executeQuery();

            while (rs.next()) {
                SalesTransaction transaction = new SalesTransaction();
                transaction.setSaleID(rs.getInt("saleID"));
                transaction.setCustomerID(rs.getInt("customerID"));
                transaction.setCashierID(rs.getInt("cashierID"));
                transaction.setShiftID(rs.getInt("shiftID"));
                transaction.setTotalAmount(rs.getBigDecimal("totalAmount"));
                transaction.setTaxAmount(rs.getBigDecimal("taxAmount"));
                transaction.setDiscountAmount(rs.getBigDecimal("discountAmount"));
                transaction.setFinalAmount(rs.getBigDecimal("finalAmount"));
                transaction.setStatus(rs.getString("status"));
                transaction.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

                // Load sale items if needed (optional)
                // transaction.setItems(getSaleItemsForTransaction(transaction.getSaleID()));

                // Load payments if needed (optional)
                // transaction.setPayments(getPaymentsForTransaction(transaction.getSaleID()));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching sales transactions", e);
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }

        return transactions;
    }

//    /**
//     * Calculate transaction totals with global discount
//     * Real-world: Global discount applied to subtotal, then tax calculated on discounted amount
//     */
//    public SalesTransaction calculateTransactionTotals(List<SaleItem> items,
//                                                       BigDecimal discountPercent,
//                                                       BigDecimal discountFixedAmount,
//                                                       BigDecimal taxRate) {
//        // Calculate subtotal from all items
//        BigDecimal subtotal = BigDecimal.ZERO;
//        for (SaleItem item : items) {
//            subtotal = subtotal.add(item.getLineTotal());
//        }
//
//        // Apply global discount (percentage takes precedence over fixed amount)
//        BigDecimal discountAmount = BigDecimal.ZERO;
//        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
//            discountAmount = subtotal.multiply(discountPercent)
//                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
//        } else if (discountFixedAmount != null && discountFixedAmount.compareTo(BigDecimal.ZERO) > 0) {
//            discountAmount = discountFixedAmount;
//        }
//
//        // Ensure discount doesn't exceed subtotal
//        if (discountAmount.compareTo(subtotal) > 0) {
//            discountAmount = subtotal;
//        }
//
//        // Calculate amount after discount
//        BigDecimal amountAfterDiscount = subtotal.subtract(discountAmount);
//
//        // Calculate tax (on discounted amount in real world)
//        BigDecimal taxAmount = amountAfterDiscount.multiply(taxRate)
//                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
//
//        // Calculate final amount
//        BigDecimal finalAmount = amountAfterDiscount.add(taxAmount);
//
//        SalesTransaction transaction = new SalesTransaction();
//        transaction.setTotalAmount(subtotal);
//        transaction.setDiscountAmount(discountAmount);
//        transaction.setTaxAmount(taxAmount);
//        transaction.setFinalAmount(finalAmount);
//        transaction.setItems(items);
//        transaction.setStatus("COMPLETED");
//        transaction.setCreatedAt(LocalDateTime.now());
//
//        return transaction;
//    }
//
//    /**
//     * Get sales transaction by ID with all related items and payments
//     */
    public SalesTransaction getSalesTransactionById(Integer saleID) throws SQLException {
        String salesSQL = "SELECT * FROM salesTransactions WHERE saleID = ?";
        SalesTransaction transaction = null;

        PreparedStatement salesStmt = null;
        ResultSet salesRs = null;

        try {
            salesStmt = dbConnection.getPreparedStatement(salesSQL);
            salesStmt.setInt(1, saleID);
            salesRs = salesStmt.executeQuery();

            if (salesRs.next()) {
                transaction = new SalesTransaction();
                transaction.setSaleID(salesRs.getInt("saleID"));
                transaction.setCustomerID(salesRs.getInt("customerID"));
                transaction.setCashierID(salesRs.getInt("cashierID"));
                transaction.setShiftID(salesRs.getInt("shiftID"));
                transaction.setTotalAmount(salesRs.getBigDecimal("totalAmount"));
                transaction.setTaxAmount(salesRs.getBigDecimal("taxAmount"));
                transaction.setDiscountAmount(salesRs.getBigDecimal("discountAmount"));
                transaction.setFinalAmount(salesRs.getBigDecimal("finalAmount"));
                transaction.setStatus(salesRs.getString("status"));
                transaction.setCreatedAt(salesRs.getTimestamp("createdAt").toLocalDateTime());

                // Load related items and payments
//                transaction.setItems(getSaleItemsBySaleId(saleID));
//                transaction.setPayments(getPaymentsBySaleId(saleID));
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching sales transaction", e);
        } finally {
            dbConnection.closeResultSet(salesRs);
            dbConnection.closeStatement(salesStmt);
        }

        return transaction;
    }

//    /**
//     * Get sale items for a specific sale
//     */
//    private List<SaleItem> getSaleItemsBySaleId(Integer saleID) throws SQLException {
//        String sql = "SELECT * FROM saleItems WHERE saleId = ?";
//        List<SaleItem> items = new ArrayList<>();
//
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            pstmt = dbConnection.getPreparedStatement(sql);
//            pstmt.setInt(1, saleID);
//            rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                SaleItem item = new SaleItem();
//                item.setSaleItemID(rs.getInt("saleItemID"));
//                item.setSaleId(rs.getInt("saleId"));
//                item.setProductID(rs.getInt("productID"));
//                item.setQuantity(rs.getInt("quantity"));
//                item.setUnitPrice(rs.getBigDecimal("unitPrice"));
//                item.setLineTotal(rs.getBigDecimal("lineTotal"));
//                items.add(item);
//            }
//        } finally {
//            dbConnection.closeResultSet(rs);
//            dbConnection.closeStatement(pstmt);
//        }
//
//        return items;
//    }
//
//    /**
//     * Get payments for a specific sale
//     */
//    private List<Payment> getPaymentsBySaleId(Integer saleID) throws SQLException {
//        String sql = "SELECT * FROM payments WHERE saleId = ?";
//        List<Payment> payments = new ArrayList<>();
//
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            pstmt = dbConnection.getPreparedStatement(sql);
//            pstmt.setInt(1, saleID);
//            rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Payment payment = new Payment();
//                payment.setPaymentID(rs.getInt("paymentID"));
//                payment.setSaleId(rs.getInt("saleId"));
//                payment.setPaymentMethod(rs.getString("paymentMethod"));
//                payment.setAmount(rs.getBigDecimal("amount"));
//                payment.setStatus(rs.getString("status"));
//                payment.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
//                payments.add(payment);
//            }
//        } finally {
//            dbConnection.closeResultSet(rs);
//            dbConnection.closeStatement(pstmt);
//        }
//
//        return payments;
//    }
//
//    /**
//     * Void a sales transaction (real-world: keep record but mark as voided, restore stock)
//     */
//    public boolean voidSalesTransaction(Integer saleID, Integer processedBy, String reason) throws SQLException {
//        String updateSalesSQL = "UPDATE salesTransactions SET status = 'VOIDED' WHERE saleID = ?";
//        String updatePaymentsSQL = "UPDATE payments SET status = 'VOIDED' WHERE saleId = ?";
//        String updateStockSQL = "UPDATE products SET currentStock = currentStock + ? WHERE productID = ?";
//        String insertRefundSQL = "INSERT INTO refunds (originalSaleId, reason, processedBy) VALUES (?, ?, ?)";
//
//        PreparedStatement salesStmt = null;
//        PreparedStatement paymentsStmt = null;
//        PreparedStatement stockStmt = null;
//        PreparedStatement refundStmt = null;
//
//        try {
//            dbConnection.setAutoCommitToValue(false);
//
//            // 1. Get sale items to restore stock
//            List<SaleItem> items = getSaleItemsBySaleId(saleID);
//
//            // 2. Update sales transaction status
//            salesStmt = dbConnection.getPreparedStatement(updateSalesSQL);
//            salesStmt.setInt(1, saleID);
//            salesStmt.executeUpdate();
//
//            // 3. Update payments status
//            paymentsStmt = dbConnection.getPreparedStatement(updatePaymentsSQL);
//            paymentsStmt.setInt(1, saleID);
//            paymentsStmt.executeUpdate();
//
//            // 4. Restore product stock
//            stockStmt = dbConnection.getPreparedStatement(updateStockSQL);
//            for (SaleItem item : items) {
//                stockStmt.setInt(1, item.getQuantity());
//                stockStmt.setInt(2, item.getProductID());
//                stockStmt.addBatch();
//            }
//            stockStmt.executeBatch();
//
//            // 5. Record in refunds table for audit
//            refundStmt = dbConnection.getPreparedStatement(insertRefundSQL);
//            refundStmt.setInt(1, saleID);
//            refundStmt.setString(2, reason);
//            refundStmt.setInt(3, processedBy);
//            refundStmt.executeUpdate();
//
//            dbConnection.commit();
//            return true;
//
//        } catch (SQLException e) {
//            dbConnection.rollback();
//            throw new SQLException("Error voiding sales transaction", e);
//        } finally {
//            dbConnection.closeStatement(salesStmt);
//            dbConnection.closeStatement(paymentsStmt);
//            dbConnection.closeStatement(stockStmt);
//            dbConnection.closeStatement(refundStmt);
//            dbConnection.setAutoCommitToValue(true);
//        }
//    }
//
//    /**
//     * Get sales transactions for a specific shift (useful for end-of-shift reporting)
//     */
//    public List<SalesTransaction> getSalesTransactionsByShift(Integer shiftID) throws SQLException {
//        String sql = "SELECT * FROM salesTransactions WHERE shiftID = ? AND status = 'COMPLETED' ORDER BY createdAt";
//        List<SalesTransaction> transactions = new ArrayList<>();
//
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            pstmt = dbConnection.getPreparedStatement(sql);
//            pstmt.setInt(1, shiftID);
//            rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                SalesTransaction transaction = new SalesTransaction();
//                transaction.setSaleID(rs.getInt("saleID"));
//                transaction.setCustomerID(rs.getInt("customerID"));
//                transaction.setCashierID(rs.getInt("cashierID"));
//                transaction.setShiftID(rs.getInt("shiftID"));
//                transaction.setTotalAmount(rs.getBigDecimal("totalAmount"));
//                transaction.setTaxAmount(rs.getBigDecimal("taxAmount"));
//                transaction.setDiscountAmount(rs.getBigDecimal("discountAmount"));
//                transaction.setFinalAmount(rs.getBigDecimal("finalAmount"));
//                transaction.setStatus(rs.getString("status"));
//                transaction.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
//
//                transactions.add(transaction);
//            }
//        } catch (SQLException e) {
//            throw new SQLException("Error while fetching shift transactions", e);
//        } finally {
//            dbConnection.closeResultSet(rs);
//            dbConnection.closeStatement(pstmt);
//        }
//
//        return transactions;
//    }
//
//    /**
//     * Get daily sales summary for reporting
//     */
//
//    // Will write the remaining functions after the UI is done
////    public Map<String, Object> getDailySalesSummary(Date date) throws SQLException {
////        String sql = "SELECT " +
////                "COUNT(*) as totalTransactions, " +
////                "SUM(finalAmount) as totalRevenue, " +
////                "SUM(taxAmount) as totalTax, " +
////                "SUM(discountAmount) as totalDiscount " +
////                "FROM salesTransactions " +
////                "WHERE DATE(createdAt) = ? AND status = 'COMPLETED'";
////
////        PreparedStatement pstmt = null;
////        ResultSet rs = null;
////        Map<String, Object> summary = new HashMap<>();
////
////        try {
////            pstmt = dbConnection.getPreparedStatement(sql);
////            pstmt.setDate(1, date);
////            rs = pstmt.executeQuery();
////
////            if (rs.next()) {
////                summary.put("totalTransactions", rs.getInt("totalTransactions"));
////                summary.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
////                summary.put("totalTax", rs.getBigDecimal("totalTax"));
////                summary.put("totalDiscount", rs.getBigDecimal("totalDiscount"));
////                summary.put("date", date);
////            }
////        } catch (SQLException e) {
////            throw new SQLException("Error while fetching daily sales summary", e);
////        } finally {
////            dbConnection.closeResultSet(rs);
////            dbConnection.closeStatement(pstmt);
////        }
////
////        return summary;
////    }
////
//    // Add these methods to your SalesTransactionDBManager class
//
//    /**
//     * Process a full refund for a sales transaction
//     * Real-world scenario: Refund entire amount, restore all items to stock, and mark as REFUNDED
//     */
//    public boolean processRefund(Integer saleID, Integer processedBy, String reason) throws SQLException {
//        String updateSalesSQL = "UPDATE salesTransactions SET status = 'REFUNDED' WHERE saleID = ? AND status = 'COMPLETED'";
//        String updatePaymentsSQL = "UPDATE payments SET status = 'REFUNDED' WHERE saleId = ? AND status = 'COMPLETED'";
//        String updateStockSQL = "UPDATE products SET currentStock = currentStock + ? WHERE productID = ?";
//        String insertRefundSQL = "INSERT INTO refunds (originalSaleId, reason, processedBy) VALUES (?, ?, ?)";
//
//        PreparedStatement salesStmt = null;
//        PreparedStatement paymentsStmt = null;
//        PreparedStatement stockStmt = null;
//        PreparedStatement refundStmt = null;
//
//        try {
//            dbConnection.setAutoCommitToValue(false);
//
//            // 1. Get the original sale transaction and validate
//            SalesTransaction originalSale = getSalesTransactionById(saleID);
//            if (originalSale == null) {
//                throw new SQLException("Original sale transaction not found with ID: " + saleID);
//            }
//
//            // 2. Check if sale is already refunded or voided
//            if ("REFUNDED".equals(originalSale.getStatus())) {
//                throw new SQLException("Sale transaction is already refunded");
//            }
//
//            if ("VOIDED".equals(originalSale.getStatus())) {
//                throw new SQLException("Sale transaction is voided and cannot be refunded");
//            }
//
//            if (!"COMPLETED".equals(originalSale.getStatus())) {
//                throw new SQLException("Only COMPLETED sales can be refunded. Current status: " + originalSale.getStatus());
//            }
//
//            List<SaleItem> items = originalSale.getItems();
//            if (items == null || items.isEmpty()) {
//                throw new SQLException("No items found in the original sale");
//            }
//
//            // 3. Update sales transaction status to REFUNDED
//            salesStmt = dbConnection.getPreparedStatement(updateSalesSQL);
//            salesStmt.setInt(1, saleID);
//            int salesUpdated = salesStmt.executeUpdate();
//
//            if (salesUpdated == 0) {
//                throw new SQLException("Failed to update sales transaction status. It may already be refunded or voided.");
//            }
//
//            // 4. Update payments status to REFUNDED
//            paymentsStmt = dbConnection.getPreparedStatement(updatePaymentsSQL);
//            paymentsStmt.setInt(1, saleID);
//            paymentsStmt.executeUpdate();
//
//            // 5. Restore product stock for all items
//            stockStmt = dbConnection.getPreparedStatement(updateStockSQL);
//            for (SaleItem item : items) {
//                stockStmt.setInt(1, item.getQuantity());
//                stockStmt.setInt(2, item.getProductID());
//                stockStmt.addBatch();
//            }
//            stockStmt.executeBatch();
//
//            // 6. Record in refunds table for audit
//            refundStmt = dbConnection.getPreparedStatement(insertRefundSQL);
//            refundStmt.setInt(1, saleID);
//            refundStmt.setString(2, reason);
//            refundStmt.setInt(3, processedBy);
//            refundStmt.executeUpdate();
//
//            dbConnection.commit();
//            return true;
//
//        } catch (SQLException e) {
//            dbConnection.rollback();
//            throw new SQLException("Error processing refund: " + e.getMessage(), e);
//        } finally {
//            dbConnection.closeStatement(salesStmt);
//            dbConnection.closeStatement(paymentsStmt);
//            dbConnection.closeStatement(stockStmt);
//            dbConnection.closeStatement(refundStmt);
//            dbConnection.setAutoCommitToValue(true);
//        }
//    }
//
//    /**
//     * Check if a sale can be refunded
//     * Validations: Must exist, be COMPLETED status, and not already refunded/voided
//     */
//    public boolean canRefundSale(Integer saleID) throws SQLException {
//        String sql = "SELECT status FROM salesTransactions WHERE saleID = ?";
//
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            pstmt = dbConnection.getPreparedStatement(sql);
//            pstmt.setInt(1, saleID);
//            rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                String status = rs.getString("status");
//                return "COMPLETED".equals(status);
//            }
//            return false;
//
//        } catch (SQLException e) {
//            throw new SQLException("Error checking refund eligibility", e);
//        } finally {
//            dbConnection.closeResultSet(rs);
//            dbConnection.closeStatement(pstmt);
//        }
//    }

}