package iScope;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.ScreenImage;

import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import selector.MySectorSelector;

public class GUILayer extends RenderableLayer
{
	protected ScreenAnnotation loadingData;
	protected ScreenAnnotation loadingBuildings;
	private boolean downloadSpinnerEnabled = false;
	private ScreenImage spinner;
	
	public MySectorSelector solarSelector;
	public MySectorSelector noiseSelector;

	public GUILayer()
	{
		super();
		this.setName(Props.getStr("GUILayerName"));
		spinner = new ScreenImage();
		spinner.setImageSource("media/spinner.png");
		spinner.setRotationOffset(new Offset(0.5, 0.5, AVKey.FRACTION, AVKey.FRACTION));
		new Timer(50, actionListener).start();
		
		solarSelector = new MySectorSelector(WWJApplet.getWWD());		
		noiseSelector = new MySectorSelector(WWJApplet.getWWD());			
		noiseSelector.addAreaListener(0.01d);
	}


	private double i = 0;
	int count = 0;
	public ActionListener actionListener = new ActionListener()
	{
		public void actionPerformed(java.awt.event.ActionEvent actionEvent)
		{
			count++;
			if (WorldWind.getRetrievalService().hasActiveTasks())
			{				
				i = i - 10;
				spinner.setRotation(i);
				spinner.setScreenLocation(new Point(WWJApplet.getWWD().getWidth() - 70, 70));
				WWJApplet.getWWD().redraw();
				if (downloadSpinnerEnabled == false)
				{
					addRenderable(spinner);
					downloadSpinnerEnabled = true;
				}
			} else
			{
//				firstIn = true;
				if (downloadSpinnerEnabled == true)
				{
					removeRenderable(spinner);
					downloadSpinnerEnabled = false;
				}
			}
		}
	};
}
