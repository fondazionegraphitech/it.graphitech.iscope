package Kml;

import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import iScope.WWJApplet;

public class KmlPilot
{
	private String name;
	private String kmlURL;
	private KmlThread kmlThread;

	
	public KmlPilot(String name, String kmlURL)
	{
		this.name = name;
		this.kmlURL = kmlURL;
	}
	
	
	
	
	public KmlThread getKmlThread()
	{
		return kmlThread;
	}	

	public String getName()
	{
		return name;
	}

	public String getKmlURL()
	{
		return kmlURL;
	}

	public KMLController getController()
	{
		if (kmlThread == null)
			return null;
		return kmlThread.getKmlController();
	}

	public void load()
	{
		if (kmlThread == null)
		{
			// INIT THE KML AND START THE THREAD
			if (kmlURL != null)
			{
				kmlThread = new KmlThread(kmlURL, WWJApplet.getWWD());
				System.out.println("Pilot " + name + "loaded from url: " + kmlURL);
				if (kmlThread != null)
				{
					kmlThread.start();
				} else
				{
					System.err.println("Kml Thread was not initialized.");
				}
			} else
			{
				System.err.println("Pilot " + name + " dosn't exist");
			}
		} else
		{
			// IF THIS IS CALLED AGAIN PROBABLY IS BECAUSE IT HAS BEEN DISABLED BEFORE
			LayerList layers = WWJApplet.getWWD().getModel().getLayers();
			if (!layers.contains(kmlThread.getKmlLayer()))
			{
				layers.add(kmlThread.getKmlLayer());
			}
		}
	}

	public boolean isLoaded()
	{
		if (kmlThread != null)
			return true;
		return false;
	}

	public boolean isVisible()
	{
		return kmlThread.getKmlLayer().isEnabled();
	}

	public void setInvisible()
	{
		if (kmlThread != null)
		{
			kmlThread.getKmlLayer().setEnabled(false);
		}
	}

	public void setVisible()
	{
		if (kmlThread != null)
		{
			kmlThread.getKmlLayer().setEnabled(true);
		}
	}
}
