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

public class WfsSolarAttributesParser extends DefaultHandler{
	
	private String temp;  
	
	private static String output;
	

	public static String parse(String toBeParsed, Feature feature) {	
		      String intro = "    -----SOLAR ATTIBUTES WFS INFOS-----    ";
		      if (WWJApplet.DEBUG){
		    	  intro.concat("\nPicked ID: "+feature.id + "\nParent ID: " +feature.parent.id);
		      }
		      output = intro;
	           	//Create a "parser factory" for creating SAX parsers
		        SAXParserFactory spfac = SAXParserFactory.newInstance();
		        //Now use the parser factory to create a SAXParser object
		        SAXParser sp;
		        //Create an instance of this class; it defines all the handler methods
		        WfsSolarAttributesParser handler = new WfsSolarAttributesParser();


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
				if (output.equals(intro))	  
				{
					output.concat("\nNo SOLAR WFS info available.");
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
		   if(WWJApplet.DEBUG){
			   if (qName.contains(WWJApplet.getPilotManager().getCurrentPilot().getName()+":ROOFSURFACE_SOL")) {	       
				   if(attributes.getValue("gml:id")!=null || attributes.getValue("gml:id")!=""){
					   String featureID = attributes.getValue("gml:id");
	//				   featureID = featureID.substring(featureID.indexOf(".")+1);
					   output = output.concat("\nPickend id from WFS: " + featureID);
				   }
		         }	               
		   }
	   }

	   /*
	    * When the parser encounters the end of an element, it calls this method
	    */
	   public void endElement(String uri, String localName, String qName)
	                 throws SAXException {
		    
//		      if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":REFBID")) {	 
//		    	  output = output.concat("\nREFBID: "+temp);
//	          }
//		      else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":ROOFID")) {	                
//		    	  output = output.concat("\nROOFID: " + temp);
//	          }
	          if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":ROOFAREA")) {	                
	        	  output = output.concat("\nROOFAREA: " + temp );
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":ORIENTATION")) {	                
	        	  output = output.concat("\nORIENTATION: " + temp);
	          }
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONYEAR")) {	                
	        	  output = output.concat("\n----SOLAR IRRADIATION IN THE GIVEN PERIOD----");
	        	  output = output.concat("\nYear : " + Double.parseDouble(temp)*365/1000 + " KWh/(m^2)");
	          }	      
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM01")) {	                
	        	  output = output.concat("\nGenuary : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	 
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM02")) {	                
	        	  output = output.concat("\nFebruary : " + Double.parseDouble(temp)*28/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM03")) {	                
	        	  output = output.concat("\nMarch : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM04")) {	                
	        	  output = output.concat("\nApril : " + Double.parseDouble(temp)*30/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM05")) {	                
	        	  output = output.concat("\nMay : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM06")) {	                
	        	  output = output.concat("\nJune : " + Double.parseDouble(temp)*30/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM07")) {	                
	        	  output = output.concat("\nJuly : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM08")) {	                
	        	  output = output.concat("\nAugust : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM09")) {	                
	        	  output = output.concat("\nSeptember : " + Double.parseDouble(temp)*30/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM10")) {	                
	        	  output = output.concat("\nOctober : " + Double.parseDouble(temp)*31/1000 + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM11")) {	                
	        	  output = output.concat("\nNovember : " + Double.parseDouble(temp)*30/1000  + " KWh/(m^2)");
	          }	
	          else if (qName.equalsIgnoreCase(WWJApplet.getPilotManager().getCurrentPilot().getName()+":IRRADIATIONM12")) {	                
	        	  output = output.concat("\nDecember : " + Double.parseDouble(temp)*31/1000  + " KWh/(m^2)");
	          }		        
	   }

	   private void readList() {
		   if (false){
	          System.out.println(output);	
		   }
	   }	   

	 
	
}
