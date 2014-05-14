package openLS;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.Color;
import java.util.List;

public class OpenLSLayer extends RenderableLayer
{
	List<OpenLSPosition> openLSPositions;
	
	public OpenLSLayer()
	{
		super();
		this.setName(Props.getStr("OpenLSRouteLayerName"));
	}

	public void updateRoute(List<OpenLSPosition> openLSPositions)
	{
		this.openLSPositions = openLSPositions;
		Polyline routePolyline = new Polyline();
		routePolyline.setFollowTerrain(true);
		routePolyline.setPositions(this.openLSPositions);
		routePolyline.setPathType(AVKey.GREAT_CIRCLE);
		routePolyline.setAntiAliasHint(Polyline.ANTIALIAS_NICEST);
		routePolyline.setColor( new Color(102, 156, 207) );
		routePolyline.setLineWidth(4.0);
		this.renderables.clear();
		this.renderables.add(routePolyline);
		WWJApplet.getApplet();
		WWJApplet.routingPickingManager.setRoutePolyline(routePolyline);
	}

	public void moveViewToRoute() throws Exception
	{
		throw new Exception("Not Implemented");
	}
}
