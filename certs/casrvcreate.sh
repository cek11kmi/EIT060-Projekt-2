keytool -importcert -file cert.pem -keystore servertruststore
keytool -genkeypair -dname "cn=Hospital Server" -keystore serverkeystore
keytool -certreq -file servercertreq.csr -keystore serverkeystore
openssl x509 -req -in servercertreq.csr -CA cert.pem -CAkey key.pem -CAcreateserial -out serversignedcert.cnf
keytool -importcert -alias CA -file cert.pem -trustcacerts -keystore serverkeystore
keytool -importcert -alias mykey -file serversignedcert.cnf -trustcacerts -keystore serverkeystore
