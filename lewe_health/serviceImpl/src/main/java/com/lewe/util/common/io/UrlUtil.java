package com.lewe.util.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class UrlUtil {
	
	/**
	* 检测网络资源是否存在　
	* 判断一个url 路径对应的资源是否存在
	* (准确的说，这个链接是否是有效的链接，能否请求到资源)
	* @param strUrl
	* @return
	*/
	public static boolean isNetFileAvailable(String strUrl) {
		InputStream netFileInputStream = null;
		try {
			URL url = new URL(strUrl);
			URLConnection urlConn = url.openConnection();
			netFileInputStream = urlConn.getInputStream();
			if (null != netFileInputStream) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (netFileInputStream != null)
					netFileInputStream.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * 把url 转化成输入流对象
	 * @Description: 转换成流对象以后，就可以获取数据
	 * @param url
	 * @throws Exception
	 */
	public static InputStream urlToIO(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		ignoreHttpsCert();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		InputStream input = conn.getInputStream();
		return input;
	}
	
	/**
	 * 忽略HTTPS证书
	 */
	public static void ignoreHttpsCert() {
		try {
			javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
			javax.net.ssl.TrustManager tm = new MyTM();
			trustAllCerts[0] = tm;
			javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					System.out.println("Warning: URL Host: " + hostname + " vs. " + session.getPeerHost());
			        return true;
				}
			};
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class MyTM implements javax.net.ssl.TrustManager,javax.net.ssl.X509TrustManager {
	    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
	        return true;
	    }
	    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
	        return true;
	    }
	    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
	            throws java.security.cert.CertificateException {
	        return;
	    }
	    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
	            throws java.security.cert.CertificateException {
	        return;
	    }
	}

}
