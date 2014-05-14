package picking;

import iScope.WWJApplet;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.UserFacingIcon;

public class RoutingPointsIcon extends UserFacingIcon
{
	AnnotationAttributes annotationInitialAttributes; // se voglio cambio quetsi
	AnnotationAttributes higlightedAnnotationAtributes; // se voglio cambio questi
	RoutingPickingManager routingPickingManager;

	public RoutingPointsIcon(RoutingPickingManager routingPickingManager, boolean isFirst, Position position)
	{
		
		super(isFirst ? "media/start.png" : "media/end.png", new Position(position.getLatitude(),position.getLongitude(),0));
		this.routingPickingManager = routingPickingManager;
		this.setHighlightScale(1.2);
		// this.set
		// setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
	}
	
	public RoutingPointsIcon( boolean isFirst, Position position)
	{
		
		super(isFirst ? "media/start.png" : "media/end.png", new Position(position.getLatitude(),position.getLongitude(),0));
		this.setHighlightScale(1.2);
		this.routingPickingManager = WWJApplet.routingPickingManager;
		// setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
	}
}
