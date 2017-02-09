package client;

import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import java.security.KeyStore;
import java.security.cert.*;

public class ClientMain {

	public static void main(String[] args) throws IOException {

		String host = "";
		String portString = "";
		Console cons = System.console();
		if (args.length == 2) {
			host = args[0];
			portString = args[1];
		} else if (cons != null){
			// Input host and port number
			System.out.println("Please input the host and port");

			System.out.print("Host: ");
			host = cons.readLine();

			System.out.print("Port: ");
			portString = cons.readLine();

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
		boolean fileFound = false;
		InputStream is = null;
		while (!fileFound) {
			System.out.println("Please insert name of certificate you would like to use: ");
			String certName = cons.readLine();
			try {
				is = new FileInputStream("../clientassets/keystores/" + certName);
				fileFound = true;
			} catch (FileNotFoundException ex) {
				System.out.println("File not found! Please try again.");
			}
		}

		// password input for the keystore
		System.out.println("Please insert password: ");
		char[] ksPW = cons.readPassword();

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
			System.out.println("Service selectör");
			System.out.println("1: Read record \n2: Edit record \n3: Add to record \n4: \n0: Exit");
			String option = cons.readLine();
			switch (option) {
			case "1":
				System.out.println("Can't read right now");
				break;
			case "2":
				System.out.println("Can't edit right now");
				break;
			case "3":
				System.out.println("Can't do shit right now.");
				break;
			case "0":
				System.exit(0);
				break;
			default:
				System.out.println("No such option.\n");
				break;
			}
		}
	}

}
