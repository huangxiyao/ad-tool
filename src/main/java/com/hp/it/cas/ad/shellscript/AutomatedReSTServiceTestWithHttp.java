package com.hp.it.cas.ad.shellscript;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Parse AddressDoctor ReST service log on production.
 * Each address call AD ITG service.
 * 
 * @author yu-juan.zhang@hp.com
 * 
 */
public class AutomatedReSTServiceTestWithHttp {
	
	public static void main(String[] args){
		new AutomatedReSTServiceTestWithHttp().parseReSTLog(args[0], args[1], args[2]);
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
					HttpURLConnection httpConnection = getConnection(serverName, strUrl);
					
					int responseCode = httpConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						successCount ++;
						
						BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
						
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
						out.write(httpConnection.getURL().toString());
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
	
	private HttpURLConnection getConnection(String serverName, String strUrl){
		HttpURLConnection httpConnection = null;
		try {
			URL url = new URL(serverName + strUrl);
			URLConnection urlConnection = url.openConnection();
			httpConnection = (HttpURLConnection) urlConnection;
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("X-HP-Application-Process-UID", "w-mdcp:prd-http");
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return httpConnection;
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
