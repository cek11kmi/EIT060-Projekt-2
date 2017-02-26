package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Logger {
	private String patientName;
	private File file;
	private FileOutputStream output;

	/**
	 * Logs changes to a file in the same folder as serverMain with the name of the patient.
	 * @param recordPath
	 * @throws FileNotFoundException
	 */
	public Logger(String patientName) throws FileNotFoundException {
		this.patientName = patientName;
		this.file = new File(patientName);
		output = new FileOutputStream(file, true);
	}
	public Logger() throws FileNotFoundException {
		this.file = new File("log.txt");
		output = new FileOutputStream(file, true);
	}
	
	/**
	 * 
	 * @param actionPerfomer The person responsible for the changes
	 * @param modifiedRecord The record modified
	 * @param modification What was changed in the record
	 * @throws IOException 
	 */
	public void newEditEntry(String actionPerfomer, String modification) throws IOException {
		Date currentDate = new Date();
		String s = currentDate.toString();
		s += "\t" + actionPerfomer + "\t" + modification +"\n";
		output.write(s.getBytes());
	}

	public void shutdown() throws IOException {
		output.close();
	}
	
	public String getLoggedPatient(){
		return patientName;
	}
}
