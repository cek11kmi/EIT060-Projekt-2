package main;

import java.io.IOException;

import client.ClientMain;
import server.ServerMain;

public class Main {

	public static void main(String[] args) throws IOException {
		String port = "";
		if (args.length > 0) {
			port = args[0];
			String keyStore = args[1];
			String pw = args[2];
			String[] start = { "localhost", port, keyStore, pw };
			String[] smS = { port };
			ServerMain.main(smS);
			ClientMain.main(start);

		} else {
			port = "9872";
			String[] start = { "localhost", port, "nurse_1_keystore", "password" };
			String[] smS = { port };
			ServerMain.main(smS);
			ClientMain.main(start);
		}

	}

}
