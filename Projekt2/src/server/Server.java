package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.cert.X509Certificate;

public class Server implements Runnable {
	private ServerSocket serverSocket = null;
	private static int numConnectedClients = 0;
	private Database db;
	private Logger log;

	public Server(ServerSocket ss, Database db) throws IOException, SQLException {
		serverSocket = ss;
		this.db = db;
		startDb();
		newListener();
		log = new Logger();

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

	/**
	 * msg is an array where the first index chooses what method to call in the
	 * server. Rest of fields are data to be used by other methods.
	 * 
	 * @param out
	 * @param msg
	 *            String array
	 * @param currentUserId
	 * @param title
	 * @throws IOException
	 * @throws SQLException
	 */
	private void receivedMsg(PrintWriter out, String msg, int currentUserId, String title)
			throws IOException, SQLException {
		String[] message = msg.split(";");
		String messageToSend = null;
		switch (message[0]) {
		case "menu":
			messageToSend = handleMenu(message, currentUserId, title);
			break;
		case "addRecord":
			if (!title.equals("doctor")) {
				messageToSend = "You do not have permission to add records";
			} else {
				messageToSend = addRecord(message, currentUserId);
			}
			break;
		case "editRecord":
			messageToSend = editRecord(currentUserId, message, title);
			break;
		case "deleteRecord":
			messageToSend = deleteRecord(message, title);
			break;
		}
		if (messageToSend != null) {
			out.println(messageToSend);
			out.println("done");
			out.flush();
		}
	}

	/**
	 * 
	 * @param message
	 * @param id
	 * @param title
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private String handleMenu(String[] message, int id, String title) throws SQLException, IOException {
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
							log.newEditEntry(title + " " + id, "read record with id: " + mr.getRecordId());
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
		StringBuilder sb = new StringBuilder("The records you can read are\nRecord id\tPatient name \n");
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

	/**
	 * Takes data in form of a string array with the following data. Empty
	 * fields wont be edited 0: Action to be performed (Not used in this method)
	 * 1: Id of the patient 2: NurseId for the record 3: Division for the record
	 * 4: Disease for the record
	 * 
	 * @param message
	 *            A string array with data to make a record of.
	 * @param id
	 *            Id of the doctor creating the record
	 * @return String with the added data.
	 * @throws SQLException
	 * @throws IOException
	 */
	private String addRecord(String[] message, int id) throws SQLException, IOException {
		String messageToSend = null;
		String patientId = message[1];
		String doctorId = String.valueOf(id);
		String nurseId = message[2];
		String division = message[3];
		String disease = message[4];

		if (db.getDoctorPatients(id).contains(Integer.parseInt(patientId))) {
			MedicalRecord mr = new MedicalRecord(patientId, doctorId, nurseId, division, disease);
			db.addMedicalRecord(mr);
			String patientName = db.getPatientName(Integer.parseInt(patientId));
			String doctorName = db.getDoctorName(id);
			String nurseName = db.getNurseName(Integer.parseInt(nurseId));

			messageToSend = ("You added the following record\nPatient id: " + patientId + "\tPatient name: "
					+ patientName + "\nDoctor id: " + id + "\tDoctor name: " + doctorName + "\nNurse id: " + nurseId
					+ "\tNurse name: " + nurseName + "\nDivision: " + division + "\nDisease: " + disease);
			;
			log.newEditEntry("doctor" + " " + doctorId, "added a new record for patient with id: " + patientId);
		} else {
			System.out.println(doctorId);
			log.newEditEntry("doctor" + " " + doctorId, "tried to add a journal without permission for patient with id: " + patientId);
			messageToSend = "This is not your patient";
		}
		return messageToSend;
	}

	/**
	 * Takes data in form of a string array with the following data. Empty
	 * fields wont be edited 0: Action to be performed (Not used in this method)
	 * 1: Id of the record 2: NurseId to change to 3: Division to change to 4:
	 * Disease to change to
	 * 
	 * If currently logged in user is ot authorized to change record, nothing
	 * happens.
	 * 
	 * @param message
	 *            A string array with data to make a record of.
	 * @param id
	 *            Id of the doctor creating the record
	 * @return String with the added data.
	 * @throws SQLException
	 * @throws IOException
	 */
	private String editRecord(int editorsId, String[] message, String title) throws SQLException, IOException {
		String recordId = message[1];
		String nurseId = message[2];
		String division = message[3];
		String disease = message[4];
		StringBuilder edited = new StringBuilder();
		for (MedicalRecord mr : getWriteableRecords(title, editorsId)) {
			if (mr.getRecordId() == Integer.parseInt(recordId)) {
				if (!disease.equals("doNotEdit")) {
					mr.setDisease(disease);
					edited.append("disease ");
				}
				if (!division.equals("doNotEdit")) {
					mr.setDivision(division);
					edited.append("division ");
				}
				if (!nurseId.equals("doNotEdit")) {
					mr.setNurseId(Integer.parseInt(nurseId));
					edited.append("nurseId ");
				}
				if (db.updateMedicalRecord(mr)) {
					log.newEditEntry(title + " " + editorsId,
							"edited following content in record with ID " + recordId + ": " + edited.toString());
					return ("The record after the edit\nPatient id: " + mr.getPatientId() + "\tPatient name: "
							+ db.getPatientName(mr.getPatientId()) + "\nDoctor id: " + mr.getDoctorId()
							+ "\tDoctor name: " + db.getDoctorName(mr.getDoctorId()) + "\nNurse id: " + nurseId
							+ "\tNurse name: " + db.getNurseName(mr.getNurseId()) + "\nDivision: " + division
							+ "\nDisease: " + disease);
				} else {
					log.newEditEntry(title + " " + editorsId, "tried to edit journal with ID : " + recordId);
					return "Could not edit record.";
				}

			}
		}
		log.newEditEntry(title + " " + editorsId, "failed, due to lack of permission, to edit record with id: " + recordId);
		return ("Not authorized to edit this record or it doesn't exist");
		
	}

	private String deleteRecord(String[] message, String title) throws SQLException, IOException {
		if (title.equals("government")) {
			String recordId = message[1];
			int rId = Integer.parseInt(recordId);
			if (db.getMedicalRecord(rId) != null && db.deleteMedicalRecord(db.getMedicalRecord(rId))) {
				log.newEditEntry(title, "deleted record with ID " + recordId);
				return ("Record with ID" + recordId + " was deleted");
			} else {
				return ("No record with that ID");
			}
		} else {
			return ("You do not have permission to delete records");
		}
	}
}
