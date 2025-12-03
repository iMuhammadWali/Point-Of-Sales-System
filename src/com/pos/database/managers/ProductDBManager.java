package com.pos.database.managers;


import  java.sql.*;
import  java.util.*;

import com.pos.models.*;
import com.pos.database.*;

public class ProductDBManager {
    private final DBConnection dbConnection;
    public ProductDBManager(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean createNewProduct(Product p){
        // Will write this later.
        return true;
    }


    public List<Product> getProductsFromName(String name) throws SQLException {
        String sql = "SELECT * FROM Products WHERE LOWER(name) LIKE LOWER(?)";
        List<Product> result = new ArrayList<>();

        PreparedStatement p = null;
        ResultSet rs = null;
        System.out.println(name);
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setString(1, "%" + name + "%");
            rs = p.executeQuery();

            while (rs.next()) {
                System.out.println("Why is this not working");
                result.add(
                        new Product(
                                rs.getInt("productID"),
                                rs.getString("supplierName"),
                                rs.getString("barcode"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getDouble("cost_price"),
                                rs.getInt("currentStock"),
                                rs.getString("createdAt")
                        )
                );

            }
        }
        catch (SQLException e) {
            throw new SQLException("Error while fetching products by name", e);
        }
        finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }

        return result;
    }

    public Product getProductFromBarcode(String barcode) throws SQLException {
        PreparedStatement p = dbConnection.getPreparedStatement(
                "SELECT * FROM Products WHERE barcode = ? LIMIT 1"
        );

        p.setString(1, barcode);
        ResultSet rs = null;
        Product result = null;
        try {
            rs = p.executeQuery();
            if (rs.next()) {
                System.out.println("Why is this not working");
                result = new Product(
                        rs.getInt("productID"),
                        rs.getString("supplierName"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getDouble("cost_price"),
                        rs.getInt("currentStock"),
                        rs.getString("createdAt")
                );
                result.printProduct();
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching product by barcode", e);
        }
        finally{
            dbConnection.closeStatement(p);
            dbConnection.closeResultSet(rs);
        }
        return result;
    }

    public List<Product> getNewlyAddedProducts(int limit) throws SQLException {
        String sql = "SELECT * FROM Products ORDER BY createdAt DESC LIMIT ?";
        List<Product> result = new ArrayList<>();

        PreparedStatement p = null;
        ResultSet rs = null;

        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, limit);
            rs = p.executeQuery();

            while (rs.next()) {
                result.add(
                        new Product(
                                rs.getInt("productID"),
                                rs.getString("supplierName"),
                                rs.getString("barcode"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getDouble("cost_price"),
                                rs.getInt("currentStock"),
                                rs.getString("createdAt")
                        )
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error while fetching newly added products", e);
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }

        return result;
    }

    public boolean changeProductStockWithValue(int productID, int value) throws SQLException {
        // Update the currentStock of a product by adding 'value'
        String sql = "UPDATE Products SET currentStock = currentStock + ? WHERE productID = ?";
        boolean result = false;
        PreparedStatement p = null;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, value);
            p.setInt(2, productID);

            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while updating product stock", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return result;
    }

}