package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Scanner;

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
	private KeyStore ks;
	private KeyStore ts;
	private KeyManagerFactory kmf;
	private String host;
	private String port;
	private boolean connected;
	private SSLSocket socket;
	private BufferedReader in;
	private Scanner s;

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
			System.out.println("\nsocket before handshake:\n" + socket + "\n");

			/*
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why there is
			 * a forced handshake here when using PrintWriters.
			 */
			socket.startHandshake();

			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			System.out.println(
					"certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");

			String issuer = cert.getIssuerDN().getName();
			System.out.println("issuer on certificate received from server:\n" + issuer + "\n");

			System.out.println("serialnumber on certificate received from server:\n" + cert.getSerialNumber() + "\n");

			System.out.println("socket after handshake:\n" + socket + "\n");
			System.out.println("secure connection established\n\n");
			connected = true;
			s = new Scanner(System.in);
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

	public void printMenu() {
		System.out.println("Service selector");
		System.out.println("1: List records \n2: Create new record \n3: Edit record \n0: Exit");

		// Ändra tillbaka till console.in om detta inte funkar i terminalen
		String option = s.nextLine();
		StringBuilder sb = new StringBuilder("menu");
		switch (option) {
		case "1":
			try {
				sb.append("1");
				sendMessage(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "2":
			try {
				sb.append("2");
				sendMessage(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "3":
			try {
				sb.append("3");
				sendMessage(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	public void sendMessage(String message) throws IOException {

			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println(message);
			out.flush();
		
	}
	
	private void sendMessage() throws IOException{
		String option = s.nextLine();
		while (option.isEmpty()){
			option = s.nextLine();
		}
		sendMessage(option);


	}
	
	public void receiveMessage() {
		try {
			String message = in.readLine();
			if(message.equals("done")){
				sendMessage();
			} else if(message.equals("plsinput")){
				sendMessage();
			} else if (message.equals("plsmenu")){
				printMenu();
			} else {
				System.out.println(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
// PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
// BufferedReader in = new BufferedReader(new
// InputStreamReader(socket.getInputStream()));
// String msg;
// for (;;) {
// printMenu();
// System.out.print(">");
// msg = read.readLine();
//
// switch (msg){
// case "1":
// System.out.print("Input the string you want backwards");
// System.out.print(">");
// msg = read.readLine();
// System.out.print("sending '" + msg + "' to server...");
// out.println(msg);
// out.flush();
// System.out.println("done");
// System.out.println("received '" + in.readLine() + "' from server\n");
// break;
// case "0":
// in.close();
// out.close();
// read.close();
// socket.close();
// }
// }
//
// }
// }

// static void printMenu(){
// System.out.println("Choose by entering a number");
// System.out.println("1. Your input will be repeated to you backwards");
// System.out.println("0. Exit");
// }
//
// }
