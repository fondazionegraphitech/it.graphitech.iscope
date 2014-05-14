package Pilot;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.BasicLayerFactory;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.OGCCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.terrain.AbstractElevationModel;
import gov.nasa.worldwind.terrain.BasicElevationModel;
import gov.nasa.worldwind.terrain.BasicElevationModelFactory;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwind.terrain.LocalElevationModel;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Timer;

import nFinteractionManager.ImportExportManager;

public class PilotManager
{
	private List<Pilot> pilotsListInConfigFIle = new ArrayList<>();
	public static Pilot currentPilot;
	private Layer currentOrthoLayer;
	private WorldWindowGLCanvas wwd;
	private double initZoom = 10000d;
	private double initHeading = 0d;
	private double initPithc = 10d;
	double minZoom = 300d;
	double maxZoom = 10000d;
	double minPinch = 0d;
	double maxPinch = 75d;
	public static ImportExportManager importExportManager;
	
	Timer downloadTimer;

	public PilotManager()
	{
		wwd = WWJApplet.getWWD();
		importExportManager = new ImportExportManager();
	}

	HashMap<String, Pilot> pilots = new HashMap<>();
	private float maxNumRetrieversPending;
	protected int lastNumRetrieversPending;
	protected boolean firstin;

	
	
	public void loadPilot(String pilotname)
	{		
		if (currentPilot != null)
		currentPilot.unload3DLayer();
		if (pilots.containsKey(pilotname))
		{
			currentPilot = pilots.get(pilotname);
			currentPilot.load3DLayer();
		} else
		{
			currentPilot = new Pilot(pilotname);
			pilots.put(pilotname, currentPilot);					
		}
		Sector availableData = getPilotExtent(currentPilot);		
		if (availableData != null)
		{
			setEyePosAndLimitView(availableData,initZoom, initHeading, initPithc, minZoom, maxZoom, minPinch, maxPinch);
		}
		if(Props.getBool("loadPilotCustomOrtophoto")){
			System.out.println("loading custom ortoPhoto. Pilot: " + pilotname);
			setOrtholayer(currentPilot);
		}
        // 		 terrain
		if(Props.getBool("addPilotCustomTerrain")){
			System.out.println("Adding custom terrain model to the existing one");
			try {
				addCustomTerrainModel(currentPilot);
			} catch (Exception e) {
				System.err.println("Unable to load custom elevation model for pilot " + pilotname);
				e.printStackTrace();
			}		
		}			
		if(Props.getBool("loadOnlyPilotCustomTerrain")){
			System.out.println("Loading only custom terrain model");
					try {						
						setNewTerrainModel(currentPilot);
					} catch (Exception e) {
						System.err.println("Unable to add custom elevation model for pilot " + pilotname);
						e.printStackTrace();
					}		
				}
		firstin = true;		
		lastPerc=0;
		downloadTimer = new Timer(50, actionListener);	
		downloadTimer.start();
	}
	
	
	float lastPerc=0;
	public ActionListener actionListener = new ActionListener()
	{		
		public void actionPerformed(java.awt.event.ActionEvent actionEvent)
		{
			//lock the applet until all data has been downloaded			
			if (WorldWind.getRetrievalService().hasActiveTasks())
			{	
				if (firstin){
					maxNumRetrieversPending = WorldWind.getRetrievalService().getNumRetrieversPending();
					firstin = false;					
				}
					float perc = (maxNumRetrieversPending-(float)WorldWind.getRetrievalService().getNumRetrieversPending())/(float)maxNumRetrieversPending*100;
					if(perc > lastPerc){										
						WWJApplet.getJS().call("progressBarUpdate", new Object[] { perc });	
						lastPerc = perc;
					}
					 
			}
			else{
				System.out.println("Pilot " + currentPilot.getName() + " loaded");
				WWJApplet.getJS().call("progressBarHide",null);
				downloadTimer.stop();
			}
		}
	};
		

	private void removeLastPilot()
	{
		// wwd.getModel().getGlobe().setElevationModel(new BasicElevationModel());
	}

	public Pilot getCurrentPilot()
	{
		return currentPilot;
	}

	@SuppressWarnings("unused")
	private void addCustomTerrainModel(Pilot pilot)
	{
		AbstractElevationModel em = createEM(pilot);
		if(em!=null && wwd.getModel().getGlobe().getElevationModel() instanceof CompoundElevationModel){
			CompoundElevationModel compundModel = (CompoundElevationModel) wwd.getModel().getGlobe().getElevationModel();
			compundModel.addElevationModel(em);
			wwd.getModel().getGlobe().setElevationModel(compundModel);
			System.out.println("Eleveation model of pilot "+ pilot.getName() + " updated");			
		} else
			System.out.println("the EM in use is not of compound type");
	}


