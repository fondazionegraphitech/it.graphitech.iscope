package iScope;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Props
{
	private static Properties cfg;
//	private String propsFileLocation = "http://77.72.192.34/iScope2/config/config.properties";  //tnet server
//	private String propsFileLocation1 = "http://192.168.253.26:8099/i-Scope/iScopeApplet/config.properties"; //mediastore
	private String propsFileLocation1 = "http://iscope.graphitech-projects.com/applet/config.properties"; //aruba cloud	
	//private String propsFileLocation2 = "http://cytherea.graphitech.it/iscope/applet/config.properties"; //gt develop
	private String propsFileLocation2 = "https://dl.dropboxusercontent.com/u/44546315/config.properties"; //dropBoxMarco	
	
	public Props()
	{
		cfg = new Properties();			
//		InputStream inputStream = getClass().getResourceAsStream("config.properties");
		InputStream inputStream;
		try {
			inputStream = new URL(propsFileLocation1).openStream();
			cfg.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("impossibile trovare il file Props in " + propsFileLocation1 + ", provo con quello in " + propsFileLocation2);						
			try {
				inputStream = new URL(propsFileLocation2).openStream();
				cfg.load(inputStream);
			} catch (IOException e1) {
				System.err.println("impossibile trovare il file Props in " +  propsFileLocation2 + " CHIUSURA FORZATA APPLICAZIONE");	
				e1.printStackTrace();
				System.exit(0);
			}		
		}
		System.out.println("Props file test:" + cfg.getProperty("testProps"));	
	}

	static public String getStr(String key)
	{
		return cfg.getProperty(key);
	}

	static public int getInt(String key)
	{
		return Integer.parseInt(cfg.getProperty(key));
	}

	static public double getDou(String key)
	{
		return Double.parseDouble(cfg.getProperty(key));
	}

	static public boolean getBool(String key)
	{
		return Boolean.parseBoolean(cfg.getProperty(key));
	}

	static public List<String> getStrList(String key)
	{
		String listString = Props.getStr(key);
		return Arrays.asList(listString.split(","));
	}
}
