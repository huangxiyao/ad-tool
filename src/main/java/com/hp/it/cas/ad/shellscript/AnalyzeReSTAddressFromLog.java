package com.hp.it.cas.ad.shellscript;

import java.util.ArrayList;

import com.hp.it.cas.ad.utilities.ReSTServiceUtils;

public class AnalyzeReSTAddressFromLog {
	
	public static String getQueryUrl(String line){
		String addressQuery = line.substring(line.indexOf("AddressQuery [") + 14, line.lastIndexOf("]"));
		
		String[] query = parseEachAddress(addressQuery);
		StringBuffer bufQueryUrl = new StringBuffer();
		for (int i = 0 ; i < query.length; i++) {
			String field = query[i];
			String key = field.substring(0, field.indexOf("=") + 1);
			String value = field.substring(field.indexOf("=") + 1);
			
			bufQueryUrl.append(key).append(escapeSpecialCharacters(value));
			
			if (i < query.length - 1) {
				bufQueryUrl.append("&");
			}
		}
		return bufQueryUrl.toString();
	}
	
	/**
	 * Split address through Comma Space.
	 * 
	 * @param address
	 * @return
	 */
	public static String[] parseEachAddress(String address) {
		String[] fields = address.split(", ");
		
		ArrayList<String> finalFields = new ArrayList<String>();
		for (String field : fields){
			if (!(field == null || field.trim().equals(""))) {
				if (field.contains("=")){
					if (ReSTServiceUtils.checkKeyFields(field.substring(0, field.indexOf("=")))){
						finalFields.add(field);
					} else { // part of value
						StringBuffer buf = new StringBuffer(finalFields.remove(finalFields.size()-1));
						buf.append(", ").append(field);
						finalFields.add(buf.toString());
					}
				} else { // part of value
					StringBuffer buf = new StringBuffer(finalFields.remove(finalFields.size()-1));
					buf.append(", ").append(field);
					finalFields.add(buf.toString());
				}
			} else { // a=v1, , , b=v2, , ,
				StringBuffer buf = new StringBuffer(finalFields.remove(finalFields.size()-1));
				buf.append(", ").append(field);
				finalFields.add(buf.toString());
			}
		}
		return finalFields.toArray(new String[0]);
	}
	
	/**
	 * Escape special character in url.
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeSpecialCharacters(String str) {
		return str.replaceAll("%", "%25").replaceAll("[+]", "%2B")
				.replaceAll(" ", "%20").replaceAll("[/]", "%2F")
				.replaceAll("[?]", "%3F").replaceAll("[#]", "%23")
				.replace("[&]", "%26").replaceAll("[=]", "%3D");
	}
}
