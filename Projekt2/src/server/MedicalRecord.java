package server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalRecord {
	private int recordId;
	private int patientId;
	private int doctorId;
	private int nurseId;
	private String division;
	private String disease;

	public MedicalRecord(ResultSet rs) throws SQLException {
		this.recordId = rs.getInt("record_id");
		this.patientId = rs.getInt("patient_id");
		this.doctorId = rs.getInt("doctor_id");
		this.nurseId = rs.getInt("doctor_id");
		this.division = rs.getString("division");
		this.disease = rs.getString("disease");
	}
	
	public MedicalRecord(String patientId, String doctorId, String nurseId, String division, String disease){
		this.patientId = Integer.parseInt(patientId);
		this.doctorId = Integer.parseInt(doctorId);
		this.nurseId = Integer.parseInt(nurseId);
		this.division = division;
		this.disease = disease;
	}
	public MedicalRecord(){
		this.patientId = 1;
		this.doctorId = 1;
		this.nurseId = 1;
		this.division = "division";
		this.disease = "disease";
	}

	public int getRecordId(){
		return recordId;
	}

	public int getPatientId(){
		return patientId;
	}

	public int getDoctorId() {
		return doctorId;
	}

	public int getNurseId() {
		return nurseId;
	}

	public String getDivision() {
		return division;
	}

	public String getDisease() {
		return disease;
	}
	
	public void setNurseId(int nurseId){
		this.nurseId = nurseId;
	}
	
	public void setDivision(String division){
		this.division = division;
	}
	
	public void setDisease(String disease){
		this.disease = disease;
	}
	
	@Override
	public String toString() {
		String patient = ("Patient: " + patientId + "\nDoctor: "
				+ doctorId + "\nNurse: " + nurseId + "\nDivision: " + division + "\nDisease: " + disease);
		return patient;
	}
}
