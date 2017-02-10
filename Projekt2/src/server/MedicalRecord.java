package server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalRecord {
	private int recordId;
	private String patientName;
	private String doctorName;
	private String nurseName;
	private String division;
	private String disease;

	public MedicalRecord(ResultSet rs) throws SQLException {
		this.recordId = rs.getInt("record_id");
		this.patientName = rs.getString("patient_name");
		this.doctorName = rs.getString("doctor_name");
		this.nurseName = rs.getString("doctor_name");
		this.division = rs.getString("division");
		this.disease = rs.getString("disease");
	}
	
	public MedicalRecord(){
		this.patientName = "patient_name";
		this.doctorName = "doctor_name";
		this.nurseName = "doctor_name";
		this.division = "division";
		this.disease = "disease";
	}

	public int getRecordId(){
		return recordId;
	}


	public String getPatientName(){
		return patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public String getNurseName() {
		return nurseName;
	}

	public String getDivision() {
		return division;
	}

	public String getDisease() {
		return disease;
	}

	@Override
	public String toString() {
		String patient = patientName + "\nDoctor: "
				+ doctorName + "\nNurse: " + nurseName + "\nDivision: " + division + "\nDisease: " + disease;
		return patient;
	}
}
