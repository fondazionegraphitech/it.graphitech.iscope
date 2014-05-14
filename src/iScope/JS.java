package iScope;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import httpConnection.httpPost;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;

import nFinteractionManager.Export;
import netscape.javascript.JSObject;
import noise.NoiseManager;
import openLS.OpenLSManager;
import openLS.OpenLSParsedResponse;
import openLS.OpenLSPosition;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import wfs.WfsManager;
import wms.PickableWMSLayer;
import Pilot.PilotManager;
import Tiled3dLayer.PickSupport;
import Tiled3dLayer.Tiled3DLayerRenderer;

public class JS
{
	private JSObject jso;
	private WWJApplet applet;
	private WorldWindowGLCanvas wwd;		
	private JSONParser jsonParser; 	
	PickableWMSLayer pickableWMSLayer;
	boolean osmEnable = false;
	Layer osm;
	private WfsManager wfsManager;
	IconLayer iconLayer = new IconLayer();

	/**
	 * Initialize  the Javascript interface
	 */
	public JS()
	{
		wwd = WWJApplet.getWWD();		
		System.out.println("Initialising JSObject...");
		applet = WWJApplet.getApplet();
		System.out.println("Applet obj found");
		jsonParser = new JSONParser();
		try
		{
			jso = JSObject.getWindow(applet);
//			jso.call("appletInit", null);
		} catch (Throwable e)
		{
			System.out.println("Running from eclipse, JSObject not initialized.");
		}	
		pickableWMSLayer = new PickableWMSLayer();  //serve per aggiungere mouse listener
		osm = WWJApplet.getWWD().getModel().getLayers().getLayerByName("Open Street Map Nick");		
		wfsManager = new WfsManager();
		wwd.getModel().getLayers().add(iconLayer);
	}
	
	/**
	 * Call a function on the javascript file.
	 * @param method
	 * @param parameters
	 */
	public void call(String method, Object[] parameters)
	{
		System.out.println("Calling: " + method + " With parameters: ");
		if (parameters != null)
		{
			for (Object object : parameters)
			{
				System.out.println("Parameter: " + object);
			}
		}
		if (jso != null)
		{
			jso.call(method, parameters);
		} else
		{
			System.err.println("Warning: Javascript interface is not initialized.");
		}
	}
	
	/**
	 * Enable solar assessment picking
	 * @param method
	 * @param parameters
	 */
	public void newsolarAssesmentPicking()
	{
		PickSupport.enableBuildingPickingOnApplet();
	}

	/**
	 * Load a pilot from the configuration file
	 * @param pilotname
	 */
	public void loadPilot(String pilotname)
	{
		System.out.println("Loading pilot " + pilotname);
		toggle3D(true);  //riabilito il 3d se non lo era		
		try {
			WWJApplet.getPilotManager().loadPilot(pilotname);
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}

	/**
	 * Does something i don't really understand but prevents the applet from asking for permissions
	 */
	public void fakeAction()
	{
		System.out.println("Fake action");
		wwd.redrawNow();
	}

	/**
	 * Inserts a layer on WWD Layer List before another layer by name
	 * @param wwd
	 * @param layer
	 * @param targetName
	 */
	public void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName)
	{
		LayerList layers = wwd.getModel().getLayers();
		int targetPosition = layers.size() - 1;
		for (Layer l : layers)
		{
			if (l.getName().indexOf(targetName) != -1)
			{
				targetPosition = layers.indexOf(l);
				break;
			}
		}
		layers.add(targetPosition, layer);
	}

	/**
	 * Move the camera to a certain lat-lon
	 * @param lat
	 * @param lon
	 */
	public void gotoLatLon(double lat, double lon)
	{
		gotoLatLon(lat, lon, Double.NaN, 0, 0);
	}

