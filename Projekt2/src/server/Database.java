package server;

import java.sql.*;


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

    public String getUserTitle(String serialNbr) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT title\n" +
                "FROM   users\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("title");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return "";
    }
    
    public String getPatientName(String serialNbr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT name\n" +
                "FROM   patients\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return "";
    }
    
    public String getNurseName(String serialNbr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT name\n" +
                "FROM   nurses\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return "";
    }
    
    public String getDoctorName(String serialNbr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT name\n" +
                "FROM   doctors\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return "";
    }
   
    
    
    
    
    private void closePs(PreparedStatement ps, ResultSet rs){
    	try {
			if (ps != null){
				ps.close();
			}
			if (rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
