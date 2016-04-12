package com.hp.it.cas.ad.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class AddressServiceX509TrustManager implements X509TrustManager {
	X509TrustManager sunJSSEX509TrustManager;
	
	public AddressServiceX509TrustManager() throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		String jave_home = System.getProperty("java.home");
		ks.load(new FileInputStream(new File(jave_home + "/lib/security/cacerts")), "changeit".toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		tmf.init(ks);
		TrustManager tms[] = tmf.getTrustManagers();
		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509TrustManager) {
				sunJSSEX509TrustManager = (X509TrustManager) tms[i];
				return;
			}
		}
		throw new Exception("Couldn't initialize");
	}
	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException excep) {
		}
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try	{
			sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException excep) {
		}
	}
	
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return sunJSSEX509TrustManager.getAcceptedIssuers();
	}
}