	/**
	 * Move the camera to a certain lat-lon with determined heading
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @param heading
	 * @param pitch
	 */
	public void gotoLatLon(double lat, double lon, double zoom, double heading, double pitch)
	{
		BasicOrbitView view = (BasicOrbitView) wwd.getView();
		if (!Double.isNaN(lat) || !Double.isNaN(lon) || !Double.isNaN(zoom))
		{
			lat = Double.isNaN(lat) ? view.getCenterPosition().getLatitude().degrees : lat;
			lon = Double.isNaN(lon) ? view.getCenterPosition().getLongitude().degrees : lon;
			zoom = Double.isNaN(zoom) ? view.getZoom() : zoom;
			heading = Double.isNaN(heading) ? view.getHeading().degrees : heading;
			pitch = Double.isNaN(pitch) ? view.getPitch().degrees : pitch;
			view.addPanToAnimator(Position.fromDegrees(lat, lon, 0), Angle.fromDegrees(heading), Angle.fromDegrees(pitch), zoom, true);
		}
	}
	
	
	UserFacingIcon facingIcon;
	
	public void addPlacemark(double lat, double lon)
	{	
		if (facingIcon == null) facingIcon = new UserFacingIcon("media/pimpignegol.png", Position.fromDegrees(lat,lon));			
		facingIcon.setPosition(Position.fromDegrees(lat,lon));
		facingIcon.setVisible(true);
		iconLayer.addIcon(facingIcon);
	}
		
		/**
	 * Sets heading and pitch of the camera
	 * @param heading
	 * @param pitch
	 */
	public void setHeadingAndPitch(double heading, double pitch)
	{
		BasicOrbitView view = (BasicOrbitView) wwd.getView();
		heading = Double.isNaN(heading) ? view.getHeading().degrees : heading;
		pitch = Double.isNaN(pitch) ? view.getPitch().degrees : pitch;
		view.addHeadingPitchAnimator(view.getHeading(), Angle.fromDegrees(heading), view.getPitch(), Angle.fromDegrees(pitch));
	}

	/**
	 * Sets pitch of the camera
	 * @param pitch
	 */
	public void setPitch(double pitch)
	{
		BasicOrbitView view = (BasicOrbitView) wwd.getView();
		pitch = Double.isNaN(pitch) ? view.getPitch().degrees : pitch;
		view.addPitchAnimator(view.getPitch(), Angle.fromDegrees(pitch));
	}

	/**
	 * Sets Zoom of the camera
	 * @param zoom
	 */
	public void setZoom(double zoom)
	{
		BasicOrbitView view = (BasicOrbitView) wwd.getView();
		if (!Double.isNaN(zoom))
		{
			view.addZoomAnimator(view.getZoom(), zoom);
		}
	}

	/**
	 * Set position of current view in World Coordinates
	 * @param d
	 * @param e
	 * @param f
	 */
	public void setViewPosition(double lat, double lon, double elev)
	{
		wwd.getView().setEyePosition(new Position(Angle.fromDegrees(lat), Angle.fromDegrees(lon), elev));
	}

	/**
	 * Return a layer by the name
	 * @param layerName
	 * @return
	 */
	public Layer getLayerByName(String layerName)
	{
		for (Layer layer : wwd.getModel().getLayers())
		{
			if (layer.getName().indexOf(layerName) != -1)
				return layer;
		}
		return null;
	}

	/**
	 * Enable a layer by name
	 * @param layerName
	 */
	public void enableLayer(String layerName)
	{
		for (Layer layer : wwd.getModel().getLayers())
		{
			if (layer.getName().equals(layerName))
			{
				layer.setEnabled(true);
				applet.activeLayers.add(layer);
				// aggiungo alla lista di layer attivi
				System.out.println("Layer " + layerName + " ON");
			}
		}
	}
	
	/**
	 *  Switch visibility of a layer by name
	 * @param layerName
	 */
	public void switchLayerStatus(String layerName)
	{
		for (Layer layer : wwd.getModel().getLayers())
		{
			if (layer.getName().equals(layerName))
			{
				if (!layer.isEnabled())
				{
					applet.activeLayers.add(layer);
					// aggiungo alla lista di layer attivi
				} else
				{
					if (applet.activeLayers.contains(layer))
					{
						applet.activeLayers.remove(layer);
						// rimuovo alla lista di layer attivi
					}
				}
				layer.setEnabled(!layer.isEnabled());
				System.out.println("Layer " + layerName + " is enabled: " + layer.isEnabled());
			}
		}
	}


