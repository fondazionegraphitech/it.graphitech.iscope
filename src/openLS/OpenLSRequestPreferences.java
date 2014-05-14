package openLS;

import iScope.Props;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpenLSRequestPreferences
{
	public List<OpenLSPosition> positions = new ArrayList<OpenLSPosition>();
	public boolean useRealTimeTraffic = Props.getBool("useRealTimeTraffic");
	public Date expectedStartTime = new Date(System.currentTimeMillis());
	public Date expectedEndTime = new Date(System.currentTimeMillis());
	public int requestId = 123456789;
	public String version = Props.getStr("routingVersion");
	public boolean provideRouteHandle = Props.getBool("provideRouteHandle");
	public String distanceUnit = Props.getStr("distanceUnit");
	public String routePreference = Props.getStr("routePreference");
	public float scale = (float) Props.getDou("scale");
	public boolean provideStartingPortion = Props.getBool("provideStartingPortion");
	public int maxPoints = Props.getInt("maxPoints");
	public boolean stopAtPoints = Props.getBool("stopAtPoints");
	public String srsName = Props.getStr("srsName");
	public boolean provideGeometry = Props.getBool("provideGeometry");
	public boolean provideBoundingBox = Props.getBool("provideBoundingBox");
	public String surfaceQuality;
	public String maxSlope;
	public String tactilePavingRequired;
	
	//SECONDARYROUTEPREFERENCES
	/*
	 <xls:SecondaryRoutePreferences>
   <surfaceQuality>Cobblestones</surfaceQuality>
   <maxSlope>3</maxSlope>
   </xls:SecondaryRoutePreferences>
   */


	public OpenLSRequestPreferences clone()
	{
		OpenLSRequestPreferences clone = new OpenLSRequestPreferences();
		clone.useRealTimeTraffic = true;
		clone.expectedStartTime = this.expectedStartTime;
		clone.expectedEndTime = this.expectedEndTime;
		clone.requestId = this.requestId;
		clone.version = this.version;
		clone.provideRouteHandle = this.provideRouteHandle;
		clone.distanceUnit = this.distanceUnit;
		clone.routePreference = this.routePreference;
		clone.scale = this.scale;
		clone.provideStartingPortion = this.provideStartingPortion;
		clone.maxPoints = this.maxPoints;
		clone.stopAtPoints = this.stopAtPoints;
		clone.srsName = this.srsName;
		clone.provideGeometry = this.provideGeometry;
		clone.provideBoundingBox = this.provideBoundingBox;
		clone.surfaceQuality = this.surfaceQuality;
		clone.maxSlope = this.maxSlope;
		clone.tactilePavingRequired = this.tactilePavingRequired;
		return clone;
	}
}
