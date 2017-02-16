package server;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.sql.SQLException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ServerMain {

	public static void main(String[] args) throws SQLException {
		String portString = "9853";
		Console cons = System.console();
		if (args.length == 1) {
			portString = args[0];
		}
		// else if (cons != null){
		// // Input host and port number
		// System.out.println("Please input the port");
		//
		// System.out.print("Port: ");
		// portString = cons.readLine();
		//
		// } else {
		// System.out.println("Start with ServerMain port");
		// //System.exit(0);
		// }

		ServerMain sm = new ServerMain();
		Database db = new Database();
		sm.startUp(portString, db);

	}

	private void startUp(String portString, Database db) throws SQLException {
		// this will convert string port to int
		int port;
		try {
			port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException();
		}
		System.out.println("\nServer Started\n");
		String type = "TLS";
		try {
			ServerSocketFactory ssf = getServerSocketFactory(type);
			ServerSocket ss = ssf.createServerSocket(port);
			((SSLServerSocket) ss).setNeedClientAuth(true); // enables client
															// authentication
			Server s = new Server(ss, db);
		} catch (IOException e) {
			System.out.println("Unable to start Server: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
			try { // set up key manager to perform server authentication
				SSLContext ctx = SSLContext.getInstance("TLS");
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				char[] password = "password".toCharArray();

				ks.load(new FileInputStream("serverassets/serverkeystore"), password); // keystore
																						// password
																						// (storepass)
				ts.load(new FileInputStream("serverassets/servertruststore"), password); // truststore
																							// password
																							// (storepass)
				kmf.init(ks, password); // certificate password (keypass)
				tmf.init(ts); // possible to use keystore as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}

}
