package server;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;


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
    
    public String getNurseDivision(String serialNbr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT division\n" +
                "FROM   nurses\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("division");
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
   
    public String getDoctorDivision(String serialNbr) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT division\n" +
                "FROM   doctors\n" +
                "WHERE  serial_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, serialNbr);
            rs = ps.executeQuery();
            if (rs.next()){
            	return rs.getString("division");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return "";
    }
    
    public List<String> getDoctorPatients(String doctorName) {
    	List<String> patientList = new LinkedList<String>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT name\n" +
                "FROM   doctors\n" +
                "WHERE  doctor_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, doctorName);
            rs = ps.executeQuery();
            while (rs.next()){
            	patientList.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return patientList;
    }
    
    public List<MedicalRecord> getMedicalRecord(String patientName){
    	List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT *\n" +
                "FROM   medical_records\n" +
                "WHERE  doctor_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, patientName);
            rs = ps.executeQuery();
            while (rs.next()){
            	recordsList.add(new MedicalRecord(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return recordsList;
    }
    
    public List<MedicalRecord> getMedicalRecordsByDivision(String division){
    	List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT *\n" +
                "FROM   medical_records\n" +
                "WHERE  division = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, division);
            rs = ps.executeQuery();
            while (rs.next()){
            	recordsList.add(new MedicalRecord(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return recordsList;
    }
    
    public List<MedicalRecord> getMedicalRecordsByDivisionAndDoctor(String division, String doctorName){
    	List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT *\n" +
                "FROM   medical_records\n" +
                "WHERE  division = ?\n" +
                "UNION \n" +
                "SELECT *\n" +
                "FROM medical_records\n" +
                "WHERE doctor_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, division);
            ps.setString(2, doctorName);
            rs = ps.executeQuery();
            while (rs.next()){
            	recordsList.add(new MedicalRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return recordsList;
    }
    
    public List<MedicalRecord> getMedicalRecordsByDivisionAndNurse(String division, String nurseName){
    	List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "SELECT *\n" +
                "FROM   medical_records\n" +
                "WHERE  division = ?\n" +
                "UNION \n" +
                "SELECT *\n" +
                "FROM medical_records\n" +
                "WHERE nurse_name = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, division);
            ps.setString(2, nurseName);
            rs = ps.executeQuery();
            while (rs.next()){
            	recordsList.add(new MedicalRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	closePs(ps, rs);
        }
        return recordsList;
    }
    
    public boolean addMedicalRecord(MedicalRecord mr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "INSERT INTO medical_records(patient_name, doctor_name, nurse_name, division, disease)\n" +
                "VALUES(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, mr.getPatientName());
            ps.setString(2, mr.getDoctorName());
            ps.setString(3, mr.getNurseName());
            ps.setString(4, mr.getDivision());
            ps.setString(5, mr.getDisease());
            ps.executeQuery();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
        	closePs(ps, rs);
        }
    }
    
    public boolean deleteMedicalRecord(MedicalRecord mr){
    	PreparedStatement ps = null;
    	ResultSet rs = null;
        try {
            String sql =
                "DELETE FROM medical_records\n" +
                "WHERE record_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, mr.getRecordId());
            ps.executeQuery();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
        	closePs(ps, rs);
        }
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
