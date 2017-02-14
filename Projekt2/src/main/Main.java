package main;

import java.io.IOException;
import java.sql.SQLException;

import client.ClientMain;
import server.ServerMain;

public class Main {

	public static void main(String[] args) throws IOException, SQLException {
		String port = "";
		if (args.length > 0) {
			port = args[0];
			String keyStore = args[1];
			String pw = args[2];
			String[] start = { "localhost", port, keyStore, pw };
			String[] smS = { port };
			ClientMain.main(start);
			ServerMain.main(smS);

		} else {
			port = "9871";
			String[] start = { "localhost", port, "doctor_1_keystore", "password" };
			String[] smS = { port };
			ServerMain.main(smS);
			ClientMain.main(start);
		}

	}

}
