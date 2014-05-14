package httpConnection;

import iScope.WWJApplet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpPost 
{				
		public static String sendPostrequest(String server,String request) throws IOException
		{
			if (WWJApplet.DEBUG)
				System.out.println("Connecting...");
			URL url = new URL(server);
			System.out.println("Server: " + server);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");			
			con.setRequestProperty("Content-Type", "application/xml");
//			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			if (WWJApplet.DEBUG)
				System.out.println("Writing...");
			wr.writeBytes(request);
			wr.flush();
			wr.close();
			if (WWJApplet.DEBUG)
				System.out.println("Reading...");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			if (WWJApplet.DEBUG)
				System.out.println("Finished...");
			return response.toString();
		}
		
		public static String sendExportPostrequest(String server,String request) throws IOException
		{
			if (WWJApplet.DEBUG)
				System.out.println("Connecting to server "+ server);
			
			URL url = new URL(server);			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");			
			con.setRequestProperty("Content-Type", "application/xml");
			
			con.setDoOutput(true);
			System.out.println(con.getDoOutput());
//			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			if (WWJApplet.DEBUG)
				System.out.println("Writing...");
			wr.writeBytes(request);
			wr.flush();
			wr.close();
			if (WWJApplet.DEBUG)
				System.out.println("Reading...");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			if (WWJApplet.DEBUG)
				System.out.println("Finished...");
			return response.toString();
		}
}
