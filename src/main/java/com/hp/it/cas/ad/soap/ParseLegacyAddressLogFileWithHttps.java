package com.hp.it.cas.ad.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import com.hp.it.cas.ad.utilities.AddressServiceX509TrustManager;
import com.hp.it.cas.match.address.soap.ArrayOfString;
import com.hp.it.cas.match.address.soap.CASServiceSoap;

/**
 * 
 * Parse AddressDoctor SOAP service log on production.
 * Each address call AD ITG service.
 * 
 */
public class ParseLegacyAddressLogFileWithHttps {
	
	public static void main(String[] args) throws Exception {
		new ParseLegacyAddressLogFileWithHttps().parseLogFile("C:/AddressDoctor/document/20150410-570Upgrade/log/houston/data-match1/ad-legacy-service.log");
	}
	
	private void parseLogFile(String logFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(logFile)));
			
			String line = null;
			int sum = 0;
			
			while ((line = br.readLine()) != null) {
				
				// HybirdAddress method
				if (line.contains("analyzeHybridAddress")) {
					if (line.contains("Input address") && line.endsWith("]")) {
						
						ArrayOfString hybInput = new ArrayOfString();
						String hybInputAddress = line.substring(line.indexOf("Input address: [") + 16, line.lastIndexOf(']'));
						
						String[] hybInputFields = hybInputAddress.split(",");
						if (hybInputFields.length == 8 || hybInputFields.length == 13) {
						
							// HybridAddress require 8 fields
							setHybInputAddress(hybInput, hybInputFields);
							
							// call AD SOAP service
							ArrayOfString hybOutput = getCASServiceSoapClient().standardizeHybridAddress(hybInput);
							System.out.println(line.substring(line.indexOf("Input address: [")));
							System.out.println(hybInput.getString());
							System.out.println(hybOutput.getString());
						}
					}
				}
				
				if (line.contains("analyzeDiscreteAddress")) {
					if (line.contains("Input address") && line.endsWith("]")) {
						
						String disInputAddress = line.substring(line.indexOf("Input address: [") + 16, line.lastIndexOf(']'));
						ArrayOfString disInput = new ArrayOfString();
						String[] disInputFields = disInputAddress.split(",");
						
						if (disInputFields.length < 8 || disInputFields.length > 13) {
							continue;
						}
						
						// DiscreteAddress required 13 fields
						setDisInputAddress(disInput, disInputFields);
						
						// call AD SOAP service
						ArrayOfString disOutput = getCASServiceSoapClient().standardizeDiscreteAddress(disInput);
						System.out.println(line.substring(line.indexOf("Input address: [")));
						System.out.println(disInput.getString());
						System.out.println(disOutput.getString());
					}
				}
				sum ++;
			}
			System.out.println(logFile + " Input address count: " + sum);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
	}
	
	private void setDisInputAddress(ArrayOfString input, String[] inputFields) {
		for (String field : inputFields) {
			if (field == null || "".equals(field) || field.trim().equals("") || field.trim().equals("null")) {
				input.getString().add("");
			} else {
				input.getString().add(field.trim());
			}
		}
	}
	
	private void setHybInputAddress(ArrayOfString input, String[] inputFields) {
		for (int i = 0; i < 8; i++) {
			if (inputFields[i] == null || "".equals(inputFields[i]) || inputFields[i].trim().equals("") || inputFields[i].trim().equals("null")) {
				input.getString().add("");
			} else {
				input.getString().add(inputFields[i].trim());
			}
		}
	}
	
	private CASServiceSoap getCASServiceSoapClient() throws MalformedURLException {
		QName SERVICE_NAME = new QName("http://localhost/", "CASServiceSoapImplService");
		CASServiceSoap client = null;
		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			
			URL url = new URL("https://address-service-itg.core.hpecorp.net/legacy-match/address/v1?wsdl");
			
			// create SSLContext object and initial it with specified Trust Manager
			TrustManager[] tm = { new AddressServiceX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			
			// get SSLSocketFactory object
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			// create HttpsURLConnection object and set its default SSLSocketFactory object
			HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
			httpsConnection.setSSLSocketFactory(ssf);
			Service service = Service.create(url, SERVICE_NAME);
			String endpointAddress = "https://address-service-itg.core.hpecorp.net/legacy-match/address/v1";
			
			client = service.getPort(CASServiceSoap.class);
			
			BindingProvider bp = (BindingProvider) client;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		} catch (Exception e){
			e.printStackTrace();
		}
		return client;
	}
	
	private void close(BufferedReader br) {
		try {
			if (br != null) {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
