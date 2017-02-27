package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;

public class ClientMain {

	public static void main(String[] args) throws IOException {

		String host = "";
		String portString = "";
		String certName = "clientassets/keystores/";
		char[] ksPW = new char[0];
		Scanner scan = new Scanner(System.in);
		if (args.length == 4) {
			host = args[0];
			portString = args[1];
			certName = args[2];
			ksPW = args[3].toCharArray();
			// Input host and port number
		} else if (args.length == 0) {
			System.out.println("Please input the host and port");

			System.out.print("Host: ");
			host = scan.nextLine();

			System.out.print("Port: ");
			portString = scan.nextLine();

		} else {
			System.out.println("Start with ClientMain host port");
			System.exit(0);
		}
		// this will convert string port to int
		int port;
		try {
			port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException();
		}
		System.out.println("Connecting to: " + host + " " + port);

		// input the name of the keystore you want to use, this checks if it
		// exists
		InputStream is = null;
		boolean fileFound = false;
		while (!fileFound) {
			if (certName.equals("clientassets/keystores/")) {
				System.out.println("Please specify the path to your keystore: ");
				certName = certName.concat(scan.nextLine());

			}
			try {
				is = new FileInputStream(certName);
				fileFound = true;
			} catch (FileNotFoundException ex) {
				certName = "clientassets/keystores/";
				System.out.println(certName);
				// System.out.println("File not found! Please try again.");
			}
		}

		// password input for the keystore
		if (ksPW.length == 0) {
			System.out.println("Please insert password: ");
			ksPW = scan.nextLine().toCharArray();
		}
		// this loads the keystore with the password provided
		KeyStore ks;
		try {
			ks = KeyStore.getInstance("JKS");
			ks.load(is, ksPW);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		// this part establishes a connection via the client
		Client client = new Client();
		KeyManagerFactory kmf;
		try {
			kmf = KeyManagerFactory.getInstance("SunX509");
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		try {
			kmf.init(ks, ksPW);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

		if (!(client.initConnection(kmf, host, port))) {
			System.out.println("Connection failed!");
		}

		while (client.isConnected()) {
			client.printMenu();

		}
	}

}
