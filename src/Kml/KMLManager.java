package Kml;

import iScope.Props;

import java.util.ArrayList;
import java.util.List;

public class KMLManager
{
	List<KmlPilot> kmlPilots = new ArrayList<>();
	KmlPilot currentPilot = null;

	public KMLManager()
	{
		List<String> pilotsNames = Props.getStrList("pilots");
		for (String pilotName : pilotsNames)
		{
			kmlPilots.add(new KmlPilot(pilotName, Props.getStr("kml_" + pilotName)));
		}
	}

	/**
	 * Load into worlwind a pilot of the configuration file
	 * @param pilotName
	 */
	public void loadPilot(String pilotName)
	{
		KmlPilot kmlPilot = getPilotByName(pilotName);
		if (kmlPilot == null)
		{
			System.out.println("Pilot " + pilotName + " does not exists");
		} else
		{
			unloatAllPilots();
			kmlPilot.load();
			currentPilot = kmlPilot;
		}
	}
	
	/**
	 * Load an external pilot from a kml FILE
	 * @param pilotFile
	 */
	public void loadExternalPilot(String pilotFile)
	{
		KmlPilot kmlPilot = new KmlPilot(pilotFile, pilotFile);
		kmlPilots.add(kmlPilot);
		unloatAllPilots();
		kmlPilot.load();

	}

	/**
	 * Set loaded pilots to invisible
	 */
	public void unloatAllPilots()
	{
		for (KmlPilot pilot : kmlPilots)
		{
			pilot.setInvisible();
		}		
	}

	/**
	 * Return loaded Pilot by name
	 * @param name
	 * @return
	 */
	public KmlPilot getPilotByName(String name)
	{
		for (KmlPilot kmlPilot : kmlPilots)
		{
			if (kmlPilot.getName().equalsIgnoreCase(name))
			{
				return kmlPilot;
			}
		}
		return null;
	}

	/**
	 * Returns the list of pilot loaded from the configuration file
	 * @return
	 */
	public List<KmlPilot> getKmlPilots()
	{
		return kmlPilots;
	}

	/**
	 * Enable or disable visualization of the current KML Pilot 3D Buildings
	 */
	public void toggleCurrentKMLPilot()
	{
		System.out.println("Calling: toggleCurrentKMLPilot, current pilot: " + currentPilot.getName());
		if ( currentPilot.isVisible())
		{			
			currentPilot.setInvisible();
		} else
		{			
			currentPilot.setVisible();
		}
		
	}

}