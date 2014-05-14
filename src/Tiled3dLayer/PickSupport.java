package Tiled3dLayer;

import geometryFormat.Feature;
import iScope.WWJApplet;

import java.util.HashMap;

public class PickSupport
{
	public static HashMap<Integer, Object> colorToObject = new HashMap<>(100000);
	public static int uniqueSurfaceColor = Integer.MAX_VALUE;
	
	public static boolean pickEnabled = false;

	public static int[] getUniqueColor(Object obj)
	{
		int[] ia = getARGBFromInt( uniqueSurfaceColor + 1 );
		ia[0] = 0;
		colorToObject.put(getIntFromARGB(ia), obj);
		uniqueSurfaceColor--;
		return ia;
	}
	
	public static int[] getARGBFromInt( int color )
	{
		int[] b = new int[4];
		for (int i = 0; i < 4; i++)
		{
			int offset = (b.length - 1 - i) * 8;
			b[i] = (color >>> offset) & 0xFF;
		}
		return b;
	}
	public static int getIntFromARGB( int[] b )
	{
		return b[1] * 0xffffff + b[2] * 0xffff+ b[3] * 0xff + b[0];
	}
	
	public static Object doPickWithWWDColor(int color)
	{
		int b[] = PickSupport.getARGBFromInt(color);
		int pickColorCorrected = PickSupport.getIntFromARGB(b);
		Object o = colorToObject.get( pickColorCorrected );
		
		if (pickEnabled && o != null)
		{
			pickEnabled = false;
			Feature f = (Feature) o;
			WWJApplet.getJS().call("newsolarAssesmentPick", new Object[] {f.id, f.parent.id});
		}
		
		return o;
	}

	public static void enableBuildingPickingOnApplet()
	{
		pickEnabled = true;
	}
	
}
