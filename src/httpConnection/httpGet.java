package httpConnection;

import iScope.Props;
import iScope.WWJApplet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class httpGet {
	
	private static int serverTimeout = Props.getInt("NoiseServerTimeout");
	
	public static String sendGetRequest(String requestString)
	{
		String result = null;
		try
		{						
			URL url = new URL(requestString);
			if (WWJApplet.DEBUG)
				System.out.println("Request String: " + requestString);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(serverTimeout);
			conn.setReadTimeout(serverTimeout);		
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
		}
		catch (SocketTimeoutException e)		{
			System.out.println("Timed out: " + serverTimeout + "ms.");
			return "Error sending request.";
		} 
		catch (Exception e)	{
			System.out.println("Error sending request.");
			e.printStackTrace();
			return "Error sending request.";
		}
		return result;
		//THIS SHOULD WORK TOO
		/*try
		{
			return IOUtils.toString(sendGetRequestInputStream(requestString));
		} catch (IOException e)
		{
			System.out.println("Error reading request.");
			e.printStackTrace();
			return null;
		}*/
	}

}
