package com.hp.it.cas.ad.utilities;

import java.util.Arrays;
import java.util.List;

public class ReSTServiceUtils {
	public static String[] QUERY_KEYS = new String[] { "key1", "key2", "key3", "modeUsed", "preferredLanguage", "preferredScript", "characterScriptDetectionIndicator", "country1", "country2", "country3", "addressComplete", "building1", "building2",
		"building3", "building4", "building5", "building6", "locality1", "locality2", "locality3", "locality4", "locality5", "locality6", "postalCode1", "postalCode2", "postalCode3", "countrySpecificLocalityLine1", "countrySpecificLocalityLine2",
		"countrySpecificLocalityLine3", "countrySpecificLocalityLine4", "countrySpecificLocalityLine5", "countrySpecificLocalityLine6", "street1", "street2", "street3", "street4", "street5", "street6", "number1", "number2", "number3", "number4",
		"number5", "number6", "province1", "province2", "province3", "province4", "province5", "province6", "deliveryAddressLine1", "deliveryAddressLine2", "deliveryAddressLine3", "deliveryAddressLine4", "deliveryAddressLine5", "deliveryAddressLine6",
		"deliveryService1", "deliveryService2", "deliveryService3", "deliveryService4", "deliveryService5", "deliveryService6", "formattedAddressLine1", "formattedAddressLine2", "formattedAddressLine3", "formattedAddressLine4", "formattedAddressLine5",
		"formattedAddressLine6", "formattedAddressLine7", "formattedAddressLine8", "formattedAddressLine9", "formattedAddressLine10", "formattedAddressLine11", "formattedAddressLine12", "formattedAddressLine13", "formattedAddressLine14",
		"formattedAddressLine15", "formattedAddressLine16", "formattedAddressLine17", "formattedAddressLine18", "formattedAddressLine19", "organization1", "organization2", "organization3", "contact1", "contact2", "contact3", "recipientLine1",
		"recipientLine2", "recipientLine3", "residue1", "residue2", "residue3", "residue4", "residue5", "residue6", "subBuilding1", "subBuilding2", "subBuilding3", "subBuilding4", "subBuilding5", "subBuilding6"};
		
	public static boolean checkKeyFields(String key) {
		List<String> list_query_keys = Arrays.asList(QUERY_KEYS);
		return list_query_keys.contains(key);
	}
}
