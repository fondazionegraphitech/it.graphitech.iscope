package wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;
import httpConnection.httpGet;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PickableWMSLayer {

	private final String WMSBase = Props.getStr("pilotGeoServer")+"wms"; // "http://77.72.192.34:8080/geoserver/wms";
	String wmsLayer = null; // "wommelgem:wommelgem_noise_20130414_20130531";
	private GlobeAnnotation ga = new GlobeAnnotation("empty", new Position(
			Angle.ZERO, Angle.ZERO, 0));
	private AnnotationAttributes defaultAttributes;
	PickableLayerMouseListener layerMouseListener = new PickableLayerMouseListener();
	boolean enablePicking = true;

	public PickableWMSLayer() {		
		// Create default attributes
		defaultAttributes = new AnnotationAttributes();
        defaultAttributes.setCornerRadius(0);
        defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
        defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
        defaultAttributes.setTextColor(Color.WHITE);
        defaultAttributes.setDrawOffset(new Point(0, 100));    
        defaultAttributes.setLeader("void");      
        ga.setAttributes(defaultAttributes);
        ga.setAltitudeMode(0);
        WWJApplet.getWWD().addMouseListener(layerMouseListener);
	}	

	public void setLayerToQuery(Layer layer) {
		wmsLayer = layer.getName();		
	}

	public void enablePicking() {
		enablePicking = true;
	}

	public void disablePicking() {
		enablePicking = false;
	}

	/**
	 * Perform GetFeatureInfo as per WMS client spec
	 * 
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public String getFeatureInfo(Position location) {
		if (wmsLayer!=null){
			// New info coming clear the caching lists
			AVList params = new AVListImpl();
	
			params.setValue(AVKey.GET_MAP_URL, WMSBase);
			params.setValue(AVKey.LAYER_NAMES, wmsLayer);
			params.setValue(AVKey.WMS_VERSION, "1.1.1");
	
			// Get the URL for catches
			PickableWMSLayer.URLBuilder builder = new PickableWMSLayer.URLBuilder(
					params);
			URL finfoUrl = builder.getFinfoURL(location);
			String response = httpGet.sendGetRequest(finfoUrl.toString());
			return response;
		}
		System.err.println("no wmsLayer to pick defined in getFeatureInfo");
		return null;
	}

	public static class URLBuilder extends WMSTiledImageLayer.URLBuilder 
	{

		private final String layerNames;
		private final String wmsVersion;
		private final String crs;
		private final String wmsGetMap;
		public String URLTemplate;

		public URLBuilder(AVList params) {
			super(params);
			this.layerNames = params.getStringValue(AVKey.LAYER_NAMES);
			String version = params.getStringValue(AVKey.WMS_VERSION);
			wmsGetMap = params.getStringValue(AVKey.GET_MAP_URL);

			this.wmsVersion = version;
			this.crs = "&srs=EPSG:4326";

		}

		public URL getFinfoURL(Position pos) {
			// Buffer position
			final double eps = 0.000001D;
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(6);
			final double lat = pos.getLatitude().degrees;
			final double lon = pos.getLongitude().degrees;
			double lat_max = lat + eps;
			double lon_max = lon + eps;
			double lat_min = lat - eps;
			double lon_min = lon - eps;

			StringBuffer sb = new StringBuffer(wmsGetMap);
			sb.append("?service=wms");
			sb.append("&request=GetFeatureInfo");
			sb.append("&version=").append(this.wmsVersion);
			sb.append(this.crs);
			sb.append("&info_format=application/json");
			sb.append("&bbox=");
			sb.append(df.format(lon_min).replace(',', '.'));
			sb.append(",");
			sb.append(df.format(lat_min).replace(',', '.'));
			sb.append(",");
			sb.append(df.format(lon_max).replace(',', '.'));
			sb.append(",");
			sb.append(df.format(lat_max).replace(',', '.'));
			sb.append("&layers=" + layerNames);
			sb.append("&query_layers=" + layerNames);
			sb.append("&width=1");
			sb.append("&height=1");
			if (this.wmsVersion == "1.1.1" || this.wmsVersion == "1.1.0") {
				sb.append("&x=0");
				sb.append("&y=0");
			} else if (this.wmsVersion == "1.3.0") {
				sb.append("&i=0");
				sb.append("&j=0");
			} else {
				System.err
						.println("WMS version not supported bu getFeatureInfo class");
				return null;
			}
			sb.append("&feature_count=50");
			try {
				return new URL(sb.toString());
			} catch (MalformedURLException e) {
				System.err.println("Malformed URL in PikableWMSlayer");
				e.printStackTrace();
			}
			return null;
		}
	}

	private class PickableLayerMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			try {
				if (enablePicking) {					
					String result = getFeatureInfo(WWJApplet.getWWD().getCurrentPosition());
					if (result != null
							&& !result
							.equals("{\"type\":\"FeatureCollection\",\"features\":[]}")) {						
						e.consume();
						ga.setPosition(WWJApplet.getWWD().getCurrentPosition());
						ga.setAltitudeMode(1); // 1 calmp to ground //2 relative
						// to ground
						String textTovisualise = parseGetfeatureJsonNoiseRespone(result);
						ga.setText(textTovisualise);
						WWJApplet.annotationLayer.addAnnotation(ga);
					} else {
						WWJApplet.annotationLayer.removeAnnotation(ga);
					}
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public String parseGetfeatureJsonNoiseRespone(String result) {
		try {
			JSONParser parser = new JSONParser();
			Object output = parser.parse(result);
			JSONObject root = (JSONObject) output;
			JSONArray features = (JSONArray) root.get("features");
			JSONObject aa = (JSONObject)features.get(0);			
			JSONObject props = (JSONObject) aa.get("properties");
			String parsedResult = "Mean dB value: "
					+  props.get("meandb") + "\nMin dB value: "
					+ props.get("mindb") + "\nMax dB value: "
					+ props.get("maxdb") + "\nNumer of measurements: "
					+ props.get("meas_size") + "\nStart time: "
					+ props.get("start_time") + "\nEnd time: "
					+ props.get("end_time");
			
			 return parsedResult;
		} catch (ParseException e) {
			System.err.println("Problems parsing GetFeatureInfo response");
			e.printStackTrace();
		}
		return null;
	}
}