package server;

import java.sql.*;
import java.util.*;


/**
 * Database is a class that specifies the interface to the movie
 * database. Uses JDBC.
 */
public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Create the database interface object. Connection to the
     * database is performed later.
     */
    public Database() {
        conn = null;
    }

    /**
     * Open a connection to the database, using the specified user
     * name and password.
     */
    public boolean openConnection(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the connection to the database has been established
     * 
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }

//    public boolean userExists(String userName) {
//    	PreparedStatement ps = null;
//    	ResultSet rs = null;
//        try {
//            String sql =
//                "SELECT *\n" +
//                "FROM   users\n" +
//                "WHERE  username = ?";
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, userName);
//            rs = ps.executeQuery();
//            if (rs.next()){
//            	return true;
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//        	closePs(ps, rs);
//        }
//        return false;
//    }
}
