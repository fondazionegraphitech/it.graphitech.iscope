package openLS;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
/**
 * This class extends basic WW Position, is possible to specify an instruction for each @OpenLSPosition, the instruction 
 * will indicate human readable instruction to follow the path. If instructions are null it means this position does not 
 * have an instruction, only certain positions will have an instruction string (For example when changing road or when 
 * there is a significative change in the road direction)
 * @author f.devigili
 *
 */
public class OpenLSPosition extends Position
{
	String routeInstructions = null;
	
	public String getRouteInstructions()
	{
		return routeInstructions;
	}

	public void setRouteInstructions(String routeInstructions)
	{
		this.routeInstructions = routeInstructions;
	}

	public OpenLSPosition(Angle latitude, Angle longitude, double elevation)
	{
		super(latitude, longitude, elevation);
	}

	public static OpenLSPosition fromDegrees(double latitude, double longitude, double elevation)
	{
		return new OpenLSPosition(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), elevation);
	}

	public static OpenLSPosition fromPosition(Position p)
	{
		return new OpenLSPosition(p.getLongitude(), p.getLatitude(), p.getElevation());
	}

	public String toString()
	{
		return getLatitude().getDegrees() + " " + getLongitude().getDegrees();
	}
}
