package selector;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwindx.examples.util.SectorSelector;
import iScope.WWJApplet;

import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class MySectorSelector extends SectorSelector {
	
	private boolean isEnabled = false;		
	private boolean tooBig; 
	
	public MySectorSelector(WorldWindow wwd) {
		super(wwd);				
	}		
	
	@Override
	public void enable() {		
		super.enable();
		isEnabled=true;	
	}

	@Override
	public void disable(){
		super.disable();		
		WWJApplet.getWWD().setCursor(Cursor.getDefaultCursor());
		isEnabled = false;
	}
	
	public boolean isEnable(){
		return isEnabled;
	}
	
	public boolean tooBig(){
		return tooBig;
	}
	
	public void changeColor(Color color){
		this.setBorderColor(color);
	}	
	
	public void addAreaListener (final double maxSelectedArea){	
		addAreaListener(maxSelectedArea,Color.GREEN, Color.RED);
	}
	
	public void addAreaListener (final double maxSelectedArea, final Color smallColor, final Color bigColor){
		
		PropertyChangeListener selectorChangeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				Sector sector = (Sector) evt.getNewValue();
				if (sector != null && !sector.equals(Sector.EMPTY_SECTOR))
				{
					if (sector.getDeltaLonDegrees() * sector.getDeltaLatDegrees() > maxSelectedArea)
					{ 
						changeColor(bigColor);			
						tooBig = true;												
					} else {
						changeColor(smallColor);
					    tooBig = false;			
					    }
				}
			}
		};
		
		this.addPropertyChangeListener(selectorChangeListener);
	}

}
