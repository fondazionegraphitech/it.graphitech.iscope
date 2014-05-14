package nFinteractionManager;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import iScope.WWJApplet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nFinteractionManager.NFproduct.Layer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class NFLayerTilesRequestParser extends DefaultHandler{
	
	 private String temp;	 // qui vanno le stringe non contenute tra <> 
	 private NFproduct tmpNfproduct;	
	 private ArrayList<LatLon> latLons = new ArrayList<>(); 
	 private Tile2D tmpTile;	
	 String capabilitiesRequest;
	 ShapeAttributes normalAttributes, highlightAttributes;	  
	 
   public NFLayerTilesRequestParser(NFproduct nfproduct, String capabilitiesRequest){
	   this.tmpNfproduct = nfproduct;
	   this.capabilitiesRequest = capabilitiesRequest;
	   // Create and set an attribute bundle.
	     normalAttributes = new BasicShapeAttributes();
	     normalAttributes.setInteriorMaterial(Material.GREEN);	     
	     normalAttributes.setInteriorOpacity(0.3);
	     
	     normalAttributes.setOutlineMaterial(Material.WHITE);
	     normalAttributes.setOutlineOpacity(0.5);
	     normalAttributes.setOutlineWidth(2);
	     normalAttributes.setDrawOutline(true);
	     normalAttributes.setDrawInterior(true);
	     normalAttributes.setEnableLighting(true);	     

	     highlightAttributes = new BasicShapeAttributes(normalAttributes);
	     highlightAttributes.setOutlineMaterial(Material.BLACK);
	     highlightAttributes.setOutlineWidth(4);
	     highlightAttributes.setInteriorOpacity(0.8);
   }


   public void readTiles() { 
	    
    	//Create a "parser factory" for creating SAX parsers
        SAXParserFactory spfac = SAXParserFactory.newInstance();
        //Now use the parser factory to create a SAXParser object
        SAXParser sp;
        //Create an instance of this class; it defines all the handler methods
        NFLayerTilesRequestParser handler = new NFLayerTilesRequestParser(tmpNfproduct, capabilitiesRequest);

        ByteArrayInputStream byteArray;
		try {
			sp = spfac.newSAXParser();
			byteArray = new ByteArrayInputStream(capabilitiesRequest.getBytes("utf-8"));
			InputSource inputSource = new InputSource(byteArray);
	        //Finally, tell the parser to parse the input and notify the handler
	        sp.parse(inputSource, handler);  		        
	        handler.readList();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			System.err.println("Problems reading the capabilities response");
			e.printStackTrace();
		}	
   }   

   
   /*
    * When the parser encounters plain text (not XML elements),
    * it calls(this method, which accumulates them in a string buffer
    */
   public void characters(char[] buffer, int start, int length) {
	   temp = new String(buffer, start, length);
   }
  

   /*
    * Every time the parser encounters the beginning of a new element,
    * it calls this method, which resets the string buffer
    */ 
   public void startElement(String uri, String localName,
                 String qName, Attributes attributes) throws SAXException {
          temp = "";
                    
          if (qName.equalsIgnoreCase("tile")) { 
        	 int tilenumb =  Integer.parseInt(attributes.getValue("designation"));
        	 int status = Integer.parseInt(attributes.getValue("status"));
        	 if (status == -1) normalAttributes.setInteriorOpacity(0);  //no import
        	 else normalAttributes.setInteriorOpacity(0.3);
        	 tmpTile = new Tile2D(tilenumb, status);	  
          }            
          else if (qName.equalsIgnoreCase("vertex")) {
       	   latLons.add(new LatLon(Angle.fromDegrees(Double.parseDouble(attributes.getValue("y"))), Angle.fromDegrees(Double.parseDouble(attributes.getValue("x")))));       
          }               	 
   }

   /*
    * When the parser encounters the end of an element, it calls this method
    */
   public void endElement(String uri, String localName, String qName)
                 throws SAXException {

          if (qName.equalsIgnoreCase("exterior")) {
        	   tmpTile.setOuterBoundary(latLons);               
               ShapeAttributes attributes = normalAttributes.copy();
               ShapeAttributes higlightAtt = highlightAttributes.copy();
               tmpTile.setAttributes(attributes);
               tmpTile.setHighlightAttributes(higlightAtt);               
               latLons = new ArrayList<>();           
               tmpNfproduct.tiles.add(tmpTile);
          }
                 
   }

   private void readList() {
	   if (false){
          System.out.println("Number of product tiles: '" + tmpNfproduct.tiles.size()  + "'.");
          Iterator<Tile2D> it = tmpNfproduct.tiles.iterator();
          while (it.hasNext()) {
                 System.out.println(it.next().toString());
          }
	   }
   }
        
 
   
 
}