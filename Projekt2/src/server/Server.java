package server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.KeyStore;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import org.sqlite.SQLiteException;

public class Server implements Runnable {
	private ServerSocket serverSocket = null;
	private static int numConnectedClients = 0;
	private Database db;

	public Server(ServerSocket ss, Database db) throws IOException, SQLException {
		serverSocket = ss;
		this.db = db;
		startDb();
		newListener();

	}

	public void run() {
		try {
			SSLSocket socket = (SSLSocket) serverSocket.accept();
			newListener();
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			numConnectedClients++;

			System.out.println("client connected");
			String serialNbrString = cert.getSerialNumber().toString();

			// Authenticates the client by comparing certificate
			// serial number with entries in database
			// if title is not an empty string the user exists in the database
			String title = authentication(serialNbrString);

			if (title.equals("")) {
				System.out.println("client not authenticated");
			} else {

				System.out.println("client authenticated");
				System.out.println("client title is " + title);
				title = title.toLowerCase();

				// retrieves the users name from the database
				String name = "";
				int id = 0;
				switch (title) {
				case "patient":
					id = db.getPatientId(serialNbrString);
					name = db.getPatientName(id);
					break;
				case "nurse":
					id = db.getNurseId(serialNbrString);
					name = db.getNurseName(id);
					break;
				case "doctor":
					id = db.getDoctorId(serialNbrString);
					name = db.getDoctorName(id);
					break;
				case "government":
					System.out.println("client is government");
				}
				if (!name.equals("")) {
					System.out.println("client name is " + name);
				}

				// System.out.println("client name (cert subject DN field): " +
				// subject);
				String issuer = cert.getIssuerDN().getName();
				// System.out.println("issuer on certificate received from
				// client:\n" + issuer + "\n");
				// System.out
				// .println("serialnumber on certificate received from
				// client:\n" + cert.getSerialNumber() + "\n");
				System.out.println(numConnectedClients + " concurrent connection(s)\n");

				PrintWriter out = null;
				BufferedReader in = null;
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String clientMsg = null;
				while ((clientMsg = in.readLine()) != null) {
					receivedMsg(out, clientMsg, id, title);
					System.out.println("Received: " + clientMsg + " from client");
				}
				in.close();
				out.close();
			}
			socket.close();
			numConnectedClients--;
			System.out.println("client disconnected");
			System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
			System.out.println("Client died: " + e.getMessage());
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// EJ FÄRDIG ALLS
	private String editRecord(int editorsId, String[] message, String title) throws SQLException {
		String messageToSend = null;
		String recordId = message[1];
		String nurseId = message[2];
		String division = message[3];
		String disease = message[4];
		for (MedicalRecord mr : getWriteableRecords(title, editorsId)) {
			if (mr.getRecordId() == Integer.parseInt(recordId)) {
				if(!disease.isEmpty()){					
					mr.setDisease(disease);
				}
				if(!division.isEmpty()){					
					mr.setDivision(division);
				}
				if(!nurseId.isEmpty()){					
					mr.setNurseId(Integer.parseInt(nurseId));
				}
				if(db.updateMedicalRecord(mr)){
					return ("You added the following record\nPatient id: " + mr.getPatientId() + "\tPatient name: "
							+ db.getPatientName(mr.getPatientId()) + "\nDoctor id: " + mr.getDoctorId() + "\tDoctor name: " + db.getDoctorName(mr.getDoctorId()) + "\nNurse id: " + nurseId
							+ "\tNurse name: " + db.getNurseName(mr.getNurseId()) + "\nDivision: " + division + "\nDisease: " + disease);				
				} else {
					return "Could not edit record.";
				}
				
			} 
		}
		return ("Not authorized to edit this record or it doesn't exist");
		
		
	}

	private void newListener() {
		(new Thread(this)).start();
	} // calls run()

	private void startDb() throws SQLException {
		if (db.openConnection("serverassets/db/hospital.db")) {
			System.out.println("Database connected");
			db.foreignKey();
		} else {
			System.out.println("Database not connected");
			System.exit(0);
		}
	}

	private String authentication(String serialNbrString) throws SQLException {
		if (!(serialNbrString.equals(""))) {
			return db.getUserTitle(serialNbrString);
		} else {
			System.out.println("Error retrieving cerificate serial number");
			return "";
		}
	}

	private void receivedMsg(PrintWriter out, String msg, int id, String title) throws IOException, SQLException {
		String[] message = msg.split(";");
		String messageToSend = null;
		switch (message[0]) {
		case "menu":
			messageToSend = handleMenu(message, id, title);
			break;
		case "addRecord":
			if (!title.equals("doctor")) {
				messageToSend = "You do not have permission to add records";
			} else {
				messageToSend = addRecord(message, id);
			}
			break;
		case "editRecord":

			messageToSend = editRecord(id, message, title);
		}
		if (messageToSend != null) {
			out.println(messageToSend);
			out.println("done");
			out.flush();
		}
	}

	private String handleMenu(String[] message, int id, String title) throws SQLException {
		StringBuilder sb = new StringBuilder();
		String messageToSend = null;
		switch (message[1]) {
		case "1":
			if (message.length > 2) {
				if (message[2].equals("id")) {
					int enteredId = Integer.parseInt(message[3]);
					boolean found = false;
					for (MedicalRecord mr : getReadableRecords(title, id)) {
						if (mr.getRecordId() == enteredId) {
							found = true;
							sb.append("Patient id: " + mr.getPatientId() + "\tPatient name: "
									+ db.getPatientName(mr.getPatientId()) + "\nDoctor id: " + mr.getDoctorId()
									+ "\tDoctor name: " + db.getDoctorName(mr.getDoctorId()) + "\nNurse id: "
									+ mr.getNurseId() + "\tNurse name: " + db.getNurseName(mr.getNurseId())
									+ "\nDivision: " + mr.getDivision() + "\nDisease: " + mr.getDisease());
							break;
						}
					}
					if (!found) {
						sb.append("Invalid record id entered");
					}
				}
			} else {
				sb.append(printRecords(id, title));
				sb.append("\nEnter an id to read more\n");
			}
			messageToSend = sb.toString();
			break;
		case "2":
			break;
		default:
			messageToSend = "Unknown option";
			break;
		}
		return messageToSend;

	}

	private String printRecords(int id, String title) throws SQLException {
		List<MedicalRecord> recordList = getReadableRecords(title, id);
		StringBuilder sb = new StringBuilder("The records you can read are\nRecord id\tPatient name\n");
		for (MedicalRecord mr : recordList) {
			sb.append(mr.getRecordId() + "\t\t" + db.getPatientName(mr.getPatientId()) + "\n");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		return sb.toString();
	}

	private List<MedicalRecord> getReadableRecords(String title, int id) throws SQLException {
		title = title.toLowerCase();
		List<MedicalRecord> recordList = new ArrayList<MedicalRecord>();
		if (title.equals("patient")) {
			recordList = db.getMedicalRecordsByPatient(id);
		} else if (title.equals("doctor")) {
			recordList = db.getMedicalRecordsByDivisionAndDoctor(db.getDoctorDivision(id), id);
		} else if (title.equals("nurse")) {
			recordList = db.getMedicalRecordsByDivisionAndNurse(db.getNurseDivision(id), id);
		} else if (title.equals("government")) {
			recordList = db.getMedicalRecords();
		}
		return recordList;
	}

	private List<MedicalRecord> getWriteableRecords(String title, int id) throws SQLException {
		title = title.toLowerCase();
		List<MedicalRecord> recordList = new ArrayList<MedicalRecord>();
		if (title.equals("patient")) {
		} else if (title.equals("doctor")) {
			recordList = db.getMedicalRecordsByDivisionAndDoctor(db.getDoctorDivision(id), id);
		} else if (title.equals("nurse")) {
			recordList = db.getMedicalRecordsByDivisionAndNurse(db.getNurseDivision(id), id);
		} else if (title.equals("government")) {
			recordList = db.getMedicalRecords();
		}
		return recordList;
	}

	private String addRecord(String[] message, int id) throws SQLException {
		String messageToSend = null;
		String patientId = message[1];
		String doctorId = String.valueOf(id);
		String nurseId = message[2];
		String division = message[3];
		String disease = message[4];

		if (db.getDoctorPatients(id).contains(patientId)) {
			MedicalRecord mr = new MedicalRecord(patientId, doctorId, nurseId, division, disease);
			db.addMedicalRecord(mr);
			String patientName = db.getPatientName(Integer.parseInt(patientId));
			String doctorName = db.getDoctorName(id);
			String nurseName = db.getNurseName(Integer.parseInt(nurseId));
			messageToSend = ("You added the following record\nPatient id: " + patientId + "\tPatient name: "
					+ patientName + "\nDoctor id: " + id + "\tDoctor name: " + doctorName + "\nNurse id: " + nurseId
					+ "\tNurse name: " + nurseName + "\nDivision: " + division + "\nDisease: " + disease);
			;
		} else {
			messageToSend = "This is not your patient";
		}
		return messageToSend;
	}

}
