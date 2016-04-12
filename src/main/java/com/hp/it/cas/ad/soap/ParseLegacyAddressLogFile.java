package com.hp.it.cas.ad.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import com.hp.it.cas.match.address.soap.ArrayOfString;
import com.hp.it.cas.match.address.soap.CASServiceSoap;

/**
 * 
 * Parse AddressDoctor SOAP service log on production. 
 * Each address call AD ITG service.
 * 
 */
public class ParseLegacyAddressLogFile {
	
	public static void main(String[] args) throws Exception {
		new ParseLegacyAddressLogFile().parseLogFile("C:/AddressDoctor/document/20141112-AD5.6.0/log/houston/data-match1/ad-legacy-service-log.2014-08-03.log");
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
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
		Service service = Service.create(new URL("http://address-service-itg.core.hpecorp.net/legacy-match/address/v1?wsdl"), SERVICE_NAME);
		String endpointAddress = "http://address-service-itg.core.hpecorp.net/legacy-match/address/v1";
		
		CASServiceSoap client = service.getPort(CASServiceSoap.class);
		
		BindingProvider bp = (BindingProvider) client;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		
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
