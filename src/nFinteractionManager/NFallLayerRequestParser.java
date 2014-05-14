package nFinteractionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 
public class NFallLayerRequestParser extends DefaultHandler{
	
	 private String temp;	 // qui vanno le stringe non contenute tra <> 
	 private NFproduct nfproduct;
	 private Layer layer;
	 private static ArrayList<NFproduct> nfproductList = new ArrayList<NFproduct>();


   public static ArrayList<NFproduct> readCapabilities(String capabilitiesRequest) {
 
    	
    	//Create a "parser factory" for creating SAX parsers
        SAXParserFactory spfac = SAXParserFactory.newInstance();

        //Now use the parser factory to create a SAXParser object
        SAXParser sp;
        //Create an instance of this class; it defines all the handler methods
        NFallLayerRequestParser handler = new NFallLayerRequestParser();

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
        
		return nfproductList; 
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
          if (qName.equalsIgnoreCase("product")) {
        	     nfproduct = new NFproduct();        	     
        	     nfproduct.setName(attributes.getValue("name"));
        	     nfproduct.setId(Integer.parseInt(attributes.getValue("id")));
        	     nfproduct.setSubId(Integer.parseInt(attributes.getValue("subid")));
        	     nfproduct.setSrs(Integer.parseInt(attributes.getValue("srs")));
        	     nfproduct.setXll(Double.parseDouble(attributes.getValue("xll")));
        	     nfproduct.setYll(Double.parseDouble(attributes.getValue("yll")));
        	     nfproduct.setXur(Double.parseDouble(attributes.getValue("xur")));        	     
        	     nfproduct.setYur(Double.parseDouble(attributes.getValue("yur")));
          }
          else if (qName.equalsIgnoreCase("layer")) {        	  
        	  layer = nfproduct.new Layer();
        	  layer.setId(Integer.parseInt(attributes.getValue("id")));
        	  layer.setImportType(attributes.getValue("importType"));
        	  layer.setName(attributes.getValue("name"));
        	  nfproduct.setLayer(layer);        	  
          }              	  
        	  
               	 
   }

   /*
    * When the parser encounters the end of an element, it calls this method
    */
   public void endElement(String uri, String localName, String qName)
                 throws SAXException {

          if (qName.equalsIgnoreCase("product")) {
                 // add it to the list
        	  nfproductList.add(nfproduct);          
          }
                 
   }

   private void readList() {
	   if (false){
          System.out.println("Number of products: '" + nfproductList.size()  + "'.");
          Iterator<NFproduct> it = nfproductList.iterator();
          while (it.hasNext()) {
                 System.out.println(it.next().toString());
          }
	   }
   }
        
 
   
 
}