package server;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC.
 */
public class Database {

	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
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

	public String getUserTitle(String serialNbr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT title\n" + "FROM   users\n" + "WHERE  serial_number = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, serialNbr);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("title");
		}
		closePs(ps, rs);
		return "";
	}

	public int getPatientId(String serialNbr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT patient_id\n" + "FROM   patients\n" + "WHERE  serial_number = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, serialNbr);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt("patient_id");
		}
		closePs(ps, rs);
		return 0;
	}

	public int getNurseId(String serialNbr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT nurse_id\n" + "FROM   nurses\n" + "WHERE  serial_number = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, serialNbr);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt("nurse_id");
		}
		closePs(ps, rs);
		return 0;
	}

	public String getNurseDivision(int nurseId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT division\n" + "FROM   nurses\n" + "WHERE  nurse_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, nurseId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("division");
		}
		closePs(ps, rs);
		return "";
	}

	public int getDoctorId(String serialNbr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT doctor_id\n" + "FROM   doctors\n" + "WHERE  serial_number = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, serialNbr);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getInt("doctor_id");
		}
		closePs(ps, rs);
		return 0;
	}

	public String getDoctorDivision(int doctorId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT division\n" + "FROM   doctors\n" + "WHERE  doctor_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, doctorId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("division");
		}
		closePs(ps, rs);
		return "";
	}

	public List<Integer> getDoctorPatients(int doctorId) throws SQLException {
		List<Integer> patientList = new LinkedList<Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT patient_id\n" + "FROM   patients\n" + "WHERE  doctor_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, doctorId);
		rs = ps.executeQuery();
		while (rs.next()) {
			patientList.add(rs.getInt("patient_id"));
		}

		closePs(ps, rs);
		return patientList;
	}

	public List<MedicalRecord> getMedicalRecordsByPatient(int patientId) throws SQLException {
		List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM   medical_records\n" + "WHERE  patient_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, patientId);
		rs = ps.executeQuery();
		while (rs.next()) {
			recordsList.add(new MedicalRecord(rs));
		}

		closePs(ps, rs);
		return recordsList;
	}

	public MedicalRecord getMedicalRecord(int recordId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT *\n" + "FROM   medical_records\n" + "WHERE  record_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, recordId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return new MedicalRecord(rs);
		}

		closePs(ps, rs);
		return null;
	}

	public List<MedicalRecord> getMedicalRecordsByDivision(String division) throws SQLException {
		List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM   medical_records\n" + "WHERE  division = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, division);
		rs = ps.executeQuery();
		while (rs.next()) {
			recordsList.add(new MedicalRecord(rs));
		}

		closePs(ps, rs);
		return recordsList;
	}

	public List<MedicalRecord> getMedicalRecordsByDivisionAndDoctor(String division, int doctorId) throws SQLException {
		List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM   medical_records\n" + "WHERE  division = ?\n" + "UNION \n" + "SELECT *\n"
				+ "FROM medical_records\n" + "WHERE doctor_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, division);
		ps.setInt(2, doctorId);
		rs = ps.executeQuery();
		while (rs.next()) {
			recordsList.add(new MedicalRecord(rs));
		}

		closePs(ps, rs);
		return recordsList;
	}

	public List<MedicalRecord> getMedicalRecordsByDivisionAndNurse(String division, int nurseId) throws SQLException {
		List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM   medical_records\n" + "WHERE  division = ?\n" + "UNION \n" + "SELECT *\n"
				+ "FROM medical_records\n" + "WHERE nurse_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, division);
		ps.setInt(2, nurseId);
		rs = ps.executeQuery();
		while (rs.next()) {
			recordsList.add(new MedicalRecord(rs));
		}

		closePs(ps, rs);
		return recordsList;
	}

	public List<MedicalRecord> getMedicalRecords() throws SQLException {
		List<MedicalRecord> recordsList = new LinkedList<MedicalRecord>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *\n" + "FROM   medical_records";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while (rs.next()) {
			recordsList.add(new MedicalRecord(rs));
		}

		closePs(ps, rs);
		return recordsList;
	}

	public String getNurseName(int nurseId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT name\n" + "FROM   nurses\n" + "WHERE  nurse_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, nurseId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("name");
		}

		closePs(ps, rs);
		return "";
	}

	public String getPatientName(int patientId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT name\n" + "FROM   patients\n" + "WHERE  patient_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, patientId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("name");
		}
		closePs(ps, rs);
		return "";
	}

	public String getDoctorName(int doctorId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT name\n" + "FROM   doctors\n" + "WHERE  doctor_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, doctorId);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getString("name");
		}

		closePs(ps, rs);
		return "";
	}

	public void addMedicalRecord(MedicalRecord mr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "INSERT INTO medical_records(patient_id, doctor_id, nurse_id, division, disease)\n"
				+ "VALUES(?,?,?,?,?);";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, mr.getPatientId());
		ps.setInt(2, mr.getDoctorId());
		ps.setInt(3, mr.getNurseId());
		ps.setString(4, mr.getDivision());
		ps.setString(5, mr.getDisease());
		ps.executeUpdate();
		closePs(ps, rs);
	}

	public boolean updateMedicalRecord(MedicalRecord mr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "UPDATE medical_records\n" + "SET nurse_id = ?\n" + "SET division = ?\n" + "SET disease = ?\n"
				+ "WHERE record_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, mr.getNurseId());
		ps.setString(2, mr.getDivision());
		ps.setString(3, mr.getDisease());
		ps.setInt(4, mr.getRecordId());
		if (ps.executeUpdate() == 1) {
			closePs(ps, rs);
			return true;
		}
		closePs(ps, rs);
		return false;
	}

	public boolean deleteMedicalRecord(MedicalRecord mr) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "DELETE FROM medical_records\n" + "WHERE record_id = ?";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, mr.getRecordId());
		if (ps.executeUpdate() == 1) {
			closePs(ps, rs);
			return true;
		}
		closePs(ps, rs);
		return false;

	}

	public void foreignKey() throws SQLException {
		PreparedStatement ps = null;
		try {
			String sql = "PRAGMA foreign_keys = on";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

	private void closePs(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
