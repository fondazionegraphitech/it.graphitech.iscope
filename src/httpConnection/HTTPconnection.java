package httpConnection;

import iScope.Props;
import iScope.WWJApplet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import mangeXML.XMLconverter;
 
public class HTTPconnection 
{
	private final static String USER_AGENT = "Mozilla/5.0"; 
 
	public static StringBuffer sendGet(String url) throws Exception { 
		 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setConnectTimeout(3000);
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response;
 
	}
 
	// HTTP POST request
	public static StringBuffer sendPost(String url, String urlParameters) throws Exception { 
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
 		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());

		return response; 
	}	
	
	public static String postXMLfromFile(String targetURL, String xmlFilePath) {
		System.out.println("R:" + targetURL);
		URL url;
		HttpURLConnection conn = null;
		try {
			// Create connection
			url = new URL(targetURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", "text/xml");

			// Send request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(XMLconverter.convertXMLFileToString(xmlFilePath));
			wr.flush();
			wr.close();
			// Get Response
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer resp = new StringBuffer();
			resp.append(conn.getResponseCode() + "\r");
			while ((line = rd.readLine()) != null) {
				resp.append(line);
				resp.append('\r');
			}
			rd.close();
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	public static String postXMLfromString(String targetURL, String xmlFileText) {
		System.out.println("R:" + targetURL);
		URL url;
		HttpURLConnection conn = null;
		try {
			// Create connection
			url = new URL(targetURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", "text/xml");

			// Send request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(xmlFileText);
			wr.flush();
			wr.close();
			// Get Response
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer resp = new StringBuffer();
			resp.append(conn.getResponseCode() + "\r");
			while ((line = rd.readLine()) != null) {
				resp.append(line);
				resp.append('\r');
			}
			rd.close();
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	public static InputStream sendGetRequestInputStream( String requestString ) throws IOException
	{
					
			URL url = new URL(requestString);
			if (Props.getBool("showBuildingTilesReqeust"))
				System.out.println("Request String: " + requestString);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(200000);		
			InputStream is = conn.getInputStream();
			return is;
	}

	

 
}