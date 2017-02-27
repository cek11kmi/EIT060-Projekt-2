
PRAGMA foreign_keys = off;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS nurses;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS medical_records;
PRAGMA foreign_keys = on;



CREATE TABLE users(
	serial_number TEXT PRIMARY KEY,
	title TEXT NOT NULL
);

CREATE TABLE nurses(
	nurse_id Integer PRIMARY KEY,
	serial_number TEXT,
	name TEXT NOT NULL,
	division TEXT NOT NULL,
	FOREIGN KEY (serial_number) REFERENCES users(serial_number)
);

CREATE TABLE doctors(
	doctor_id Integer PRIMARY KEY,
	serial_number TEXT,
	name TEXT NOT NULL,
	division TEXT NOT NULL,
	FOREIGN KEY (serial_number) REFERENCES users(serial_number)
);


CREATE TABLE patients(
	patient_id Integer PRIMARY KEY,
	serial_number TEXT,
	doctor_id int,
	name TEXT NOT NULL,
	FOREIGN KEY (serial_number) REFERENCES users(serial_number),
	FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);
	

CREATE TABLE medical_records(
	record_id Integer PRIMARY KEY,
	patient_id int,
	doctor_id int,
	nurse_id int,
	division TEXT,
	disease TEXT,
	FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
	FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
	FOREIGN KEY (nurse_id) REFERENCES nurses(nurse_id)
);


INSERT INTO users(serial_number, title)
VALUES ('10962565134263569953', 'Doctor'),
	('10962565134263569954','Doctor'),
	('10962565134263569950','Nurse'),
	('10962565134263569951', 'Nurse'),
	('10962565134263569952', 'Nurse'),
	('10962565134263569946', 'Patient'),
	('10962565134263569947', 'Patient'),
	('10962565134263569948', 'Patient'),
	('10962565134263569949', 'Patient'),
	('10962565134263569955', 'Government');

INSERT INTO doctors(serial_number, name, division)
VALUES ('10962565134263569953', 'Dr. Kosmos', 'General Health'),
	('10962565134263569954', 'Dr. Dre', 'Emergency Care');

INSERT INTO nurses(serial_number, name, division)
VALUES	('10962565134263569950', 'Nurse House', 'General Health'),
	('10962565134263569951', 'Nurse Snuggles', 'General Health'),
	('10962565134263569952', 'Nurse Trump', 'Emergency Care');

INSERT INTO patients(serial_number, doctor_id, name) 
VALUES ('10962565134263569946', '1', 'Dolan Trupm'),
	('10962565134263569947', '2', 'Pepe Frog'),
	('10962565134263569948', '1', 'Robbie Rotten'),
	('10962565134263569949', '2', 'Tengil');
	
	
INSERT INTO medical_records(patient_id, doctor_id, nurse_id, division, disease)
VALUES ('1','1','1','General Health', 'Hypochondriasis'),
	('2','2','3','Emergency Care', 'Influenza'),
	('3','1','2','General Health', 'Hubris'),
	('4','2','3','Emergency Care', '4th-degree burns');



	
