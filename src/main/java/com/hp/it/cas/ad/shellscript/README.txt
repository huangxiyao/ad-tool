
Address Service Automated Shell Script Test
===========================================

Function: (Implemented in Java class)
Scan the ReST service log for a given day on production servers.
And each ENTRY address against ITG service.
Log the failed ENTRY address and response code and its request url.
Log the number of successful transactions and the number of transactions that crashed address doctor.

Note:
For the script: You should put the shell script and the jar file of this project in the same specified directory.
At the end of the script, change to the specified directory that you used and run the java command to call the java class.
Java version: Oracle java 7.

//////////////////////////////////////////
For the test with http protocol - Usage:
bash ad-curl.sh $PRO_ReST_LOG_FILE $RESULT_LOG_FILE $AddressService_ITG_SERVER_NAME $PORT
The default port number is 80.

//////////////////////////////////////////
For the test with https protocol:
1. At first, make sure that you have import the certificates into the JDK(which you are using in the shell script).
1.1 Here are the certificates:
  com.hp.it.cas.ad.utilities.server.crt
  com.hp.it.cas.ad.utilities.ca-bundle.crt
1.2 It needs keyutil jar file when import the ca-bundle.crt. Here we can get the keyutil jar file:
  https://code.google.com/p/java-keyutil/downloads/detail?name=keyutil-0.4.0.jar
  https://java-keyutil.googlecode.com/files/keyutil-0.4.0.jar
1.3 Import the certificates into oracle java7:
  keytool -import -alias ADDRESS -keystore $JAVA_HOME/jre/lib/security/caters -file server.crt
  $JAVA_HOME/bin/java -jar keyutil-0.4.0.jar --import --keystore-file $JAVA_HOME/jre/lib/security/caters --password changeit --import-pem-file ca-bundle.crt

2. Usage: bash ad-https-curl.sh $PRO_ReST_LOG_FILE $RESULT_LOG_FILE $AddressService_ITG_SERVICE_NAME


