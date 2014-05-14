package wms;

import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.util.WWUtil;
import iScope.WWJApplet;

import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Set;

public class WmsManager {
	
	WorldWindowGLCanvas wwd;	
	
	protected static class LayerInfo
    {
        protected WMSCapabilities caps;
        protected AVListImpl params = new AVListImpl();

        protected String getTitle()
        {
            return params.getStringValue(AVKey.DISPLAY_NAME);
        }

        protected String getName()
        {
            return params.getStringValue(AVKey.LAYER_NAMES);
        }

        protected String getAbstract()
        {
            return params.getStringValue(AVKey.LAYER_ABSTRACT);
        }
    }
	
	
	public WmsManager(WorldWindowGLCanvas wwd){
		this.wwd = wwd;			
	}
	
	
	public void addWMSSecure(final String brokerURL){

		AccessController.doPrivileged(
				new PrivilegedAction<String>() {
					public String run() {
						return addWMSlayers(brokerURL);
					}
				}
				);
	}
	
	public LayerList getLayerListFromWMS(String brokerURL){
		
		System.out.println("GetLayerListFromWMS, Parsing capabilities of wms having URL "+ brokerURL);
		WMSCapabilities caps;
		LayerList layerList = new LayerList();
		try {
			caps = WMSCapabilities.retrieve(new URI(brokerURL));			
			caps.parse();
		} catch (Exception e) {
			System.err.println("Problem parsing capabilities address " + brokerURL + " or this wms does not exist");
			return null;
		}

		// Gather up all the named layers and make a world wind layer for each.
		final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();	
		
		if (namedLayerCaps == null){
			System.out.println("The wms having URL " + brokerURL + " is empty");
			return null;
		}
		try {
			for (WMSLayerCapabilities lc : namedLayerCaps) {
				Set<WMSLayerStyle> styles = lc.getStyles();
				    //se un layer ha piu stili diponibili li aggiungo tutti alla lista
					for (WMSLayerStyle style : styles) {
						LayerInfo layerInfo = createLayerInfo(
								caps, lc, style);						

						Layer layer = (Layer) createComponent(layerInfo.caps,
								layerInfo.params);
						layer.setName(lc.getName());						
						layerList.add(layer);								
					}
				}
			}
	    catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("GetLayerListFromWMS: " + layerList.size() + " layers found");
		return layerList;
				
	}	

	
	public String addWMSlayers(String brokerURL) {	
		System.out.println("AddWMSlayers, Parsing capabilities of wms having URL "+ brokerURL);
	
		// System.getSecurityManager().checkPermission(new
		// SocketPermission(brokerURL, "connect"));
		WMSCapabilities caps;

		try {
			caps = WMSCapabilities.retrieve(new URI(brokerURL));
			caps.parse();
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

		// Gather up all the named layers and make a world wind layer for each.
		final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();	
		
		if (namedLayerCaps == null){
			System.out.println("The wms having URL " + brokerURL + " is empty");
			return "0";
		}

		try {
			for (WMSLayerCapabilities lc : namedLayerCaps) {
				Set<WMSLayerStyle> styles = lc.getStyles();
				if (styles == null || styles.size() == 0) {
					LayerInfo layerInfo = createLayerInfo(caps,
							lc, null);
					
					Layer layer = (Layer) createComponent(caps,
							layerInfo.params);
					
					boolean trovato = false;

					LayerList layers = wwd.getModel().getLayers();
					for (Layer l : layers) {  //controllo che il layer non si agià presente in WW
						if (l.getName().equals(layer.getName())) {
							trovato = true;							
						}
					}

					if (!trovato) {
						System.out.println("New layer found in workSpace " + lc.getName() + " and added to WW layerList: " + layer.getName());
						layer.setEnabled(true);
						WWJApplet.getJS().insertBeforeLayerName(this.wwd, layer, "Compass");
					}
				} else {
					for (WMSLayerStyle style : styles) {
						LayerInfo layerInfo = createLayerInfo(
								caps, lc, style);						

						Layer layer = (Layer) createComponent(layerInfo.caps,
								layerInfo.params);

						layer.setName(layer.getName().substring(0,
								layer.getName().indexOf(" : ")));
						boolean trovato = false;

						LayerList layers = wwd.getModel().getLayers();
						for (Layer l : layers) {
							if (l.getName().equals(layer.getName())) {
								trovato = true;								
							}
						}
						if (!trovato) {
							System.out.println("New layer found in workSpace " + lc.getName() + "and added to WW layerList: " + layer.getName());
						
							layer.setEnabled(false);
							WWJApplet.getJS().insertBeforeLayerName(this.wwd, layer, "Compass");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
	
	public  Object createComponent(WMSCapabilities caps, AVList params)
	{
		AVList configParams = params.copy(); // Copy to insulate changes from the caller.

		// Some wms servers are slow, so increase the timeouts and limits used by world wind's retrievers.
		configParams.setValue(AVKey.URL_CONNECT_TIMEOUT, 30000);
		configParams.setValue(AVKey.URL_READ_TIMEOUT, 30000);
		configParams.setValue(AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT, 60000);

		try
		{
			String factoryKey = getFactoryKeyForCapabilities(caps);
			Factory factory = (Factory) WorldWind.createConfigurationComponent(factoryKey);
			return factory.createFromConfigSource(caps, configParams);
		}
		catch (Exception e)
		{
			// Ignore the exception, and just return null.
		}

		return null;
	}
	
	protected static String getFactoryKeyForCapabilities(WMSCapabilities caps)
	{
		boolean hasApplicationBilFormat = false;

		Set<String> formats = caps.getImageFormats();
		for (String s : formats)
		{
			if (s.contains("application/bil"))
			{
				hasApplicationBilFormat = true;
				break;
			}
		}

		return hasApplicationBilFormat ? AVKey.ELEVATION_MODEL_FACTORY : AVKey.LAYER_FACTORY;
	}
	
	protected LayerInfo createLayerInfo(WMSCapabilities caps, WMSLayerCapabilities layerCaps, WMSLayerStyle style)
	{
		// Create the layer info specified by the layer's capabilities entry and the selected style.

		LayerInfo linfo = new LayerInfo();
		linfo.caps = caps;
		linfo.params = new AVListImpl();
		linfo.params.setValue(AVKey.LAYER_NAMES, layerCaps.getName());
		if (style != null)
			linfo.params.setValue(AVKey.STYLE_NAMES, style.getName());
		String abs = layerCaps.getLayerAbstract();
		if (!WWUtil.isEmpty(abs))
			linfo.params.setValue(AVKey.LAYER_ABSTRACT, abs);

		linfo.params.setValue(AVKey.DISPLAY_NAME, makeTitle(caps,linfo));

		return linfo;
	}
	
	
	//modifica il nome del layer togliendo la definizione di workspace
	protected static String makeTitle(WMSCapabilities caps, LayerInfo layerInfo)
	{
		String layerNames = layerInfo.params.getStringValue(AVKey.LAYER_NAMES);
		String styleNames = layerInfo.params.getStringValue(AVKey.STYLE_NAMES);
		String[] lNames = layerNames.split(",");
		String[] sNames = styleNames != null ? styleNames.split(",") : null;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lNames.length; i++)
		{
			if (sb.length() > 0)
				sb.append(", ");

			String layerName = lNames[i];
			WMSLayerCapabilities lc = caps.getLayerByName(layerName);
						
			String layerTitle = lc.getTitle();
			sb.append(layerTitle != null ? layerTitle : layerName);

			if (sNames == null || sNames.length <= i)
				continue;

			String styleName = sNames[i];
			WMSLayerStyle style = lc.getStyleByName(styleName);
			if (style == null)
				continue;

			sb.append(" : ");
			String styleTitle = style.getTitle();
			sb.append(styleTitle != null ? styleTitle : styleName);
		}

		return sb.toString();
	}
}
