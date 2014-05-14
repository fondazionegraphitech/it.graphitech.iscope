package openLS;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import httpConnection.httpGet;
import iScope.Props;
import iScope.WWJApplet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import picking.RoutingPickingListener;
import picking.RoutingPickingManager;
import picking.RoutingPointsIcon;

public class OpenLSManager implements RoutingPickingListener
{
	WorldWindowGLCanvas wwd = WWJApplet.getWWD();
	public String openLSServer = Props.getStr("openLSServer");
	// The default Preferences are loaded automatically for each request.
	private static OpenLSRequestPreferences openLSDefaultRequestPreferences = new OpenLSRequestPreferences();
	private static OpenLSLayer oLSRenderableLayer = new OpenLSLayer();
	
	

	public OpenLSManager()
	{
		WWJApplet.getApplet();
		WWJApplet.routingPickingManager.addRoutingPickingListener(this);
		WWJApplet.getWWD().getModel().getLayers().add(oLSRenderableLayer);
	}

	static public OpenLSRequestPreferences getDefaultPreferences()
	{
		return openLSDefaultRequestPreferences;
	}

	static public void setDefaultPreferences(OpenLSRequestPreferences openLSDefaultRequestPreferences)
	{
		OpenLSManager.openLSDefaultRequestPreferences = openLSDefaultRequestPreferences;
	}

	public void doRequest(final List<OpenLSPosition> positions)
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				if (WWJApplet.DEBUG)
				{
					if (Props.getBool("debugMode"))System.out.println("Route Request:");
					for (OpenLSPosition openLSPosition : positions)
					{
						if (Props.getBool("debugMode"))System.out.println(openLSPosition);
					}
				}
				if (WWJApplet.DEBUG)
					if (Props.getBool("debugMode"))System.out.println("Creating Request...");
				OpenLSRequest request = new OpenLSRequest(openLSServer);
				if (WWJApplet.DEBUG)
					if (Props.getBool("debugMode"))System.out.println("Creating Preferences...");
				OpenLSRequestPreferences openLSRequestPreferences = openLSDefaultRequestPreferences.clone();
				openLSRequestPreferences.positions.addAll(positions);
				OpenLSParsedResponse response = request.doRequest(openLSRequestPreferences);
				if (response != null)
				{
					oLSRenderableLayer.updateRoute(response.getPositions());
				   WWJApplet.getWWD().redraw();
					//   WWJApplet.routingPickingManager.iconLayer.addIcon( new RoutingPointsIcon(true, response.getPositions().getFirst()));
					//   WWJApplet.routingPickingManager.iconLayer.addIcon( new RoutingPointsIcon(true, response.getPositions().getLast()));

				   doInstructionRequest(response);
				}
			}

			private void doInstructionRequest(OpenLSParsedResponse response)
			{
			   String instructionServiceTemplate = Props.getStr("instructionServiceTemplate");
			   String instructionServer = Props.getStr("instructionServer");
			   String instructionRequest = String.format(instructionServiceTemplate, ""+response.responseID);
			   if (Props.getBool("debugMode"))System.out.println("Instruction request: " + instructionRequest);
				try
				{
					String instructionResponse = httpGet.sendGetRequest(instructionServer+instructionRequest);
					WWJApplet.jS.call("getSemanticInstructions", new Object[] { instructionResponse });
					System.out.println(instructionResponse);
				} catch (Exception e)
				{
					if (Props.getBool("debugMode"))System.err.println("Error while requesting instruction from Instruction Service.");
				}
			}
		});
		t.start();
	}
	
	public void doCalculatePickRequest()
	{
		lsPositions.clear();
		for (RoutingPointsIcon routingPointsIcon : RoutingPickingManager.routingPointsAnnotations)
		{
			OpenLSPosition lsPosition = OpenLSPosition.fromPosition(routingPointsIcon.getPosition());
			lsPositions.add(lsPosition);
		}
		doRequest(lsPositions);
	}
	
	private List<OpenLSPosition> lsPositions = new LinkedList<>();
	@Override
	public void PickChanged(RoutingPointsIcon iconChanged, List<RoutingPointsIcon> routingPointsIcons)
	{
		lsPositions.clear();
		for (RoutingPointsIcon routingPointsIcon : routingPointsIcons)
		{
			OpenLSPosition lsPosition = OpenLSPosition.fromPosition(routingPointsIcon.getPosition());
			lsPositions.add(lsPosition);
		}
		
		//doRequest(lsPositions);
	}

	public void clearPolyLine()
	{
		try
		{
		oLSRenderableLayer.openLSPositions.clear();
		oLSRenderableLayer.updateRoute(new ArrayList<OpenLSPosition>());
		} catch (Exception e)
		{
			System.out.println("No route to clear yo.");
		}
		
	}
}
