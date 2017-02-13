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

public class Server implements Runnable {
	private ServerSocket serverSocket = null;
	private static int numConnectedClients = 0;
	private Database db;
	

	public Server(ServerSocket ss, Database db) throws IOException {
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
			
			//Authenticates the client by comparing certificate 
			//serial number with entries in database
			//if title is not an empty string the user exists in the database
			String title = authentication(serialNbrString);
			
			
			if (title.equals("")) {
				System.out.println("client not authenticated");
			} else {
				
				System.out.println("client authenticated");	
				System.out.println("client title is " + title);
				
				//retrieves the users name from the database
				String name = "";
				int id = 0;
				switch (title) {
				case "Patient": 
					id = db.getPatientId(serialNbrString); 
					name = db.getPatientName(id);
					break;
				case "Nurse":
					id = db.getNurseId(serialNbrString);
					name = db.getNurseName(id);
					break;
				case "Doctor":
					id = db.getDoctorId(serialNbrString);
					name = db.getDoctorName(id);
					break;
				case "Government":
					System.out.println("client is government");
				}
				if (!name.equals("")){
					System.out.println("client name is " + name);
				}

				System.out.println("client name (cert subject DN field): " + subject);
				String issuer = cert.getIssuerDN().getName();
				System.out.println("issuer on certificate received from client:\n" + issuer + "\n");
				System.out
						.println("serialnumber on certificate received from client:\n" + cert.getSerialNumber() + "\n");
				System.out.println(numConnectedClients + " concurrent connection(s)\n");

				PrintWriter out = null;
				BufferedReader in = null;
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String clientMsg = null;
				while ((clientMsg = in.readLine()) != null) {
					receivedMsg(in, out, clientMsg, id, title);
					
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
		}
	}
	
	private List<MedicalRecord> getReadableRecords(String title, int id){
		title = title.toLowerCase();
		System.out.println(title);
		List<MedicalRecord> recordList = new ArrayList<MedicalRecord>();
		if(title.equals("patient")){
			recordList = db.getMedicalRecord(id);
		} else if(title.equals("doctor")){
			recordList = db.getMedicalRecordsByDivisionAndDoctor(db.getDoctorDivision(id), id);
		} else if(title.equals("nurse")){
			recordList = db.getMedicalRecordsByDivisionAndNurse(db.getNurseDivision(id), id);
		}  else if (title.equals("government")){
			recordList = db.getMedicalRecords();
		}
		return recordList;
	}

	private void newListener() {
		(new Thread(this)).start();
	} // calls run()

	private void startDb() {
		if (db.openConnection("serverassets/db/hospital.db")) {
			System.out.println("Database connected");
		} else {
			System.out.println("Database not connected");
			System.exit(0);
		}

	}

	private String authentication(String serialNbrString) {
		if (!(serialNbrString.equals(""))) {
			return db.getUserTitle(serialNbrString);
		} else {
			System.out.println("Error retrieving cerificate serial number");
			return "";
		}
	}
	
	private void receivedMsg(BufferedReader in, PrintWriter out, String msg, int id, String title){
		switch (msg) {
		case "1":
			printRecords(out, id, title);
			break;
		case "2":
			addRecord(in, out, id, title);
			break;
		} 
		


	}
	
	private void printRecords(PrintWriter out, int id, String title){
		List<MedicalRecord> recordList = getReadableRecords(title, id);
		out.println(title + " " + id);
		for (MedicalRecord mr : recordList){
			out.println("Id: " + mr.getRecordId() + " Namn: " + db.getPatientName(mr.getPatientId()));
			out.flush();
		}
	}
	
	private void addRecord(BufferedReader in, PrintWriter out, int id, String title){
		String patientId = "";
		out.println("Patient id: ");
		try {
			patientId = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Patient id is " + patientId + " Patient name is " + db.getPatientName(Integer.parseInt(patientId)));
	}
}
