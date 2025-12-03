package com.pos.database.managers;

import java.sql.*;
import java.util.*;


import com.pos.database.DBConnection;
import com.pos.models.*;

public class UserDBManager {
    private final DBConnection dbConnection;

    // Constructor
    public UserDBManager(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean checkUsernameAlreadyExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        PreparedStatement p = null;
        ResultSet rs = null;
        boolean result = false;
        try{
            p = dbConnection.getPreparedStatement(sql);
            p.setString(1, username);
            rs = p.executeQuery();
            if (rs.next()){
                result = true;
            }
        }
        catch (SQLException e){
            throw new SQLException("An Error while checkingUsernameAlreadyExists", e);
        }
        finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }
        return result;
    }

    public boolean createNewUser(String firstName, String lastName, String username, String passwordHash, String role) throws SQLException {
        if (checkUsernameAlreadyExists(username)) {
            return false;
        }

        String insertPersonSQL = "INSERT INTO people (firstName, lastName) VALUES (?, ?)";
        String insertUserSQL = "INSERT INTO users (userID, username, passwordHash, role) VALUES (?, ?, ?, ?)";

        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        ResultSet rs = null;
        boolean result = false;
        try {
            dbConnection.setAutoCommitToValue(false);

            p1 = dbConnection.getPreparedStatement(insertPersonSQL);
            p1.setString(1, firstName);
            p1.setString(2, lastName);
            p1.executeUpdate();

            rs = p1.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("PersonID not generated");
            }
            int personID = rs.getInt(1);

            p2 = dbConnection.getPreparedStatement(insertUserSQL);
            p2.setInt(1, personID);
            p2.setString(2, username);
            p2.setString(3, passwordHash);
            p2.setString(4, role);
            int rowsAffected = p2.executeUpdate();
            result = rowsAffected > 0;
            dbConnection.commit();
        } catch (SQLException e) {
            // Rollback to last commit because we may be able to insert to people table but not in the users table.
            dbConnection.rollback();
            throw new SQLException("Error while adding new user", e);
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p1);
            dbConnection.closeStatement(p2);
            dbConnection.setAutoCommitToValue(true);
        }
        return result;
    }

    public boolean changeUserPassword(Integer userID, String newPassword) throws SQLException {
        String sql = "UPDATE users SET passwordHash = ? WHERE userID = ?";
        PreparedStatement p = null;
        boolean result = false;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setString(1, newPassword);
            p.setInt(2, userID);
            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while changing password", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return  result;
    }

    // This function can also return the username in addition as well.
    public String[] verifyCredentials(String username, String passwordHash) throws SQLException {
        String sql = "SELECT userID, username, role FROM users WHERE username = ? AND passwordHash = ?";
        ResultSet rs = null;
        PreparedStatement p = null;

        try{
            p = dbConnection.getPreparedStatement(sql);
            p.setString(1, username);
            p.setString(2, passwordHash);

            rs = p.executeQuery();
            if (rs.next()) {
                String userID = String.valueOf(rs.getInt(1));
                String _username = rs.getString(2);
                String role = rs.getString(3);
                return new String[]{userID, _username, role};
            }
        }
        catch (SQLException e){
            throw new SQLException("Error in verifying user credentials", e);
        }
        finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(p);
        }
        return null;
    }

    public boolean changeUserRole(String username, String newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        try{
            PreparedStatement p = dbConnection.getPreparedStatement(sql);
            p.setString(1, newRole);
            p.setString(2, username);
            int rowsUpdated = p.executeUpdate();

            System.out.println(username + "'s role has been updated to: " + newRole);

            return rowsUpdated > 0;
        }
        catch (SQLException e){
            // Do nothing for now.
            throw new SQLException("Error while changing user role", e);
        }
    }

    // since the verifyCredentials function return userID, it can be used here.
    public boolean recordUserLogin(int userID) throws SQLException {
        String sql = "INSERT INTO Sessions (userID, loginTime) VALUES (?, CURRENT_TIMESTAMP)";
        PreparedStatement p = null;
        boolean result = false;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, userID);
            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while recording user login", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return result;
    }

    public boolean recordUserLogout(int sessionID) throws SQLException {
        String sql = "UPDATE Sessions SET logoutTime = CURRENT_TIMESTAMP WHERE sessionID = ?";
        PreparedStatement p = null;
        boolean result = false;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, sessionID);
            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while recording user logout", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return result;
    }

    public boolean recordUserShiftStart(int userID) throws SQLException {
        String sql = "INSERT INTO shifts (userID, startTime) VALUES (?, CURRENT_TIMESTAMP)";
        PreparedStatement p = null;
        boolean result = false;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, userID);
            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while recording shift start", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return result;
    }

    public boolean recordUserShiftEnd(int shiftID) throws SQLException {
        String sql = "UPDATE shifts SET endTime = CURRENT_TIMESTAMP WHERE shiftId = ?";
        PreparedStatement p = null;
        boolean result = false;
        try {
            p = dbConnection.getPreparedStatement(sql);
            p.setInt(1, shiftID);
            int rowsAffected = p.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while recording shift end", e);
        } finally {
            dbConnection.closeStatement(p);
        }
        return result;
    }

    public User getUserDetailsFromUsername(String username) throws SQLException {
        String selectUserSQL = "SELECT * FROM users WHERE username = ?";
        String selectPeopleSQL = "SELECT * FROM people WHERE personID = ?";

        PreparedStatement p1 = dbConnection.getPreparedStatement(selectUserSQL);
        PreparedStatement p2 = dbConnection.getPreparedStatement(selectPeopleSQL);

        ResultSet rs1 = null;
        ResultSet rs2 = null;

        User result = null;
        try {
            p1.setString(1, username);
            rs1 = p1.executeQuery();
            if (rs1.next()) {
                p2.setInt(1, rs1.getInt("userID"));
                rs2 = p2.executeQuery();
                result = new User(
                        rs1.getInt("userID"),
                        rs2.getString("firstName"),
                        rs2.getString("lastName"),
                        rs1.getString("username"),
                        rs1.getString("role")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving user", e);
        } finally {
            dbConnection.closeResultSet(rs1);
            dbConnection.closeResultSet(rs2);
            dbConnection.closeStatement(p1);
            dbConnection.closeStatement(p2);

        }
        return result;
    }
    public User getUserDetailsFromID(Integer userID) throws SQLException {
        String selectUserSQL = "SELECT * FROM users WHERE userID = ?";
        String selectPeopleSQL = "SELECT * FROM people WHERE personID = ?";

        PreparedStatement p1 = dbConnection.getPreparedStatement(selectUserSQL);
        PreparedStatement p2 = dbConnection.getPreparedStatement(selectPeopleSQL);

        ResultSet rs1 = null;
        ResultSet rs2 = null;

        User result = null;
        try {
            p1.setInt(1, userID);
            rs1 = p1.executeQuery();
            if (rs1.next()) {
                p2.setInt(1, rs1.getInt("userID"));
                rs2 = p2.executeQuery();
                result = new User(
                        rs1.getInt("userID"),
                        rs2.getString("firstName"),
                        rs2.getString("lastName"),
                        rs1.getString("username"),
                        rs1.getString("role")
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving user", e);
        } finally {
            dbConnection.closeResultSet(rs1);
            dbConnection.closeResultSet(rs2);
            dbConnection.closeStatement(p1);
            dbConnection.closeStatement(p2);

        }
        return result;
    }


    // Functions that I need to write yet.
    public void getAllTransactionDetails() throws SQLException {
        // Need to write this

        // This will return a very big object.
    }

    public void getAllUserLogs(String username) throws SQLException{

    }
}