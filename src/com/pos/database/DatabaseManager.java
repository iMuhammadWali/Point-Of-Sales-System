package com.pos.database;

import com.pos.database.managers.ProductDBManager;
import com.pos.database.managers.SalesTransactionDBManager;
import com.pos.database.managers.UserDBManager;

// This is a singleton Class
public class DatabaseManager {
    private DBConnection db;
    private ProductDBManager pm;
    private UserDBManager um;
    private SalesTransactionDBManager sm;

    private static DatabaseManager instance;

    private DatabaseManager(){
        // TODO: Keep adding new managers from the data layer that I have already created.
        instance = null;
        db = new DBConnection();
        pm = new ProductDBManager(db);
        um = new UserDBManager(db);
        sm = new SalesTransactionDBManager(db);
    }

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    public ProductDBManager getProductManager(){
        return pm;
    }
    public SalesTransactionDBManager getSalesTransactionManager(){
        return sm;
    }
    public UserDBManager getUserManager(){
        return um;
    }
}
