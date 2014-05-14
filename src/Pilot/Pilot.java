package Pilot;

import geometry.Vector3;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import iScope.Props;
import iScope.WWJApplet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nFinteractionManager.NFLayerTilesRequestParser;
import nFinteractionManager.NFproduct;
import nFinteractionManager.Tile2D;
import wms.WmsManager;
import DebugConsole.DebugConsoleManager;
import Tiled3dLayer.Tiled3DLayerRenderer;

public class Pilot
{	
	private String dtmConfigFileName;
	private String dsmConfigFileName;
	private String ortoPhotoLayerCOnfigFileName;
	private String name;
	private LayerList pilotLayerList;
	private NFproduct nFproduct;
	private LayerList noiseLayers = new LayerList();
	private LayerList solarLayers = new LayerList();
	private LayerList orthoLayers = new LayerList();
	private LayerList DTMLayers = new LayerList();
	private WmsManager wmsManager;
	public boolean wmsLoaded = false;
	protected long timeout = (long) Props.getDou("wmsTimeout");
	private static ExecutorService executorService = Executors.newFixedThreadPool(1);	
	public Tiled3DLayerRenderer tredilayer;
	public Vector3 dataSet3dOffSet;
	
	public Pilot(String pilotName)
	{
		this.name = pilotName;		
		this.dtmConfigFileName = Props.getStr("dtm_" + pilotName);
		this.dsmConfigFileName = Props.getStr("dsm_" + pilotName);
		this.ortoPhotoLayerCOnfigFileName = Props.getStr("ortho_" + pilotName);		
		
		dataSet3dOffSet = new Vector3(0, 0, 0);  //imposto l'offset a 0, se non definito rimmarrà così
		String latOff =Props.getStr("latOff_" + pilotName);
		if (latOff!=null) dataSet3dOffSet.y = Double.parseDouble(latOff);		
		String lonOff =Props.getStr("lonOff_" + pilotName);
		if (lonOff!=null) dataSet3dOffSet.x = Double.parseDouble(lonOff);	
		String zOff =Props.getStr("verticalOff_" + pilotName);
		if (zOff!=null) dataSet3dOffSet.z = Double.parseDouble(zOff);	
		
		System.out.println("OFFSET" + dataSet3dOffSet);
		
		load3DLayer();
		
		loadWmsNoThread(); 
//		executorService.execute(LoadWMS);
		//istanzio nFproduct e le sue tile
		nFproduct = PilotManager.importExportManager.getNFProductByName(pilotName);
		String productTileURL = Props.getStr("nFproductsInfoService") + "request=subdivision&productid=" + nFproduct.getId() + "&srs=4326";
		String productTilesResp = httpConnection.httpGet.sendGetRequest(productTileURL);
		NFLayerTilesRequestParser layerTilesRequestParser = new NFLayerTilesRequestParser(nFproduct, productTilesResp);
		layerTilesRequestParser.readTiles();
		for (Tile2D tile : nFproduct.tiles)
		{
			nFproduct.getTileLayer().addRenderable(tile);
		}
		
	}

	Runnable LoadWMS = new Runnable()
	{
		@Override
		public void run(){
			DebugConsoleManager.textPaneWMSready.setText("Not ready");				
			wmsManager = new wms.WmsManager(WWJApplet.getWWD());
			pilotLayerList = wmsManager.getLayerListFromWMS(Props.getStr("pilotGeoServer") + name + "/wms");
			if (pilotLayerList != null)
			{
				String noise = "noise";
				String solar = "solar";
				String orthophoto = "orthophoto";
				String DTM = "DTM";
				for (Layer layer : pilotLayerList)
				{
					if (layer.getName().toLowerCase().contains(noise.toLowerCase()))
						noiseLayers.add(layer);
					if (layer.getName().toLowerCase().contains(solar.toLowerCase()))
						solarLayers.add(layer);
					if (layer.getName().toLowerCase().contains(orthophoto.toLowerCase()))
						orthoLayers.add(layer);
					if (layer.getName().toLowerCase().contains(DTM.toLowerCase())){
						DTMLayers.add(layer);											
					}

				}
				DebugConsoleManager.textPaneWMSready.setText("Ready");		
			}			
		}
	};
	
	
	public void loadWmsNoThread (){			
		DebugConsoleManager.textPaneWMSready.setText("Not ready");
		wmsManager = new wms.WmsManager(WWJApplet.getWWD());		
		System.out.println("Loadinng WMS layers from " + Props.getStr("pilotGeoServer") + name + "/wms");
		pilotLayerList = wmsManager.getLayerListFromWMS(Props.getStr("pilotGeoServer") + name + "/wms");
		
		if (pilotLayerList != null)
		{
			String noise = "noise";
			String solar = "solar";
			String orthophoto = "orthophoto";
			String DTM = "DTM";
			for (Layer layer : pilotLayerList)
			{
				if (layer.getName().toLowerCase().contains(noise.toLowerCase()))
					noiseLayers.add(layer);
				if (layer.getName().toLowerCase().contains(solar.toLowerCase()))
					solarLayers.add(layer);
				if (layer.getName().toLowerCase().contains(orthophoto.toLowerCase()))
					orthoLayers.add(layer);
				if (layer.getName().toLowerCase().contains(DTM.toLowerCase())){
					DTMLayers.add(layer);											
				}

			}			
		}
		DebugConsoleManager.textPaneWMSready.setText("Ready");
	}

	public LayerList getNoiseLayers()
	{
		return noiseLayers;
	}

	public void setNoiseLayers(LayerList noiseLayers)
	{
		this.noiseLayers = noiseLayers;
	}

	public LayerList getSolarLayers()
	{
		return solarLayers;
	}

	public void setSolarLayers(LayerList solarLayers)
	{
		this.solarLayers = solarLayers;
	}

	public LayerList getPilotLayerList()
	{
		return pilotLayerList;
	}

	public void setPilotLayerList(LayerList pilotLayerList)
	{
		this.pilotLayerList = pilotLayerList;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	public String getDtmConfigFileName()
	{
		return dtmConfigFileName;
	}

	public void setDtmConfigFileName(String dtmConfigFileName)
	{
		this.dtmConfigFileName = dtmConfigFileName;
	}

	public String getDsmConfigFileName()
	{
		return dsmConfigFileName;
	}

	public void setDsmConfigFileName(String dsmConfigFileName)
	{
		this.dsmConfigFileName = dsmConfigFileName;
	}

	public String getOrtoPhotoLayerCOnfigFileName()
	{
		return ortoPhotoLayerCOnfigFileName;
	}

	public void setOrtoPhotoLayerCOnfigFileName(String ortoPhotoLayerCOnfigFileName)
	{
		this.ortoPhotoLayerCOnfigFileName = ortoPhotoLayerCOnfigFileName;
	}

	public NFproduct getnFproduct()
	{
		return nFproduct;
	}

	public void unload3DLayer()
	{
		if (tredilayer != null)
		tredilayer.unload();
		tredilayer = null;
		
	}
	public void load3DLayer()
	{
		if (tredilayer == null)
		{
			System.out.println("Unloading 3D Layer for pilot: " + getName());
			tredilayer = new Tiled3DLayerRenderer(this);
		} else
		{
			System.err.println("No 3D Layer Loaded for pilot: " + getName());
		}
		
	}
}
