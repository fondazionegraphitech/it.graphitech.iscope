package noise;

import gov.nasa.worldwind.geom.Position;
import httpConnection.httpGet;
import iScope.Props;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NoiseRequester
{
	private static String server = Props.getStr("NoiseServer");
	private static String apiKey = Props.getStr("NoiseApiKey");
	
	
	public List<NoisePoint> doPunctualValuesRequestAndParseJson(double latMin, double lonMin, double latMax, double lonMax, String user, String tag, String cityID, String since, String until, String dbMax, String dBMin )
	{			
		
		String boundingBox = String.valueOf(lonMin) + "," + String.valueOf(latMin) + "," + String.valueOf(lonMax) + "," + String.valueOf(latMax);
//		String requestString = server + "search.json?" +  "key=" + apiKey + "&geo=" + boundingBox;
		String requestString = server + "trackmap?" +  "key=" + apiKey + "&box=" + boundingBox;		
//		if(user!=null) requestString = requestString.concat("&user=" + user);
//		if(tag!=null) requestString = requestString.concat("&tag=" + tag);
//		if(cityID!=null) requestString = requestString.concat("&city=" + cityID);
		if(since!=null) requestString = requestString.concat("&since=" + since);
		if(until!=null) requestString = requestString.concat("&until=" + until);
		if(dbMax!=null) requestString = requestString.concat("&dbmax=" + dbMax);
		if(dBMin!=null) requestString = requestString.concat("&dBmin=" + dBMin);				
		String results = httpConnection.httpGet.sendGetRequest(requestString);
		if (results == null)
		{
			System.out.println("Service Down - Call Backup");
			return null;
		}	
		List<NoisePoint> noisePointList = parseAndVisualise(results);	
		if (noisePointList.size()!=0) return parseAndVisualise(results);	
		else return null;
	}
	
	private List<NoisePoint> parseAndVisualise(String results){
		try
		{
			List<NoisePoint> response = new LinkedList<>();
			JSONParser parser = new JSONParser();			
			JSONArray array = (JSONArray) parser.parse(results);
			for (int i = 0; i < array.size(); i++)
			{
				NoisePoint tmpNoisePoint;
				JSONObject obj2 = (JSONObject) array.get(i);
				if (obj2.get("lng") != null && obj2.get("lat") != null)
				{
					tmpNoisePoint = new NoisePoint(Position.fromDegrees((double) obj2.get("lat"), (double) obj2.get("lng")), Integer.parseInt(obj2.get(
							"loudness").toString()));
					if (obj2.get("made_at") != null)
						tmpNoisePoint.setAquisitionTime((String) obj2.get("made_at"));					
					if (obj2.get("user") != null && obj2.get("user") != "null")
						tmpNoisePoint.setUser(obj2.get("user").toString());	
					response.add(tmpNoisePoint);
				}					
			}
			return response;
		}
		catch (ParseException pe)
		{
			System.err.println("Error parsing noise response " + pe.toString());
		}
		return null;		
	}
	
	public String generateAggregateNoiseMaps(double latMin, double lonMin, double latMax, double lonMax, String startDateW3C, String endDateW3C,String name){
		return generateAggregateNoiseMaps(latMin, lonMin, latMax, lonMax, startDateW3C, endDateW3C,"00:00" ,"23:59", name);
	}
			
	
	
	public String generateAggregateNoiseMaps(double latMin, double lonMin, double latMax, double lonMax, String startDateW3C, String endDateW3C,String from, String to, String name){
		String boundingBox = String.valueOf(latMin) + "," + String.valueOf(lonMin) + "," + String.valueOf(latMax) + "," + String.valueOf(lonMax);
		String requestString = server + "aggmap?" +  "key=" + apiKey + "&box=" + boundingBox + "&since=" + startDateW3C + "&until=" + endDateW3C + "&from"+ from + "&to"+ to + "&name=" + name;
		String results = httpConnection.httpGet.sendGetRequest(requestString);
		if (results == null || results == "Error sending request.")
		{
			System.out.println("Problems generating aggreaate noise maps");
			return "";
		}		
		System.out.println("RISULTATO RICHESTA MAPPE AGGREGATE:" + results);
		return results;
	}
	
	public static void aggregateMapgenerationStatus (String mapId){
		String requestString = server + "aggmap?key=" + apiKey + "&map=" + mapId;
		String results = httpConnection.httpGet.sendGetRequest(requestString);
		if (results == null)
		{
			System.out.println("Service Down - Call Backup");
		}		
		System.out.println("Generation of map " + mapId + " status: " + results);
	}	
	
	public String requestAPIkeyFromCredentials(String user, String pwd){
		String requeststring = server + "authenticate?login=" + user.toLowerCase() + "&password=" + pwd; 
		String APIkey = httpGet.sendGetRequest(requeststring);
		System.out.println(APIkey);
		
		return APIkey;		
	}
}