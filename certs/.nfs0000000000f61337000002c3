
keytool -importcert -file cert.pem -keypass password -storepass password -keystore clienttruststore -noprompt
keytool -genkeypair -dname "cn=Patient Olle" -alias patient1 -keypass password -storepass password -keystore patient_1_keystore
keytool -certreq -file p1req.csr -alias patient1 -keypass password -storepass password -keystore patient_1_keystore
openssl x509 -req -in p1req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore patient_1_keystore -noprompt
keytool -importcert -alias patient1 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore patient_1_keystore -noprompt

keytool -genkeypair -dname "cn=Patient Karin" -alias patient2 -keypass password -storepass password -keystore patient_2_keystore
keytool -certreq -file p2req.csr -alias patient2 -keypass password -storepass password -keystore patient_2_keystore
openssl x509 -req -in p2req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore patient_2_keystore -noprompt
keytool -importcert -alias patient2 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore patient_2_keystore -noprompt

keytool -genkeypair -dname "cn=Patient Robert" -alias patient3 -keypass password -storepass password -keystore patient_3_keystore
keytool -certreq -file p3req.csr -alias patient3 -keypass password -storepass password -keystore patient_3_keystore
openssl x509 -req -in p3req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore patient_3_keystore -noprompt
keytool -importcert -alias patient3 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore patient_3_keystore -noprompt

keytool -genkeypair -dname "cn=Patient Dennis" -alias patient4 -keypass password -storepass password -keystore patient_4_keystore
keytool -certreq -file p4req.csr -alias patient4 -keypass password -storepass password -keystore patient_4_keystore
openssl x509 -req -in p4req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore patient_4_keystore -noprompt
keytool -importcert -alias patient4 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore patient_4_keystore -noprompt

keytool -genkeypair -dname "cn=Nurse Berit" -alias nurse1 -keypass password -storepass password -keystore nurse_1_keystore
keytool -certreq -file n1req.csr -alias nurse1 -keypass password -storepass password -keystore nurse_1_keystore
openssl x509 -req -in n1req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore nurse_1_keystore -noprompt 
keytool -importcert -alias nurse1 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore nurse_1_keystore -noprompt

keytool -genkeypair -dname "cn=Nurse Peter" -alias nurse2 -keypass password -storepass password -keystore nurse_2_keystore
keytool -certreq -file n2req.csr -alias nurse2 -keypass password -storepass password -keystore nurse_2_keystore
openssl x509 -req -in n2req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore nurse_2_keystore -noprompt
keytool -importcert -alias nurse2 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore nurse_2_keystore -noprompt

keytool -genkeypair -dname "cn=Nurse Johanna" -alias nurse3 -keypass password -storepass password -keystore nurse_3_keystore
keytool -certreq -file n3req.csr -alias nurse3 -keypass password -storepass password -keystore nurse_3_keystore
openssl x509 -req -in n3req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore nurse_3_keystore -noprompt
keytool -importcert -alias nurse3 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore nurse_3_keystore -noprompt

keytool -genkeypair -dname "cn=Doctor Oskar" -alias doctor1 -keypass password -storepass password -keystore doctor_1_keystore
keytool -certreq -file d1req.csr -alias doctor1 -keypass password -storepass password -keystore doctor_1_keystore
openssl x509 -req -in d1req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore doctor_1_keystore -noprompt
keytool -importcert -alias doctor1 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore doctor_1_keystore -noprompt

keytool -genkeypair -dname "cn=Doctor Ingrid" -alias doctor2 -keypass password -storepass password -keystore doctor_2_keystore
keytool -certreq -file d2req.csr -alias doctor2 -keypass password -storepass password -keystore doctor_2_keystore
openssl x509 -req -in d2req.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore doctor_2_keystore -noprompt
keytool -importcert -alias doctor2 -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore doctor_2_keystore -noprompt

keytool -genkeypair -dname "cn=Government" -alias government -keypass password -storepass password -keystore government_keystore
keytool -certreq -file greq.csr -alias government -keypass password -storepass password -keystore government_keystore
openssl x509 -req -in greq.csr -CA cert.pem -CAkey key.pem -CAcreateserial  -out signedcert.cnf -passin pass:password
keytool -importcert -alias CA -file cert.pem -trustcacerts -keypass password -storepass password -keystore government_keystore -noprompt
keytool -importcert -alias government -file signedcert.cnf -trustcacerts -keypass password -storepass password -keystore government_keystore -noprompt
