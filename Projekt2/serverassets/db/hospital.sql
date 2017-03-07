
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
VALUES ('14594712769675615388', 'Doctor'),
	('14594712769675615389','Doctor'),
	('14594712769675615385','Nurse'),
	('14594712769675615386', 'Nurse'),
	('14594712769675615387', 'Nurse'),
	('14594712769675615381', 'Patient'),
	('14594712769675615382', 'Patient'),
	('14594712769675615383', 'Patient'),
	('14594712769675615384', 'Patient'),
	('14594712769675615390', 'Government');

INSERT INTO doctors(serial_number, name, division)
VALUES ('14594712769675615388', 'Dr. Kosmos', 'General Health'),
	('14594712769675615389', 'Dr. Dre', 'Emergency Care');

INSERT INTO nurses(serial_number, name, division)
VALUES	('14594712769675615385', 'Nurse House', 'General Health'),
	('14594712769675615386', 'Nurse Snuggles', 'General Health'),
	('14594712769675615387', 'Nurse Trump', 'Emergency Care');

INSERT INTO patients(serial_number, doctor_id, name) 
VALUES ('14594712769675615381', '1', 'Dolan Trupm'),
	('14594712769675615382', '2', 'Pepe Frog'),
	('14594712769675615383', '1', 'Robbie Rotten'),
	('14594712769675615384', '2', 'Tengil');
	
	
INSERT INTO medical_records(patient_id, doctor_id, nurse_id, division, disease)
VALUES ('1','1','1','General Health', 'Hypochondriasis'),
	('2','2','3','Emergency Care', 'Influenza'),
	('3','1','2','General Health', 'Hubris'),
	('4','2','3','Emergency Care', '4th-degree burns');



	
