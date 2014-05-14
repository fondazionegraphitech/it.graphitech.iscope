package noise;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.MarkerAttributes;

import java.awt.Color;

public class NoiseMarker extends BasicMarker
{
	private NoisePoint noisePoint;
	private int minMarkersize = 2;
	private int maxMarkersize = 50;
	

	public void changeMarkerDimension(int deltaDim) {						
		if (this.getAttributes().getMinMarkerSize() > 2*Math.abs(deltaDim)){ 
			this.getAttributes().setMinMarkerSize(this.getAttributes().getMinMarkerSize() + deltaDim);
			this.getAttributes().setMaxMarkerSize(this.getAttributes().getMaxMarkerSize() + deltaDim);
		}
	}		

	public NoiseMarker(Position position, int dB, String user, String time)
	{
		super(position, new BasicMarkerAttributes());
		
		double myExageratedDimension = ((Math.pow(dB, 4)/52200625))*maxMarkersize;  //normalizzazione ^2 su un max di 85 db * maxMarkeerzSize
//		System.out.println( dB + "xa" + myExageratedDimension);
		MarkerAttributes myAttributes = new BasicMarkerAttributes();	
		myAttributes.setMaterial(new Material(getColorFromdB(dB)));
		myAttributes.setHeadingMaterial(new Material(getColorFromdB(dB)));
		myAttributes.setShapeType(BasicMarkerShape.CYLINDER);
		myAttributes.setOpacity(0.8);
		myAttributes.setMarkerPixels(myExageratedDimension);
//		myAttributes.setMinMarkerSize(minMarkersize);
		myAttributes.setMaxMarkerSize(maxMarkersize);				
		this.setAttributes(myAttributes);
		this.noisePoint = new NoisePoint(position, dB, time, user);
	}

	public static NoiseMarker fromNoisePoint(NoisePoint noisePoint)
	{
		return new NoiseMarker(noisePoint.getPosition(), noisePoint.getDeciBel(), noisePoint.getUser(), noisePoint.getAquisitionTime());
	}
	
//	private static Color getColorFromdB(double value)
//	{
//		Color color = null;
//		if (value <= 35)
//			color = Color.getHSBColor(60, 1, 1); 
//		else if (value <= 40)
//			color = Color.getHSBColor(85, 1, 1); 
//		else if (value <= 45)
//			color = Color.getHSBColor(110, 1, 0.9f); 
//		else if (value <= 50)
//			color = Color.getHSBColor(135, 1, 0.90f); 
//		else if (value <= 55)
//			color = Color.getHSBColor(160, 1, 0.80f); 
//		else if (value <= 60)
//			color = Color.getHSBColor(185, 1, 0.80f); 
//		else if (value <= 65)
//			color = Color.getHSBColor(210, 1, 0.70f); 
//		else if (value <= 70)
//			color = Color.getHSBColor(235, 1, 0.7f); 
//		else if (value <= 75)
//			color = Color.getHSBColor(260, 1, 0.60f); 
//		else if (value <= 80)
//			color = Color.getHSBColor(285, 1, 0.60f); 
//		else if (value <= 85)
//			color = Color.getHSBColor(310, 1, 0.4f); 
//		else
//			color = Color.black;
//		/*
//		 * double minValue = 35; double maxValue = 85; double hue = 0.5 -
//		 * (Math.min(Math.max(value, minValue), maxValue)-minValue) /
//		 * (maxValue-minValue); //NORMALIZE FROM 0 to 1
//		 * 
//		 * color = Color.getHSBColor((float)hue, 1, 1);
//		 */
//		return color;
//	}
	
	private Color linearColorValue(double value){
		Color color = null;
		double minValue = 35; double maxValue = 140; double hue = 0.5 - (Math.min(Math.max(value, minValue), maxValue)-minValue) /	(maxValue-minValue); //NORMALIZE FROM 0 to 1
		
		color = Color.getHSBColor((float)hue, 1, 1);
		
		return color;
	}
	
	private static Color getColorFromdB(final double value)
	{
		Color color = null;
		if (value <= 35)
			color = Color.white; // light green
		else if (value <= 40)
			color = Color.yellow;
		else if (value <= 45)
			color = Color.lightGray; // dark green
		else if (value <= 50)
			color = Color.gray; // yellow
		else if (value <= 55)
			color = Color.darkGray; // ochre
		else if (value <= 60)
			color = Color.orange; // orange
		else if (value <= 65)
			color = Color.pink; // cinnabar
		else if (value <= 70)
			color = Color.red; // carmine
		else if (value <= 75)
			color = Color.BLUE; // lilac red
		else if (value <= 80)
			color = Color.MAGENTA; // blue
		else if (value <= 85)
			color = Color.CYAN; // dark blue
		else
			color = Color.black;
		/*
		 * double minValue = 35; double maxValue = 85; double hue = 0.5 -
		 * (Math.min(Math.max(value, minValue), maxValue)-minValue) /
		 * (maxValue-minValue); //NORMALIZE FROM 0 to 1
		 * 
		 * color = Color.getHSBColor((float)hue, 1, 1);
		 */
		return color;
	}

//	private static Color getColorFromdB(final double value)
//	{
//		Color color = null;
//		if (value <= 35)
//			color = Color.getHSBColor(120, 100, 75); // light green
//		else if (value <= 40)
//			color = Color.green;
//		else if (value <= 45)
//			color = Color.getHSBColor(158, 96, 10); // dark green
//		else if (value <= 50)
//			color = Color.yellow; // yellow
//		else if (value <= 55)
//			color = Color.getHSBColor(30, 71, 47); // ochre
//		else if (value <= 60)
//			color = Color.orange; // orange
//		else if (value <= 65)
//			color = Color.getHSBColor(5, 76, 55); // cinnabar
//		else if (value <= 70)
//			color = Color.getHSBColor(350, 100, 29); // carmine
//		else if (value <= 75)
//			color = Color.getHSBColor(270, 51, 62); // lilac red
//		else if (value <= 80)
//			color = Color.blue; // blue
//		else if (value <= 85)
//			color = Color.getHSBColor(240, 100, 60); // dark blue
//		else
//			color = Color.black;
//		/*
//		 * double minValue = 35; double maxValue = 85; double hue = 0.5 -
//		 * (Math.min(Math.max(value, minValue), maxValue)-minValue) /
//		 * (maxValue-minValue); //NORMALIZE FROM 0 to 1
//		 * 
//		 * color = Color.getHSBColor((float)hue, 1, 1);
//		 */
//		return color;
//	}

	public NoisePoint getNoisePoint()
	{
		return noisePoint;
	}
}
