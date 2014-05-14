package noise;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.Color;
import java.awt.Point;

public class NoiseMarkerLayer extends MarkerLayer
{
	private ScreenAnnotation noiseAnnotation = new ScreenAnnotation("", new Point());
	private NoiseMarker lastMarkHighlit;
	private BasicMarkerAttributes lastMarkAttrs;
	
	public NoiseMarkerLayer()
	{
		super();
		this.setName(Props.getStr("NoiseMarkerLayerName"));		
		
		AnnotationAttributes defaultAttributes = new AnnotationAttributes();
		defaultAttributes.setCornerRadius(0);				
		//			defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
		defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
		defaultAttributes.setTextColor(Color.WHITE);		          
		//				defaultAttributes.setDrawOffset(new Point(0, 100));
		defaultAttributes.setLeader("void");		
		defaultAttributes.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);	
		noiseAnnotation.setAttributes(defaultAttributes);
		noiseAnnotation.getAttributes().setVisible(false);
		
		WWJApplet.annotationLayer.addAnnotation(noiseAnnotation);
		
		WWJApplet.getWWD().addSelectListener(new SelectListener()
		{
			@Override
			public void selected(SelectEvent event)
			{

                if (lastMarkHighlit != null
                    && (event.getTopObject() == null || !event.getTopObject().equals(lastMarkHighlit)))
                {
                	lastMarkHighlit.setAttributes(lastMarkAttrs);
                	lastMarkHighlit = null;
                	noiseAnnotation.getAttributes().setVisible(false);
                }

                if (!event.getEventAction().equals(SelectEvent.ROLLOVER))
                    return;

                if (event.getTopObject() == null || event.getTopPickedObject().getParentLayer() == null)
                    return;

                if (lastMarkHighlit == null && event.getTopObject() instanceof NoiseMarker)
                {                	
                	lastMarkHighlit = (NoiseMarker) event.getTopObject();
                	NoisePoint noisePoint = ((NoiseMarker) lastMarkHighlit).getNoisePoint();
                	noiseAnnotation.setText("Acquired noise value: " + noisePoint.getDeciBel() + "dB\n" + "Acquisition time: " + noisePoint.getAquisitionTime());
					Point screenPoint = event.getPickPoint();
					try
					{
						screenPoint.y = WWJApplet.getWWD().getView().getViewport().height - screenPoint.y + 10;
						noiseAnnotation.setScreenPoint(screenPoint);
						noiseAnnotation.getAttributes().setVisible(true);							
					} catch (Exception e)
					{
						// do nothing
					}
					lastMarkAttrs = (BasicMarkerAttributes) lastMarkHighlit.getAttributes();
					MarkerAttributes highliteAttrs = new BasicMarkerAttributes(lastMarkAttrs);
					highliteAttrs.setOpacity(1d);
//					highliteAttrs.setMarkerPixels(lastMarkAttrs.getMarkerPixels() * higLightScaleFactor);
//					highliteAttrs.setMaxMarkerSize(lastMarkAttrs.getMaxMarkerSize() * higLightScaleFactor);
					lastMarkHighlit.setAttributes(highliteAttrs);
                }
			}
		});
	}
	
	
}
