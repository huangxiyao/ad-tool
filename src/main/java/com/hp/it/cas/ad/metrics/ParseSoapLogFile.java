package com.hp.it.cas.ad.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseSoapLogFile {
	private static ArrayList<String> soapLogFileNames = new ArrayList<String>();
	private static String[] ctr_ISO3_ISO2 = new String[] { "ABW", "AW", "AFG",
			"AF", "AGO", "AO", "AIA", "AI", "ALA", "AX", "ALB", "AL", "AND",
			"AD", "ARE", "AE", "ARG", "AR", "ARM", "AM", "ASM", "AS", "ATA",
			"AQ", "ATF", "TF", "ATG", "AG", "AUS", "AU", "AUT", "AT", "AZE",
			"AZ", "BDI", "BI", "BEL", "BE", "BEN", "BJ", "BFA", "BF", "BGD",
			"BD", "BGR", "BG", "BHR", "BH", "BHS", "BS", "BIH", "BA", "BLM",
			"BL", "BLR", "BY", "BLZ", "BZ", "BMU", "BM", "BOL", "BO", "BRA",
			"BR", "BRB", "BB", "BRN", "BN", "BTN", "BT", "BVT", "BV", "BWA",
			"BW", "BES", "BQ", "CAF", "CF", "CAN", "CA", "CCK", "CC", "CHE",
			"CH", "CHL", "CL", "CHN", "CN", "CIV", "CI", "CMR", "CM", "COD",
			"CD", "COG", "CG", "COK", "CK", "COL", "CO", "COM", "KM", "CPV",
			"CV", "CRI", "CR", "CUB", "CU", "CUW", "CW", "CXR", "CX", "CYM",
			"KY", "CYP", "CY", "CZE", "CZ", "DEU", "DE", "DJI", "DJ", "DMA",
			"DM", "DNK", "DK", "DOM", "DO", "DZA", "DZ", "ECU", "EC", "EGY",
			"EG", "ERI", "ER", "ESH", "EH", "ESP", "ES", "EST", "EE", "ETH",
			"ET", "FIN", "FI", "FJI", "FJ", "FLK", "FK", "FRA", "FR", "FRO",
			"FO", "FSM", "FM", "GAB", "GA", "GBR", "GB", "GEO", "GE", "GGY",
			"GG", "GHA", "GH", "GIB", "GI", "GIN", "GN", "GLP", "GP", "GMB",
			"GM", "GNB", "GW", "GNQ", "GQ", "GRC", "GR", "GRD", "GD", "GRL",
			"GL", "GTM", "GT", "GUF", "GF", "GUM", "GU", "GUY", "GY", "HKG",
			"HK", "HMD", "HM", "HND", "HN", "HRV", "HR", "HTI", "HT", "HUN",
			"HU", "IDN", "ID", "IMN", "IM", "IND", "IN", "IOT", "IO", "IRL",
			"IE", "IRN", "IR", "IRQ", "IQ", "ISL", "IS", "ISR", "IL", "ITA",
			"IT", "JAM", "JM", "JEY", "JE", "JOR", "JO", "JPN", "JP", "KAZ",
			"KZ", "KEN", "KE", "KGZ", "KG", "KHM", "KH", "KIR", "KI", "KNA",
			"KN", "KOR", "KR", "KWT", "KW", "LAO", "LA", "LBN", "LB", "LBR",
			"LR", "LBY", "LY", "LCA", "LC", "LIE", "LI", "LKA", "LK", "LSO",
			"LS", "LTU", "LT", "LUX", "LU", "LVA", "LV", "MAC", "MO", "MAF",
			"MF", "MAR", "MA", "MCO", "MC", "MDA", "MD", "MDG", "MG", "MDV",
			"MV", "MEX", "MX", "MHL", "MH", "MKD", "MK", "MLI", "ML", "MLT",
			"MT", "MMR", "MM", "MNE", "ME", "MNG", "MN", "MNP", "MP", "MOZ",
			"MZ", "MRT", "MR", "MSR", "MS", "MTQ", "MQ", "MUS", "MU", "MWI",
			"MW", "MYS", "MY", "MYT", "YT", "NAM", "NA", "NCL", "NC", "NER",
			"NE", "NFK", "NF", "NGA", "NG", "NIC", "NI", "NIU", "NU", "NLD",
			"NL", "NOR", "NO", "NPL", "NP", "NRU", "NR", "NZL", "NZ", "OMN",
			"OM", "PAK", "PK", "PAN", "PA", "PCN", "PN", "PER", "PE", "PHL",
			"PH", "PLW", "PW", "PNG", "PG", "POL", "PL", "PRI", "PR", "PRK",
			"KP", "PRT", "PT", "PRY", "PY", "PSE", "PS", "PYF", "PF", "QAT",
			"QA", "REU", "RE", "ROU", "RO", "RUS", "RU", "RWA", "RW", "SAU",
			"SA", "SDN", "SD", "SEN", "SN", "SGP", "SG", "SGS", "GS", "SSD",
			"SS", "SHN", "SH", "SXM", "SX", "SJM", "SJ", "SLB", "SB", "SLE",
			"SL", "SLV", "SV", "SMR", "SM", "SOM", "SO", "SPM", "PM", "SRB",
			"RS", "STP", "ST", "SUR", "SR", "SVK", "SK", "SVN", "SI", "SWE",
			"SE", "SWZ", "SZ", "SYC", "SC", "SYR", "SY", "TCA", "TC", "TCD",
			"TD", "TGO", "TG", "THA", "TH", "TJK", "TJ", "TKL", "TK", "TKM",
			"TM", "TLS", "TL", "TON", "TO", "TTO", "TT", "TUN", "TN", "TUR",
			"TR", "TUV", "TV", "TWN", "TW", "TZA", "TZ", "UGA", "UG", "UKR",
			"UA", "UMI", "UM", "URY", "UY", "USA", "US", "UZB", "UZ", "VAT",
			"VA", "VCT", "VC", "VEN", "VE", "VGB", "VG", "VIR", "VI", "VNM",
			"VN", "VUT", "VU", "WLF", "WF", "WSM", "WS", "YEM", "YE", "ZAF",
			"ZA", "ZMB", "ZM", "ZWE", "ZW" };
			
	public void parse() {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "admetrics";
			String password = "admetrics";
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(String logFileName : soapLogFileNames) {
			parseLogFile(stmt, logFileName);
		}
		
		close(conn, stmt, null);
	}
	
	private void parseLogFile(Statement stmt, String logFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(logFile)));
			
			String line = null;
			int sum = 0;
			
			String str_date = null;
			String str_time = null;
			String str_service = null;
			String str_iso = null;
			try {
				while ((line = br.readLine()) != null) {
					if (line.contains("Input address:") && line.endsWith("]")) {
					
						if (line.contains("analyzeHybridAddress")){
							String[] data = line.split(" ");
							str_date = data[0];
							str_time = data[1];
							
							str_service = "analyzeHybridAddress";
							
							str_iso = line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf("]")).trim();
							str_iso = str_iso != null ? str_iso.toUpperCase() : str_iso;
							if ("null".equals(str_iso) || !checkCtrCode(str_iso)) {
								String tmp = line.substring(0, line.lastIndexOf(", null, null, null, null, null]"));
								str_iso = tmp.substring(tmp.lastIndexOf(", ") + 2).trim();
							}
							
							str_iso = str_iso != null ? str_iso.toUpperCase() : str_iso;
							if (checkCtrCode(str_iso)){
								String sql = "insert into tb_PRO values('h', 'SOAP','" + str_date + "','" + str_time + "','" + str_service + "','" + str_iso + "')";
								System.out.println(sql);
								stmt.execute(sql);
								sum ++;
							}
							
							clearVariables(str_date, str_time, str_service, str_iso);
						} else if (line.contains("analyzeDiscreteAddress")) {
							String[] data = line.split(" ");
							str_date = data[0];
							str_time = data[1];
							
							str_service = "analyzeDiscreteAddress";
							str_iso = line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf("]")).trim();
							
							str_iso = str_iso != null ? str_iso.toUpperCase() : str_iso;
							if (checkCtrCode(str_iso)){
								String sql = "insert into tb_PRO values('h', 'SOAP','" + str_date + "','" + str_time + "','" + str_service + "','" + str_iso + "')";
								System.out.println(sql);
								stmt.execute(sql);
								sum ++;
							}
							clearVariables(str_date, str_time, str_service, str_iso);
						}
					}
				}
				System.out.println(logFile + " Input address count: " + sum);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			close(null, null, br);
		}
	}
	
	private boolean checkCtrCode(String ctr) {
		List<String> ctrCodeList = Arrays.asList(ctr_ISO3_ISO2);
		return ctrCodeList.contains(ctr);
	}
	
	private void close(Connection conn, Statement stmt, BufferedReader br) {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
			if (br != null) {
				br.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearVariables(String str_date, String str_time, String str_appid, String str_iso) {
		str_date = null;
		str_time = null;
		str_appid = null;
		str_iso = null;
	}
	
	public static void setSoapLogFileNames(){
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match1/ad-legacy-service-log.2014-11-03.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match1/ad-legacy-service-log.2014-11-04.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match1/ad-legacy-service-log.2014-11-05.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match1/ad-legacy-service-log.2014-11-06.log");
//
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match2/ad-legacy-service-log.2014-11-03.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match2/ad-legacy-service-log.2014-11-04.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match2/ad-legacy-service-log.2014-11-05.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match2/ad-legacy-service-log.2014-11-06.log");
//
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match3/ad-legacy-service-log.2014-11-03.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match3/ad-legacy-service-log.2014-11-04.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match3/ad-legacy-service-log.2014-11-05.log");
//		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/austin/data-match3/ad-legacy-service-log.2014-11-06.log");
		
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match1/ad-legacy-service-log.2014-11-03.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match1/ad-legacy-service-log.2014-11-04.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match1/ad-legacy-service-log.2014-11-05.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match1/ad-legacy-service-log.2014-11-06.log");               
		
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match2/ad-legacy-service-log.2014-11-03.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match2/ad-legacy-service-log.2014-11-04.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match2/ad-legacy-service-log.2014-11-05.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match2/ad-legacy-service-log.2014-11-06.log");
		
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match3/ad-legacy-service-log.2014-11-03.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match3/ad-legacy-service-log.2014-11-04.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match3/ad-legacy-service-log.2014-11-05.log");
		soapLogFileNames.add("C:/AddressDoctor/document/20141107ADMetrics/log/houston/data-match3/ad-legacy-service-log.2014-11-06.log");
	}
	
	public static void main(String[] args) throws Exception {
		setSoapLogFileNames();
		new ParseSoapLogFile().parse();
	}
}
