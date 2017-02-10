package server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.KeyStore;
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

			if (!(authentication(cert.getSerialNumber()))) {
				System.out.println("client not authenticated");
			} else {
				System.out.println("client authenticated");

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
					String rev = new StringBuilder(clientMsg).reverse().toString();
					System.out.println("received '" + clientMsg + "' from client");
					System.out.print("sending '" + rev + "' to client...");
					out.println(rev);
					out.flush();
					System.out.println("done\n");
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

	private boolean authentication(BigInteger serialNbr) {
		String serialNbrString = "";
		serialNbrString = serialNbr.toString();
		if (!(serialNbr.equals(""))) {
			return db.userExists(serialNbrString);
		} else {
			System.out.println("Error retrieving cerificate serial number");
			return false;
		}
	}
}
