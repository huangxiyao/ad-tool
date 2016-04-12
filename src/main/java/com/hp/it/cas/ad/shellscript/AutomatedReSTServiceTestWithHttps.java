package com.hp.it.cas.ad.shellscript;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.hp.it.cas.ad.utilities.AddressServiceX509TrustManager;

/**
 * Parse AddressDoctor ReST service log on production.
 * Each address call AD ITG service.
 * 
 * @author yu-juan.zhang@hp.com
 * 
 */
public class AutomatedReSTServiceTestWithHttps {
	
	public static void main(String[] args){
		new AutomatedReSTServiceTestWithHttps().parseReSTLog(args[0], args[1], args[2]);
	}
	
	/**
	 * Parse ReST log file.
	 * 
	 * @return
	 */
	public void parseReSTLog(String logFile, String resultFile, String serverName){
		BufferedReader br = null;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(resultFile));
			br = new BufferedReader(new FileReader(new File(logFile)));
			int totalCount = 0;
			int successCount = 0;
			int failCount = 0;
			
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("ENTRY AddressQuery") && line.endsWith("]") && line.contains("country1=")) {
					totalCount ++;
					
					String queryUrl = AnalyzeReSTAddressFromLog.getQueryUrl(line);
					
					String strUrl = new StringBuffer("/match/validatedAddress?").append(queryUrl).toString();
					HttpsURLConnection httpsConnection = getConnection(serverName, strUrl);
					
					int responseCode = httpsConnection.getResponseCode();
					if (responseCode == HttpsURLConnection.HTTP_OK) {
						successCount ++;
						
						BufferedReader in = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream()));
						
						String response = null;
						StringBuffer content = new StringBuffer();
						while ((response = in.readLine()) != null) {
							content.append(response);
						}
					} else {
						failCount ++;
						// save result into file
						out.write(line);
						out.newLine();
						// response code
						out.write("" + responseCode);
						out.newLine();
						// request url
						out.write(httpsConnection.getURL().toString());
						out.newLine();
					}
					if ( totalCount % 10 == 0 ){
						System.out.println("The program have already read " + totalCount + " ENTRY lines. Program is running, please keep waiting...");
					}
				}
			}
			System.out.println("The program have already finished read " + totalCount + " ENTRY lines in the file " + logFile);
			out.write("number of successful transactions is " + successCount);
			out.newLine();
			out.write("number of transactions that crashed address doctor is " + failCount);
			out.newLine();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			close(br, out);
		}
	}
	
	private HttpsURLConnection getConnection(String serverName, String strUrl) {
		HttpsURLConnection httpsConnection = null;
		try {
			System.setProperty("jsse.enableSNIExtension", "false");
			URL url = new URL(serverName + strUrl);
			// create SSLContext object and initial it with specified Trust Manager
			TrustManager[] tm = { new AddressServiceX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			
			// get SSLSocketFactory object
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			// create HttpsURLConnection object and set its default SSLSocketFactory object
			httpsConnection = (HttpsURLConnection) url.openConnection();
			httpsConnection.setSSLSocketFactory(ssf);
			httpsConnection.setRequestMethod("GET");
			httpsConnection.setRequestProperty("X-HP-Application-Process-UID", "w-mdcp:prd-http");
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return httpsConnection;
	}
	
	private void close(BufferedReader br, BufferedWriter out) {
		try {
			if (br != null) {
				br.close();
			}
			if (out != null){
				out.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
