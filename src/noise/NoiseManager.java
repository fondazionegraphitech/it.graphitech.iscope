package noise;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.markers.Marker;
import iScope.WWJApplet;

import java.util.LinkedList;
import java.util.List;

public class NoiseManager
{
	private NoiseMarkerLayer noiseMarkerLayer = new NoiseMarkerLayer();
	private NoiseRequester noiseRequester = new NoiseRequester();
	private List<Marker> noiseMarkers = new LinkedList<>();
	


	public NoiseManager()
	{
		noiseMarkerLayer.setOverrideMarkerElevation(true);
		noiseMarkerLayer.setKeepSeparated(true);
		noiseMarkerLayer.setElevation(WorldWind.CLAMP_TO_GROUND);
		WWJApplet.getWWD().getModel().getLayers().add(noiseMarkerLayer);	
	}


	public boolean requestAndVisualisePunctualNoisePoints(Sector sector, String user, String tag, String cityID, String since, String until, String dbMax, String dBMin)
	{
		System.out.println("NOW: " +" usr:" + user + " tag: "+  tag + " CITYid:" + cityID + " SINCE:" + since + " UNTIL:"+ until + " DBmAX:" + dbMax + " DBmIN:" + dBMin);	
		List<NoisePoint> noisePoints = noiseRequester.doPunctualValuesRequestAndParseJson(sector.getMinLatitude().degrees, sector.getMinLongitude().degrees, sector.getMaxLatitude().degrees, sector.getMaxLongitude().degrees, user, tag, cityID, since, until, dbMax, dBMin);
		if (noisePoints != null) {
			for (NoisePoint noisePoint : noisePoints)
				noiseMarkers.add(NoiseMarker.fromNoisePoint(noisePoint));
			noiseMarkerLayer.setMarkers(noiseMarkers);
			return true;
		}
		else{			
			return false;
		}
	}
	
	public String generateAggregateMaps(Sector sector, String startDate, String endDate, String from, String to, String mapName){
		if(startDate == null || endDate == null || mapName == null) {
			System.err.println("Missing values: generateAggregateMaps");
			return "";
		}		
		return noiseRequester.generateAggregateNoiseMaps(sector.getMinLatitude().degrees, sector.getMinLongitude().degrees, sector.getMaxLatitude().degrees, sector.getMaxLongitude().degrees, startDate, endDate,from ,to , mapName);
	}

	public void clearNoiseMarkers()
	{		
		noiseMarkers = new LinkedList<>();
		noiseMarkerLayer.setMarkers(noiseMarkers);
	}
	
	
	public void changeMarkerDimension(int deltaDim){
		for (Marker noiseMarker : noiseMarkerLayer.getMarkers()) {
			NoiseMarker marker = (NoiseMarker)noiseMarker;
			marker.changeMarkerDimension(deltaDim);		    
		}
		WWJApplet.getWWD().redraw();
	}
}
