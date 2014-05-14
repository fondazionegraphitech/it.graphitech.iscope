package wfs;

import geometryFormat.Feature;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.ScreenAnnotation;
import iScope.WWJApplet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;



public class WfsManager {
	
	private ScreenAnnotation wfsScreenAnnotation = null;
	private ArrayList<ScreenAnnotation> activeAnnotations = new ArrayList<>();	

	public String newWfsROOFATTRIBUTERequest(Feature feature){	
		if ( feature != null)
		{	
			String request = "http://77.72.192.34:8080/geoserver/" + WWJApplet.getPilotManager().getCurrentPilot().getName() + "/wfs?service=wfs&version=2.0.0&request=GetFeature&typeNames="
					+ WWJApplet.getPilotManager().getCurrentPilot().getName().toUpperCase() + ":" + WWJApplet.getPilotManager().getCurrentPilot().getName() + "_ROOFATTRIBUTE" + "&featureID=" + feature.id.toString();
			String respone = httpConnection.httpGet.sendGetRequest(request);
			String parsedWfsString = WfsRoofAttributesParser.parse(respone,feature);
			return parsedWfsString;		
		}
		return null;
	}
	
	public String newWfsSolarRequest(Feature feature){	
		if ( feature != null)
		{	
			String request = "http://77.72.192.34:8080/geoserver/" + WWJApplet.getPilotManager().getCurrentPilot().getName() + "/wfs?service=wfs&version=2.0.0&request=GetFeature&typeNames="
					+ WWJApplet.getPilotManager().getCurrentPilot().getName().toUpperCase() + ":" + "ROOFSURFACE_SOL" + "&FILTER=" + "<Filter><PropertyIsEqualTo><PropertyName>ROOFID</PropertyName><Literal>"+ feature.id.toString() +"</Literal></PropertyIsEqualTo></Filter>";

			String respone = httpConnection.httpGet.sendGetRequest(request);
			
			System.out.println(respone);
			
			String parsedWfsString = WfsSolarAttributesParser.parse(respone,feature);
			return parsedWfsString;		
		}
		return null;
	}
	
	public void removeActiveAnnotations(){
		if(activeAnnotations.size()>0){
			for (ScreenAnnotation annotation : activeAnnotations) {
				WWJApplet.annotationLayer.removeAnnotation(annotation);
				annotation=null;				
			}
		}
		activeAnnotations.clear();
	}
	
	public void removeAllAnnotations(){
		WWJApplet.annotationLayer.removeAllAnnotations();
		activeAnnotations.clear();
	}
	
	
	public void addWFSAnnotation(String parsedWfsString, Point annotationPosition, Dimension dim)
	{		
			parsedWfsString = parsedWfsString.trim();
			AnnotationAttributes defaultAttributes = new AnnotationAttributes();
			defaultAttributes.setCornerRadius(0);				
			//			defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
			defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
			defaultAttributes.setTextColor(Color.WHITE);		          
			//				defaultAttributes.setDrawOffset(new Point(0, 100));
			defaultAttributes.setLeader("void");		
//			defaultAttributes.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);	
			defaultAttributes.setAdjustWidthToText(AVKey.SIZE_FIXED);
			defaultAttributes.setSize(dim);

			if (parsedWfsString != "No info available."){			
				wfsScreenAnnotation = new ScreenAnnotation(parsedWfsString, annotationPosition, defaultAttributes);						
			}
			else 				
				wfsScreenAnnotation = new ScreenAnnotation(parsedWfsString, annotationPosition,defaultAttributes);
			WWJApplet.annotationLayer.addAnnotation(wfsScreenAnnotation);	
			activeAnnotations.add(wfsScreenAnnotation);
	}


	public class wfsSolarRequestThread implements Runnable {

		Feature feature;		
		public wfsSolarRequestThread(Feature feature) {
			this.feature = feature;		
		}

		public void run() {			
			String response = newWfsSolarRequest(feature);
			
			addWFSAnnotation(response, new Point(250,50), new Dimension(320, 370));		

		}
	}
	
	public class wfsRoofAttRequestThread implements Runnable {
		Feature feature;	
		public wfsRoofAttRequestThread(Feature feature) {
			this.feature = feature;		
		}

		public void run() {			
				String response = newWfsROOFATTRIBUTERequest(feature);
//				System.out.println(WWJApplet.getApplet().getWidth());
				addWFSAnnotation(response, new Point(WWJApplet.getApplet().getWidth() -200,50),new Dimension(320, 370));									
		}
	}
}
	
	


