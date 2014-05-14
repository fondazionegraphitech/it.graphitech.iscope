package Tiled3dLayer;

import httpConnection.httpGet;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Pilot.Pilot;

public class Fixed3dTileListRetriever implements RetrieverInterface
{
	private LinkedList<Tile3D> tiles = new LinkedList<>();
	private Pilot pilot;

	@Override
	public Collection<Tile3D> getTiles()
	{
		return tiles;
	}

	private static ExecutorService executorService = Executors.newFixedThreadPool(1);

	public static RetrieverInterface fromPilot(Tiled3DLayerRenderer tiled3dLayerRenderer, Pilot pilot)
	{
		final Fixed3dTileListRetriever fixed3dTileListRetriever = new Fixed3dTileListRetriever();
		fixed3dTileListRetriever.setPilot(pilot);
		Runnable loadTile = new Runnable()
		{
			@Override
			public void run()
			{				
				fixed3dTileListRetriever.init();
			}
		};
		executorService.execute(loadTile);
		return fixed3dTileListRetriever;
	}

	private void init()
	{
		String request = "http://77.72.192.34:8080/Tile3DCacheServer/tiles?requestType=getTileList&dataSetName=" + pilot.getName();
		String tileRequest = "http://77.72.192.34:8080/Tile3DCacheServer/tiles?requestType=getTile&dataSetName=" + pilot.getName() + "&tileID=";
		
		try
		{
			System.out.println("Request for Fixed 3D Tile List: ");
			System.out.println("Tile List Request: " + request);
			String response = httpGet.sendGetRequest(request);
			String[] p1 = response.split(";");
			for (String string : p1)
			{
				String[] p2 = string.split(",");
				Tile3D tile3d = new Tile3D(tileRequest + p2[0], pilot);
				tiles.add(tile3d);
			}
		} catch (Exception e)
		{
			
			System.err.println("Error during intializing fixed tile retriever, well use fixed tiles request");
			for (int i = 0; i < 100; i++)
			{
				Tile3D tile3d = new Tile3D(tileRequest + i, pilot);
				tiles.add(tile3d);
			}
		}
	}


	private void setPilot(Pilot pilot)
	{
		this.pilot = pilot;
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}
}