	/**
	 * Clear noise points on the noise manager
	 */
	public void clearNoisePoints()
	{
		System.out.println("Reset punctual Noise layers");
		WWJApplet.getApplet().noiseManager.clearNoiseMarkers();
		WWJApplet.getWWD().redraw();
	}	
	
	
	/**
	 * Start solar selector to reaquest a new 
	 * */
	public void newsolarAssesmentSelector()
	{
		System.out.println("New solar selector");
		
		if(WWJApplet.GUI.solarSelector.isEnable()){
			WWJApplet.GUI.solarSelector.disable();			
		}
		WWJApplet.GUI.solarSelector.enable();		
	}
	
	public String getSolarSelectorExtent(){
		String out =  WWJApplet.GUI.solarSelector.getSector().toString();
		WWJApplet.GUI.solarSelector.disable();
//		return out;		
		return "663700,5105450,663950,5105700";
	}

	/**
	 * Set all layers 
	 */
	public void clearActiveLayersList()
	{
		if (!applet.activeLayers.isEmpty())
		{
			for (Layer layer : applet.activeLayers)
			{
				WWJApplet.getWWD().getModel().getLayers().remove(layer);			}
		}
		applet.activeLayers.clear(); // resetto la lista dey layer attivi
	}

	/**
	 * Change current scenario, resets all things so the system is ready to load a new scenario
	 * Scenario numbers: 1: 2: 3: 0:None
	 */


	public void switchScenario(int scenarioNumber)
	{
		
		System.out.println("Java method called: switchScenario. Param passed: " + scenarioNumber);
		
		if (facingIcon != null)
			facingIcon.setVisible(false);
		
		if (scenarioNumber == 1){ //active = solar			
			System.out.println("Java method called:: switchScenario. Active scenario SOLAR");		
			WWJApplet.GUI.noiseSelector.disable();
			pickableWMSLayer.disablePicking();
			clearActiveLayersList();
			clearNoisePoints();		
			stopRoutePicking();			
			if(osm.isEnabled()){
				osm.setEnabled(false);
			}
			
		}
		else if (scenarioNumber == 2){ //active = noise
			System.out.println("Java method called:: switchScenario. Active scenario NOISE");			
			WWJApplet.GUI.solarSelector.disable();			
//		    pickableWMSLayer.disablePicking();
			clearActiveLayersList();			
			stopRoutePicking();
			if(osm.isEnabled()){
				osm.setEnabled(false);
			}
			
		}
        else if (scenarioNumber == 3){ //active = routing
        	System.out.println("Java method called:: switch"
        			+ "Scenario. Active scenario ROUTING");        	
        	WWJApplet.GUI.solarSelector.disable();
        	WWJApplet.GUI.noiseSelector.disable();
    	    pickableWMSLayer.disablePicking();
    		clearActiveLayersList();
    		clearNoisePoints();	
    		osm.setEnabled(true);	
		}   		
	}

	/**
	 * 
	 */
	public void toggle3D( boolean active )
	{
		System.out.println("Java method called:: toggle3D");
		Tiled3DLayerRenderer.is3DActive = active;
	}

	/**
	 * Disable a layer
	 * @param layerName
	 */
	public void disableLayer(String layerName)
	{
		System.out.println("Java method called:: disableLayer");
//		Layer l = applet.activeLayers.getLayerByName(layerName);
		Layer l = WWJApplet.getWWD().getModel().getLayers().getLayerByName(layerName);
		l.setEnabled(false);
		applet.activeLayers.remove(l);
		System.out.println("Layer " + layerName + " disabled");
	}

	/**
	 * Remove a layer
	 * @param layerName
	 */
	public void removeLayer(String layerName)
	{
		System.out.println("Java method called:: removeLayer");
		disableLayer(layerName);
		Layer l = wwd.getModel().getLayers().getLayerByName(layerName);
		wwd.getModel().getLayers().remove(l);
		System.out.println("Layer " + layerName + " removed");
	}

