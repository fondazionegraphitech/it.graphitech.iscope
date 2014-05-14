package nFinteractionManager;

import iScope.Props;
import iScope.WWJApplet;

import java.util.ArrayList;

import selector.MySectorSelector;

public class ImportExportManager {
	
static MySectorSelector exportSelector;
private static ArrayList<NFproduct> nfproductList = new ArrayList<NFproduct>();


	
	public ImportExportManager() {		
		getNFcapabilities(Props.getStr("nFproductsInfoAddress"));		
	}
	
	public NFproduct getNFProductByName( String name )
	{
		for (NFproduct nfP : nfproductList)
		{
			if ( name.equalsIgnoreCase(nfP.getName()))
				return nfP;
		}
		return null;
	}
	

//	public static void newExportSelection(boolean exportByTileId, boolean exportByBB){
//		
//		if(exportByBB){
//			exportSelector = new MySectorSelector(WWJApplet.getWWD());
//			exportSelector.enable();			
//		}
//		
//		else if (exportByTileId){
//			//TODO visualizzarela griglia delle tile e abilitare il picking su di essa
//			//selectedTiles = 
//		}
//	}
	

	
	public static void getNFcapabilities(String getCapAdderss) {			
		String capabilities = httpConnection.httpGet.sendGetRequest(getCapAdderss);
		nfproductList = NFallLayerRequestParser.readCapabilities(capabilities);			
//      //genero il renderableTile layer per il currentNfProduct
//		for (NFproduct nFproduct : nfproductList) {
//			String productTileURL = Props.getStr("nFproductsInfoService") + "request=subdivision&productid=" + nFproduct.getId() + "&srs=4326";
//			String productTilesResp = httpConnection.httpGet.sendGetRequest(productTileURL);	
//			NFLayerTilesRequestParser layerTilesRequestParser = new NFLayerTilesRequestParser(nFproduct, productTilesResp);
//			layerTilesRequestParser.readTiles();
//			for (Tile2D tile : nFproduct.tiles) {
//				nFproduct.getTileLayer().addRenderable(tile);								
//			}			
//		}
	}
	
	public static void productSelected(String productName){			
//		System.out.println("2.543563: Coordinate trans");
//		SpatialReference srcCRS = new SpatialReference();
//		SpatialReference dstCRS = new SpatialReference();
//		CoordinateTransformation coordinateTransformation;
//		System.out.println("2: Coordinate trans done");
//		NFproduct nfProduct = getNfProductbyName(productName);	
//		System.out.println("trovao product avente nome" +  nfProduct.getName().toString());
//		srcCRS.ImportFromEPSG(nfProduct.getSrs()); 
//		dstCRS.ImportFromEPSG(4326);
//		coordinateTransformation = new CoordinateTransformation(srcCRS, dstCRS);
//		Double xCenterSectorPoint = (nfProduct.getXll() + nfProduct.getXur()) / 2;
//		Double yCenterSectorPoint = (nfProduct.getYll() + nfProduct.getYur()) / 2;
//		double latLonCoords[] = coordinateTransformation.TransformPoint(
//				xCenterSectorPoint, yCenterSectorPoint);
//		WWJApplet.jS.gotoLatLon(PilotManager.importExportManager.get);
		// TODO visualizzare la tiel grid su cui effettuare la selezione qunado
		// la funzionalità sarà disponibile
	}
	
	private static NFproduct getNfProductbyName(String name){
		for (NFproduct nfProduct : nfproductList) {
			if (nfProduct.getName().equalsIgnoreCase(name))
				return nfProduct;
		}		
	    System.err.println("Product " + name + " not found");
		return null;
		
	}
		
	

		
}


