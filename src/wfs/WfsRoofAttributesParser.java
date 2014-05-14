package wfs;

import geometryFormat.Feature;
import iScope.WWJApplet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WfsRoofAttributesParser extends DefaultHandler{
	
	private String temp;  
	
	private static String output;
	

	public static String parse(String toBeParsed, Feature feature) {	   	
	   	   output = "    -----ROOF ATTRIBUTES WFS INFOS-----    " + "\nPicked ID: "+feature.id + "\nParent ID: " +feature.parent.id;	      
	           	//Create a "parser factory" for creating SAX parsers
		        SAXParserFactory spfac = SAXParserFactory.newInstance();
		        //Now use the parser factory to create a SAXParser object
		        SAXParser sp;
		        //Create an instance of this class; it defines all the handler methods
		        WfsRoofAttributesParser handler = new WfsRoofAttributesParser();


		        ByteArrayInputStream byteArray;
				try {
					sp = spfac.newSAXParser();
					byteArray = new ByteArrayInputStream(toBeParsed.getBytes("utf-8"));
					InputSource inputSource = new InputSource(byteArray);
			        //Finally, tell the parser to parse the input and notify the handler
			        sp.parse(inputSource, handler);  		        
			        handler.readList();
				} catch (SAXException | IOException | ParserConfigurationException e) {
					System.err.println("Problems reading the capabilities response");
					e.printStackTrace();
				}	   
				if (output.equals("    -----INFOS-----    " + "\nPicked ID: "+feature.id + "\nParent ID: " +feature.parent.id))	  
				{
					output.concat("\nNo ROOF WFS info available.");
				}
				return output;			 
		   
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
		   if (qName.contains(WWJApplet.getPilotManager().getCurrentPilot().getName()+"_ROOFATTRIBUTE")) {	       
			   if(attributes.getValue("gml:id")!=null || attributes.getValue("gml:id")!=""){
				   String featureID = attributes.getValue("gml:id");
				   featureID = featureID.substring(featureID.indexOf(".")+1);
				   output = output.concat("\nPickend id from WFS: " + featureID);
			   }
	         }	                	 
	   }

	   /*
	    * When the parser encounters the end of an element, it calls this method
	    */
	   public void endElement(String uri, String localName, String qName)
	                 throws SAXException {
		    
		      if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":CLASS_ID")) {	 
		    	  output = output.concat("\nCLASS OF PICKED PART: ");
		    	  if(temp.equalsIgnoreCase("25"))  output = output.concat("Building Part");
		    	  if(temp.equalsIgnoreCase("26"))  output = output.concat("Building");
		    	  if(temp.equalsIgnoreCase("33"))  output = output.concat("RoofSurface");
		    	  else output = output.concat("unknow having code: "+temp);
	          }
		      else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":SOLROOFADDICT")) {	                
		    	  output = output.concat("\nRoof slope: " + temp);
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":SOLROOFAREA")) {	                
	        	  output = output.concat("\nRoof area: " + temp );
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":SOLROOFDIFF")) {	                
	        	  output = output.concat("\nAngle between roof normals and sun position: " + temp);
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":SOLROOFORIENT")) {	                
	        	  output = output.concat("\nRoof orientation: " + temp);
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":ROOFDETECTIONQUALITY")) {	                
	        	  output = output.concat("\nRoof detection quality: " + temp);
	          }	                 
	   }

	   private void readList() {
		   if (false){
	          System.out.println(output);	
		   }
	   }	   

	 
	
}