	private AbstractElevationModel createEMfromLayer(Layer layer, OGCCapabilities caps) {		
		try {
			AVList params = new AVListImpl();
			params.setValue(AVKey.LAYER_NAMES, layer.getName());
			BasicElevationModelFactory emf = new BasicElevationModelFactory();
			AbstractElevationModel em = (AbstractElevationModel) emf.createFromConfigSource(caps, params);	
			return em;	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return null;
	}

	@SuppressWarnings("unused")
	private void setNewTerrainModel(Pilot pilot)
	{
		AbstractElevationModel em = createEM(pilot);
		if(em!=null)	wwd.getModel().getGlobe().setElevationModel(em);
		else {
			System.err.println("Problems loading custom EM");
		}
	}

	private AbstractElevationModel createEM(Pilot pilot)
	{		
		if (pilot.getDtmConfigFileName() != null)
		{
			BasicElevationModelFactory emf = new BasicElevationModelFactory();
			AbstractElevationModel em = (AbstractElevationModel) emf.createFromConfigSource(pilot.getDtmConfigFileName(), null);	
			return em;
		} else
			System.err.println("No elevation model set in props file");
		return null;
	}
	
	private AbstractElevationModel createEMfromWmsURL(Pilot pilot)
	{		
		try {
			WMSCapabilities caps =  WMSCapabilities.retrieve(new URI(Props.getStr("pilotGeoServer") + pilot.getName() + "/wms"));			
			AVList params = new AVListImpl();
			params.setValue(AVKey.LAYER_NAMES, "CLES:CLES_DTM_4326");
			BasicElevationModelFactory emf = new BasicElevationModelFactory();
			AbstractElevationModel em = (AbstractElevationModel) emf.createFromConfigSource(caps, params);	
			return em;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return null;
	}

	private void setOrtholayer(Pilot pilot)
	{
		if (currentOrthoLayer != null)
		{ // rimuovo il vecchio
			wwd.getModel().getLayers().remove(currentOrthoLayer);
		}
		if (pilot.getOrtoPhotoLayerCOnfigFileName() != null)
		{
			BasicLayerFactory basicLayerFactory = new BasicLayerFactory();
			currentOrthoLayer = (Layer) basicLayerFactory.createFromConfigSource(pilot.getOrtoPhotoLayerCOnfigFileName(), null); //
			currentOrthoLayer.setEnabled(true);
			wwd.getModel().getLayers().add(currentOrthoLayer);
		} else
			System.err.println("no Ortophoto layer set");
	}

	private Sector getPilotExtent(Pilot pilot)
	{
		if (createEM(pilot) instanceof CompoundElevationModel)
		{
			CompoundElevationModel cem = (CompoundElevationModel) createEM(pilot);
			BasicElevationModel em = (BasicElevationModel) cem.getElevationModels().get(0);
			LevelSet levels = em.getLevels();
			return levels.getSector();
		} else if (createEM(pilot) instanceof BasicElevationModel)
		{
			BasicElevationModel em = (BasicElevationModel) createEM(pilot);
			LevelSet levels = em.getLevels();
			return levels.getSector();
		} else
			System.err.println("Problems retriving the elevation model extent");
		return null;
	}
	
	
	public void setEyePosAndLimitView(Sector availableDatSector, double zoom, double heading, double pitch, double minZoom, double maxZoom, double minPinch, double maxPinch){
		// limito ad una certa area esplorabile
		System.out.println(availableDatSector.getCentroid().getLatitude().degrees + " " + availableDatSector.getCentroid().getLongitude().degrees);
//		WWJApplet.jS.setViewPosition(availableDatSector.getCentroid().getLatitude().degrees, availableDatSector.getCentroid().getLongitude().degrees, computeZoomForExtent(availableDatSector));
		WWJApplet.jS.setViewPosition(availableDatSector.getCentroid().getLatitude().degrees, availableDatSector.getCentroid().getLongitude().degrees, zoom);
		if(Props.getBool("limitView")){
				limitView(availableDatSector, minZoom, maxZoom, minPinch, maxPinch);
	    }
	}

	/**
	 * Move the camera to a location and heading and limit view to a bounding box
	 */
	public void goToAndLimitView(Sector availableDatSector, double zoom, double heading, double pitch, double minZoom, double maxZoom, double minPinch, double maxPinch)
	{
		// limito ad una certa area esplorabile
		System.out.println(availableDatSector.getCentroid().getLatitude().degrees + " " + availableDatSector.getCentroid().getLongitude().degrees);
		WWJApplet.jS.gotoLatLon(availableDatSector.getCentroid().getLatitude().degrees, availableDatSector.getCentroid().getLongitude().degrees, zoom, heading, pitch);
		limitView(availableDatSector, minZoom, maxZoom, minPinch, maxPinch);
	}

	public void limitView(Sector limitViewSector, double minZoom, double maxZoom, double minPinch, double maxPinch)
	{
		BasicOrbitView tempOrbit = (BasicOrbitView) wwd.getView();
		OrbitViewLimits tempOrbitViewLimits = tempOrbit.getOrbitViewLimits();
		tempOrbitViewLimits.setCenterLocationLimits(limitViewSector);
		tempOrbitViewLimits.setZoomLimits(minZoom, maxZoom);
		tempOrbitViewLimits.setPitchLimits(Angle.fromDegrees(minPinch), Angle.fromDegrees(maxPinch));
		BasicOrbitViewLimits.applyLimits(tempOrbit, tempOrbitViewLimits);		
	}

	public static double computeZoomForExtent(Sector sector)
	{
		Angle delta = sector.getDeltaLat();
		if (sector.getDeltaLon().compareTo(delta) > 0)
			delta = sector.getDeltaLon();
		double arcLength = delta.radians * Earth.WGS84_EQUATORIAL_RADIUS;
		double fieldOfView = Configuration.getDoubleValue(AVKey.FOV, 45.0);
		return arcLength / (2 * Math.tan(fieldOfView / 2.0));
	}

	public List<Pilot> getPilotsListInConfigFIle()
	{
		return pilotsListInConfigFIle;
	}

}
