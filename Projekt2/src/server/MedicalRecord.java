package server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalRecord {
	private String patientName;
	private String doctorName;
	private String nurseName;
	private String division;
	private String disease;

	public MedicalRecord(ResultSet rs) throws SQLException {
		this.patientName = rs.getString("patient_name");
		this.doctorName = rs.getString("doctor_name");
		this.nurseName = rs.getString("doctor_name");
		this.division = rs.getString("division");
		this.disease = rs.getString("disease");
	}
	
	public String getPatientName(){
		return patientName;
	}
	
	public String getDoctorName(){
		return doctorName;
	}
	
	public String getNurseName(){
		return nurseName;
	}
	
	public String getDivision(){
		return division;
	}
	
	public String getDisease(){
		return disease;
	}
	
	@Override
	public String toString() {
		return "kul";
	}
}
