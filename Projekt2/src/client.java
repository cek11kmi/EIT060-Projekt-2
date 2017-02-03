import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.Scanner;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class client {

    public static void main(String[] args) throws Exception {
        String host = null;
        int port = -1;
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }
        if (args.length < 2) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }
        try { /* get input parameters */
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }

        try { /* set up a key manager for client authentication */
            SSLSocketFactory factory = null;
            Scanner s = new Scanner(System.in);
            StringBuilder sb = new StringBuilder();
            String ksname;
            try {
                char[] password = "password".toCharArray();
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                SSLContext ctx = SSLContext.getInstance("TLS");
                
                System.out.print("Namn: ");
                ksname = s.nextLine();
                sb.append(ksname + "keystore");
                ksname = sb.toString();
                System.out.print("Lösenord: ");
                String passwordIn = s.nextLine();

                char[] passwordInArray = passwordIn.toCharArray();
                ks.load(new FileInputStream(ksname), passwordInArray);  // keystore password (storepass)
				ts.load(new FileInputStream("clienttruststore"), password); // truststore password (storepass);
				
				kmf.init(ks, passwordInArray); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
            System.out.println("\nsocket before handshake:\n" + socket + "\n");

            /*
             * send http request
             *
             * See SSLSocketClient.java for more information about why
             * there is a forced handshake here when using PrintWriters.
             */
            socket.startHandshake();

            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
            
            String issuer = cert.getIssuerDN().getName();
            System.out.println("issuer on certificate received from server:\n" + issuer + "\n");
 
            System.out.println("serialnumber on certificate received from server:\n" + cert.getSerialNumber() + "\n");
            
            System.out.println("socket after handshake:\n" + socket + "\n");
            System.out.println("secure connection established\n\n");
            
            System.out.println("Welcome " + ksname);

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
			for (;;) {
	            printMenu();
                System.out.print(">");
                msg = read.readLine();
                
                switch (msg){
                case "1":
                	System.out.print("Input the string you want backwards");
                	System.out.print(">");
                	msg = read.readLine();
                    System.out.print("sending '" + msg + "' to server...");
                    out.println(msg);
                    out.flush();
                    System.out.println("done");
                    System.out.println("received '" + in.readLine() + "' from server\n");
                    break;
                case "0": 
                	in.close();
        			out.close();
        			read.close();
                    socket.close();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void printMenu(){
    	System.out.println("Choose by entering a number");
    	System.out.println("1. Your input will be repeated to you backwards");
    	System.out.println("0. Exit");
    }

}