	/**
	 * Sets opacity of a layer
	 * @param layerName
	 * @param opacity
	 */
	public void setLayerOpacity(String layerName, String opacity)
	{
		System.out.println("Java method called:: setLayerOpacity");
		Layer l = wwd.getModel().getLayers().getLayerByName(layerName);
		l.setOpacity(Double.parseDouble(opacity));
		System.out.println("Opacity of layer " + layerName + " :" + opacity);
	}

	/**
	 * Report view parameters on applet console
	 */
	public void writeViewParametersOnConsole()
	{
		System.out.println("Java method called:: writeViewParametersOnConsole");
		BasicOrbitView view = (BasicOrbitView) wwd.getView();
		double lat = view.getCenterPosition().getLatitude().degrees;
		double lon = view.getCenterPosition().getLongitude().degrees;
		double zoom = view.getZoom();
		double heading = view.getHeading().degrees;
		double pitch = view.getPitch().degrees;
		System.out.println("lat:" + lat);
		System.out.println("lon:" + lon);
		System.out.println("zoom:" + zoom);
		System.out.println("heading:" + heading);
		System.out.println("pitch:" + pitch);
	}

	/**
	 * Returns layer list to the javascript
	 */
	public void getLayersList()
	{
		System.out.println("Java method called:: getLayersList");
		System.out.println("Layers List");
		String tempString = "";
		for (Layer layer : wwd.getModel().getLayers())
		{
			tempString = tempString + layer.getName() + " ";
			System.out.println(layer.getName());
		}
		if (jso != null)
			try
			{
				jso.call("updateWebPage", new String[]
				{ tempString });
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
	}

	//NOISE INTERFACE
	public void newNoiseSelector()
	{
		System.out.println("Java method called:: newNoiseSelector");
		if (WWJApplet.GUI.noiseSelector.isEnable()){
			WWJApplet.GUI.noiseSelector.disable();
		}
		WWJApplet.GUI.noiseSelector.enable();		
	}
	
	
	public String sendSecureNoiseRequest(final String user,final String tag,final String cityID, final String since,final String until,final String dbMax,final String dBMin)
	{
		System.out.println("Java method called:: sendSecureNoiseRequest with parameter: " +" usr:" + user + " tag: "+  tag + " CITYid:" + cityID + " SINCE:" + since + " UNTIL:"+ until + " DBmAX:" + dbMax + " DBmIN:" + dBMin);	
		String status = (String) AccessController.doPrivileged(new PrivilegedAction<Object>()
		{
			public Object run()
			{				
				if (WWJApplet.GUI.noiseSelector.getSector() == null || WWJApplet.GUI.noiseSelector.getSector().isSameSector(Sector.EMPTY_SECTOR) ){	
					System.err.println("Try to request noise point but the selector is null.");	
					return "You must draw an area on the terrain using mouse.";
				}		
				else{
					if(!WWJApplet.GUI.noiseSelector.tooBig()){
						NoiseManager noiseManager = WWJApplet.getApplet().noiseManager;
						boolean pointsExist = noiseManager.requestAndVisualisePunctualNoisePoints(WWJApplet.GUI.noiseSelector.getSector(),user, tag, cityID, since, until, dbMax, dBMin);
						WWJApplet.GUI.noiseSelector.disable();
						if(pointsExist) return "ok";
						else return "The selected area contains no values.";
					}
					else return "The selected area is too big.";
				}				
			}
		});			
		return status;
	}
	
	
	//ROUTING INTERFACE
	/**
	 * Questo metodo viene chiamato dala pagina web per abilitare il picking del routing, l'utente dovrà selezionare 2 
	 * punti e successivamente avrà la possibilità di trascinarli.
	 */
	public void startRoutePicking()
	{
		System.out.println("Java method called:: startRoutePicking");	

		WWJApplet.getApplet().openLSManager.clearPolyLine();
		WWJApplet.routingPickingManager.clearPositions();
		WWJApplet.routingPickingManager.startPicking();
	}
	/**
	 * Questo metodo viene chiamato da JS per disabilitare il picking del routing dopo che la pagina web del portale
	 * ha cambiato la tab attiva.
	 */
	public void stopRoutePicking()
	{
		System.out.println("Java method called:: stopRoutePicking");
		WWJApplet.routingPickingManager.stopPicking();
		WWJApplet.getApplet().openLSManager.clearPolyLine();
	}
	/**
	 * Questo metodo viene chiamato dalla pagina web per cambiare la tipologia di routing per il picking
	 * 
	 * @param requestCoordinates String containing 4 double values to split [lat lon lat lon]
	 */
	public void doRoutingPickRequest( String requestPreference, String surfaceQuality, String maxSlope, String tactilePaving )
	{
		OpenLSManager.getDefaultPreferences().routePreference = requestPreference;
		OpenLSManager.getDefaultPreferences().surfaceQuality = surfaceQuality;
		OpenLSManager.getDefaultPreferences().maxSlope = maxSlope;
		OpenLSManager.getDefaultPreferences().tactilePavingRequired = tactilePaving;
		
		WWJApplet.getApplet().openLSManager.doCalculatePickRequest();
	}
	
	public void doRoutingRequest( String requestCoordinates, String requestPreference, String surfaceQuality, String maxSlope, String tactilePaving )
	{
		System.out.println("Java method called:: doRoutingRequest");
		String[] coords = requestCoordinates.split(",");
		List<OpenLSPosition> positions = new LinkedList<>();
		positions.add( new OpenLSPosition(Angle.fromDegrees(Double.parseDouble(coords[0])), Angle.fromDegrees(Double.parseDouble(coords[1])), 0));
		positions.add( new OpenLSPosition(Angle.fromDegrees(Double.parseDouble(coords[2])), Angle.fromDegrees(Double.parseDouble(coords[3])), 0));
		
		OpenLSManager.getDefaultPreferences().routePreference = requestPreference;
		OpenLSManager.getDefaultPreferences().surfaceQuality = surfaceQuality;
		OpenLSManager.getDefaultPreferences().maxSlope = maxSlope;
		OpenLSManager.getDefaultPreferences().tactilePavingRequired = tactilePaving;
		
		WWJApplet.getApplet().openLSManager.doRequest(positions);
	}
	/**
	 * Questo metodo viene chiamato dal modulo OpenLS per ritornare alla pagina WEB i dati della richiesta di routing.
	 * dato che non tutte le @OpenLsPosition della risposta hanno effettivamente delle informazioni vengono aggiunte
	 * alla risposta per il sito solo quelle con informazioni effettive.
	 * 
	 * @param {@code OpenLSParsedResponse} response  Oggetto contenente tutti i dati della risposta di routing.
	 */
	public void reportRouteRequestComplete( OpenLSParsedResponse response)
	{
		System.out.println("Java method called:: reportRouteRequestComplete");
		LinkedList<OpenLSPosition> positions = response.getPositions();
		List<String> instructions = new LinkedList<>();
		for (OpenLSPosition openLSPosition : positions)
		{
			String instruction = openLSPosition.getRouteInstructions();
			if (instruction != null)
			instructions.add(instruction);
		}
		call("reportRouteRequestCompleted", instructions.toArray());
	}
	/**
	 * Questo metodo viene chiamato dal modulo di Picking per aggiornare la pagina web sulle coordinate di partenza
	 * e arrivo della richiesta di routing.
	 * 
	 * @param positions Le posizioni in latlon dei punti in cui la strada deve passare.
	 */
	public void reportRouteRequestChange( List<OpenLSPosition> positions )
	{
		System.out.println("Java method called:: reportRouteRequestChange");
		int s = positions.size();
		if ( s < 2 ) return;
		String startLat = positions.get(0).latitude.degrees + "";
		String startLon = positions.get(0).longitude.degrees + "";
		String destLat = positions.get(s-1).latitude.degrees + "";
		String destLon = positions.get(s-1).longitude.degrees + "";
		call("reportRouteRequestChange",new Object[] {startLat,startLon,destLat,destLon} );
	}
	/**
	 * 
	 * Quando l'utente finisce di "pickare" il secondo punto per il routing viene chiamato questo metodo per avvisare 
	 * l'applet che può effettuare la richiesta di routing.
	 * 
	 */
	public void reportRoutePickComplete()
	{
		System.out.println("Java method called:: reportRoutePickComplete");
		call("reportRoutePickComplete",null );
	}
	
	public void solarAssesmentRequest(boolean requestByBldID,
			boolean requestByBB, String buildingID, String bBminX,
			String bBminY, String bBmaxnX, String bBmaxY,
			String calculationStart, String calculationEnd,
			String aquisitionCostsP, String minRoofAreaP,
			String minIrradiationP, String panelCoverageP,
			String operationCostsP, String energyCostsP, String feedInTariffP,
			String efficiencyP, String CO2consumptionP,
			String transmissionLossP, String showRoofSurfaceP) {
		
		System.out.println("Java method called:: solarAssesmentRequest");
		String server = Props.getStr("solarAssesmentServer");
		String assResponse = null;

		try {
			String solarAssXML = solar.SolarAssesment
					.generateSolarAssesmentRequest(requestByBldID, requestByBB,
							buildingID, bBminX, bBminY, bBmaxnX, bBmaxY,
							calculationStart, calculationEnd, minIrradiationP,
							minRoofAreaP, panelCoverageP, operationCostsP,
							energyCostsP, feedInTariffP, CO2consumptionP,
							efficiencyP, aquisitionCostsP, transmissionLossP,
							showRoofSurfaceP);
			assResponse = httpConnection.httpPost.sendPostrequest(
					server, solarAssXML);
			 if (Props.getBool("debugMode")){
				 System.out.println(assResponse);
			 }			
		} catch (Throwable e) {
			e.printStackTrace();
		System.err.println("Problems sending solar assement WPS request");
		}
		if(assResponse != null)	call("parseSolarAssesmenrResult", new String[]{ assResponse });
	}
	
	
//	public void newExportSelection(boolean exportByTileId, boolean exportByBB){
//		System.out.println("Java method called:: newExportSelection");
//		nFinteractionManager.ImportExportManager.newExportSelection(exportByTileId, exportByBB);		
//	}

	
	public String confirmNewExport(String accountP, String selectedTileNumber, String namingPattern,  String lodsP, String lodmodeP, String format, boolean exportPointCloud ){
		String exportXML;		
		System.out.println("Java method called:: confirmNewExport");
		String productP = PilotManager.currentPilot.getnFproduct().getName();
		String srsP = String.valueOf(PilotManager.currentPilot.getnFproduct().getSrs());
		String subdivionID =  String.valueOf(PilotManager.currentPilot.getnFproduct().getSubId());
		try {
			exportXML = Export.createExportJobXML(productP, accountP, srsP,
					null, true, false, selectedTileNumber,
					namingPattern, "polygon", lodsP, subdivionID, lodmodeP, format, exportPointCloud);
			if (WWJApplet.DEBUG){
				System.out.println("REQUEST XML:");
				System.out.println(exportXML);				
			}
		} catch (Throwable e) {
			System.err.println("Error creating export XML");
			e.printStackTrace();
			return null;		
		}
		try {
	    	String resp = httpPost.sendExportPostrequest(Props.getStr("nFwps"), exportXML);
	    	if (WWJApplet.DEBUG){
	    		System.out.println("Response XML:");
	    		System.out.println(resp);			
	    	}
			return resp;
		} catch (IOException e) {
			System.err.println("Problem posting export xml request");
			e.printStackTrace();
			return null;
		}		
	}	

	
	public String generateAggregateNoiseMaps(String startDate, String endDate,String from, String to, String mapName){
		String response = null;
		System.out.println("Java method called:: generateAggregateNoiseMaps");
		if(startDate == null || endDate == null || mapName == null) {
			System.err.println("Missing values: generateAggregateMaps");
			return "";
		}
		if (WWJApplet.GUI.noiseSelector.getSector() == null || WWJApplet.GUI.noiseSelector.getSector().isSameSector(Sector.EMPTY_SECTOR) ){	
			System.err.println("Try to generate aggregate noise map but the selector is empty");
			return "";
		}
		String jSonServerResponse = WWJApplet.getApplet().noiseManager.generateAggregateMaps(WWJApplet.GUI.noiseSelector.getSector(),startDate, endDate, from, to, mapName);
		JSONObject obj;
		if (jSonServerResponse != "") {
			try {
				obj = (JSONObject) jsonParser.parse(jSonServerResponse);
				response = WWJApplet.GUI.noiseSelector.getSector().toString()
						.replace("°", "")
						+ "|" + obj.get("id");
				WWJApplet.GUI.noiseSelector.disable();
				return response;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;		
 	}
	
	
	Layer lastNoiselayer; 
	public void avarageNoiseMapDisplay(String layerName){
		System.out.println("Java method called:: avarageNoiseMapDisplay");
		System.out.println("MIPASSI: " + layerName);
		if(layerName.equals("") || layerName==null || layerName.equals("0")){
			System.out.println("QUI");
			if (lastNoiselayer != null)	WWJApplet.getWWD().getModel().getLayers().remove(lastNoiselayer);
			lastNoiselayer = null;
		}
		else{
			Layer layer = WWJApplet.getPilotManager().getCurrentPilot().getNoiseLayers().getLayerByName(layerName);		
			if (layer != null) {	
				if (lastNoiselayer != null)	WWJApplet.getWWD().getModel().getLayers().remove(lastNoiselayer);
				pickableWMSLayer.setLayerToQuery(layer);
				pickableWMSLayer.enablePicking();
				lastNoiselayer = layer;
				WWJApplet.getWWD().getModel().getLayers().add(layer);
				applet.activeLayers.add(layer);			
			}	
			else {
				System.err.println("Requested noise layer named " + layerName + " not found in currentpilot " + WWJApplet.getPilotManager().getCurrentPilot().getName());
			}
		}
	}
	
	Layer lastSolarlayer; 
	public void irradiationMapDisplay(String layerName){
		System.out.println("Java method called:: irradiationMapDisplay. Display the -"+ layerName +"- map");
		Layer layer = WWJApplet.getPilotManager().getCurrentPilot().getSolarLayers().getLayerByName(layerName);		
		if (layer != null) {	
			if (lastSolarlayer != null)	WWJApplet.getWWD().getModel().getLayers().remove(lastSolarlayer);
			lastSolarlayer = layer;
			WWJApplet.getWWD().getModel().getLayers().add(layer);
			applet.activeLayers.add(layer);
			}		
		else {
			System.err.println("Requested solar layer named " + layerName + " not found in currentpilot " + WWJApplet.getPilotManager().getCurrentPilot().getName());
		}
	}
	
	
	public void showCurrentPilotTiles(boolean b){		
		if (b){
			WWJApplet.getWWD().getModel().getLayers().add(PilotManager.currentPilot.getnFproduct().getTileLayer());	
			applet.activeLayers.add(PilotManager.currentPilot.getnFproduct().getTileLayer());
		}
		else{
			WWJApplet.getWWD().getModel().getLayers().remove(PilotManager.currentPilot.getnFproduct().getTileLayer());	
			applet.activeLayers.remove(PilotManager.currentPilot.getnFproduct().getTileLayer());			
		}
	}
	
	public String getCurrentPilotAcronim(){
		if(PilotManager.currentPilot!=null)	return PilotManager.currentPilot.getnFproduct().getName();	
		else{
			System.err.println("no cuurentPilot set");
			return null;
		}
	}
	
//	public void stop(){
//		System.out.println("Exiting applet");
//		// Shut down World Wind when the browser stops this Applet.
//	    WorldWind.shutDown();
//	    System.exit(0);
//	}	
	
	public void enablePickHighLight(){
		WWJApplet.enablePickHighLight = true;
	}
	
	
	public void retriveSolarInfos(boolean setTo){
		if (!setTo) {
			wfsManager.removeAllAnnotations();
			WWJApplet.getPilotManager().currentPilot.tredilayer.selectColor2 = new int[]{ 0, 0, 0, 0 };//rimuovo l'higlight 	
			}		
		WWJApplet.retriveWfsSolarInfos = setTo;
	}
	
	public void retriveBuildingInfos(boolean setTo){
		if (!setTo) {
			wfsManager.removeAllAnnotations();
			WWJApplet.getPilotManager().currentPilot.tredilayer.selectColor2 = new int[]{ 0, 0, 0, 0 };			
		}
		WWJApplet.retriveWfsBldInfos = setTo;
	}
	
}
