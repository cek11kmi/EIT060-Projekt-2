package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class Client {
	private SSLSocketFactory factory;
	private char[] trustStorePW;
	private KeyStore ts;
	private KeyManagerFactory kmf;
	private boolean connected;
	private SSLSocket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Scanner s;
	private String messageToSend;

	public Client() {
		trustStorePW = "password".toCharArray();

	}

	public boolean initConnection(KeyManagerFactory kmf, String host, int port) {
		this.kmf = kmf;
		try { /* set up a key manager for client authentication */
			try {

				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				SSLContext ctx = SSLContext.getInstance("TLS");

				ts = KeyStore.getInstance("JKS");
				ts.load(new FileInputStream("clientassets/clienttruststore"), trustStorePW); // truststore
																								// password
																								// (storepass);
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

				factory = ctx.getSocketFactory();

			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			socket = (SSLSocket) factory.createSocket(host, port);

			/*
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why there is
			 * a forced handshake here when using PrintWriters.
			 */
			socket.startHandshake();
			System.out.println("secure connection established\n\n");
			connected = true;
			s = new Scanner(System.in);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isConnected() {
		return connected;
	}

	public void terminate() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connected = false;
	}

	public void printMenu() throws IOException {
		System.out.println("Service selector");
		System.out.println("1: List records \n2: Create new record \n3: Edit record \n4: Delete record \n0: Exit");

		// Ändra tillbaka till console.in om detta inte funkar i terminalen
		String option = s.nextLine();
		StringBuilder sb = new StringBuilder("menu;");
		switch (option) {
		case "1":
			sb.append("1");
			messageToSend = sb.toString();
			sendMessage();
			receiveMessage();
			readMore();
			break;
		case "2":
			addMedicalRecord();
			receiveMessage();
			break;
		case "3":
			editRecord();
			receiveMessage();
			break;
		case "4":
			deleteMedicalRecord();
			receiveMessage();
			break;
		case "0":
			System.exit(0);
			break;
		default:
			System.out.println("No such option.\n");
			printMenu();
			break;
		}
	}

	private void editRecord() throws IOException {
		System.out.println("Record id: ");
		String recordId = s.nextLine();
		while (!checkParsability(recordId)) {
			System.out.println("Record id: ");
			recordId = s.nextLine();
		}
		System.out.println("Nurse id: ");
		String nurseId = s.nextLine();
		if (!nurseId.isEmpty()){
			while (!checkParsability(nurseId)) {
				System.out.println("Nurse id: ");
				nurseId = s.nextLine();
			}
		} else {
			nurseId = "doNotEdit";
		}
		System.out.println("Division: ");
		String division = s.nextLine();
		if(division.isEmpty()){
			division = "doNotEdit";
		}

		System.out.println("Disease: ");
		String disease = s.nextLine();
		if(disease.isEmpty()){
			disease = "doNotEdit";
		}
		String message = ("editRecord;" + recordId + ";" + nurseId + ";" + division + ";" + disease);
		messageToSend = message;
		sendMessage();

	}

	private void readMore() throws IOException {
		StringBuilder sb = new StringBuilder("menu;1;id;");
		String enteredId = s.nextLine();
		while (!checkParsability(enteredId)) {
			System.out.println("Enter a record id:");
			enteredId = s.nextLine();
		}
		sb.append(enteredId);
		messageToSend = sb.toString();
		sendMessage();
		receiveMessage();
	}

	public void sendMessage() throws IOException {
		// BufferedReader read = new BufferedReader(new
		// InputStreamReader(System.in));
		System.out.println(messageToSend);
		out.println(messageToSend);
	}

	public void receiveMessage() throws IOException {
		System.out.println("");
		String serverMsg = null;
		while (!(serverMsg = in.readLine()).equals("done")) {
			if (!serverMsg.isEmpty()) {
				System.out.println(serverMsg);
			}
		}
		System.out.println("");
	}
	private void deleteMedicalRecord()throws IOException {
		System.out.println("Record id: ");
		String recordId = s.nextLine();
		while (!checkParsability(recordId)) {
			System.out.println("Record id: ");
			recordId = s.nextLine();
		}
		String message = ("deleteRecord;" + recordId);
		messageToSend = message;
		sendMessage();
	}
	/**
	 * This method constructs a string that will be sent to the server with the
	 * info required to create a medical record
	 * 
	 * @throws IOException
	 */
	private void addMedicalRecord() throws IOException {
		System.out.println("Patient id: ");
		String patientId = s.nextLine();
		while (!checkParsability(patientId)) {
			System.out.println("Patient id: ");
			patientId = s.nextLine();
		}
		System.out.println("Nurse id: ");
		String nurseId = s.nextLine();
		while (!checkParsability(nurseId)) {
			System.out.println("Nurse id: ");
			nurseId = s.nextLine();
		}
		System.out.println("Division: ");
		String division = s.nextLine();
		while (division.isEmpty()){
			System.out.println("Please input a division");
			System.out.println("Division: ");
			division = s.nextLine();
		}	

		System.out.println("Disease: ");
		String disease = s.nextLine();
		while (disease.isEmpty()){
			System.out.println("Please input a disease");
			System.out.println("Disease: ");
			disease = s.nextLine();
		}	

		String message = ("addRecord;" + patientId + ";" + nurseId + ";" + division + ";" + disease);
		messageToSend = message;
		sendMessage();
	}

	/**
	 * This method checks if an entered id is able to be converted to an int
	 * 
	 * @param id
	 *            the Id that should be checked
	 * @return true if it's a valid id and false if not
	 */
	private boolean checkParsability(String id) {
		try {
			Integer.parseInt(id);
		} catch (Exception e) {
			System.out.println("Invalid input, please input a valid id");
			return false;
		}
		return true;
	}

}
