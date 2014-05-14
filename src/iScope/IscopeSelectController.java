package iScope;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;

import nFinteractionManager.Tile2D;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Highlightable;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;

/**
 * Controls highlighting of shapes implementing {@link Highlightable} in response to pick events. Monitors a specified
 * World Window for an indicated {@link gov.nasa.worldwind.event.SelectEvent} type and turns highlighting on and off in
 * response.
 *
 * @author tag
 * @version $Id: HighlightController.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class IscopeSelectController implements SelectListener
{
    protected WorldWindow wwd;
    protected Object highlightEventType = SelectEvent.ROLLOVER;
    protected Highlightable lastHighlightObject;
    protected GlobeAnnotation ga;
    AnnotationAttributes defaultAttributes;

    public Highlightable getLastHighlightObject() {
		return lastHighlightObject;
	}

	/**
     * Creates a controller for a specified World Window.
     *
     * @param wwd                the World Window to monitor.
     * @param highlightEventType the type of {@link SelectEvent} to highlight in response to. The default is {@link
     *                           SelectEvent#ROLLOVER}.
     */
    public IscopeSelectController(WorldWindow wwd, Object highlightEventType)
    {
        this.wwd = wwd;
        this.highlightEventType = highlightEventType;
        this.wwd.addSelectListener(this);
        defaultAttributes = new AnnotationAttributes();
		defaultAttributes.setCornerRadius(0);
		defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
		defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
		defaultAttributes.setTextColor(Color.WHITE);
		defaultAttributes.setDrawOffset(new Point(0, 100));
		defaultAttributes.setLeader("void");
    }

    public void dispose()
    {
        this.wwd.removeSelectListener(this);
    }

    public void selected(SelectEvent event)
    {
    	if (this.highlightEventType != null && event.getEventAction().equals(this.highlightEventType))
    		if(event.getTopObject() instanceof Tile2D){
    			highlight(event.getTopObject());
    			String tovisualise = "Tile ID= " + ((Tile2D)event.getTopObject()).getTileNumber() +"\nTile status= " + ((Tile2D)event.getTopObject()).getStatus(); 
    			if(ga==null){            			
    				ga = new GlobeAnnotation(tovisualise, WWJApplet.getWWD().getCurrentPosition());
    				ga.setAltitudeMode(0);
    				ga.setAttributes(defaultAttributes);
    				WWJApplet.annotationLayer.addAnnotation(ga);
    			}
    			else{
    				ga.setText(tovisualise);
    				ga.setPosition(WWJApplet.getWWD().getCurrentPosition());
    			}            		
    		}
    		else{
    			if(ga!=null){
	    			WWJApplet.annotationLayer.removeAnnotation(ga);
	    			ga= null;
    			}
    		}               

    }

    protected void highlight(Object o)
    {
        if (this.lastHighlightObject == o)
            return; // same thing selected

        // Turn off highlight if on.
        if (this.lastHighlightObject != null)
        {
            this.lastHighlightObject.setHighlighted(false);
            this.lastHighlightObject = null;
        }

        // Turn on highlight if object selected.
        if (o instanceof Highlightable)
        {
            this.lastHighlightObject = (Highlightable) o;
            this.lastHighlightObject.setHighlighted(true);
        }
    }
}