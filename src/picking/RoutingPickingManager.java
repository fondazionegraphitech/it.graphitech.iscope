package picking;

import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.util.BasicDragger;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class RoutingPickingManager
{
	public IconLayer iconLayer = new IconLayer();
	
	public static List<RoutingPointsIcon> routingPointsAnnotations = new ArrayList<>();
	List<RoutingPickingListener> routingPickingListeners = new ArrayList<>();
	
	private Polyline routePolyline;
	
	private boolean enabled = false;
	private boolean pickingFirst = true;

	public boolean isEnabled()
	{
		return enabled;
	}

	public RoutingPickingManager()
	{
		
		WWJApplet.getWWD().getInputHandler().addMouseListener(mouseListener);
		WWJApplet.getWWD().addSelectListener(selectListener);
		iconLayer.setName(Props.getStr("PickingIconLayerName"));
		WWJApplet.getWWD().getModel().getLayers().add(iconLayer);
	}

	public void addRoutingPickingListener(RoutingPickingListener routingPickingListener)
	{
		routingPickingListeners.add(routingPickingListener);
	}

	private void addPoint()
	{
		
		RoutingPointsIcon routingPointsAnnotation = new RoutingPointsIcon(this, pickingFirst, WWJApplet.getWWD().getCurrentPosition());
		routingPointsAnnotations.add(routingPointsAnnotation);
		iconLayer.addIcon(routingPointsAnnotation);	
		doFireListeners(routingPointsAnnotation);
		WWJApplet.getWWD().redrawNow();
		if (pickingFirst) 
		{
			//Start picking second point
			pickingFirst = false;
		} else
		{
			//Picked second point, disable picking
			enabled = false;
			pickingFirst = true;
		}
	}

	private void doFireListeners(RoutingPointsIcon routingPointsIcon)
	{
		if (pickingFirst && !enabled)
		{
			for (RoutingPickingListener routingPickingListener : routingPickingListeners)
			{
				routingPickingListener.PickChanged(routingPointsIcon, routingPointsAnnotations);
			}
		}
	}

	public List<Position> getRoutePoints()
	{
		List<Position> positionList = new ArrayList<>();
		for (RoutingPointsIcon rPA : routingPointsAnnotations)
		{
			positionList.add(rPA.getPosition());
		}
		return positionList;
	}

	private MouseListener mouseListener = new MouseListener()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (enabled)
			{
				addPoint();
				e.consume();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e)
		{

		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	private SelectListener selectListener = new SelectListener()
	{
		private BasicDragger dragger = new BasicDragger(WWJApplet.getWWD());
		private RoutingPointsIcon lastIcon;

		@Override
		public void selected(SelectEvent e)
		{
			// Have drag events drag the selected object.
			if ((e.getEventAction().equals(SelectEvent.DRAG_END) || e.getEventAction().equals(SelectEvent.DRAG)) && e.getTopObject() instanceof RoutingPointsIcon)
			{
				lastIcon = (RoutingPointsIcon) e.getTopObject();
				this.dragger.selected(e);
			}
			if ((e.getEventAction().equals(SelectEvent.DRAG_END)) && e.getTopObject() instanceof RoutingPointsIcon)
			{
				doFireListeners(lastIcon);
			}
		}
	};

	public void startPicking()
	{
		iconLayer.removeAllIcons();
		routingPointsAnnotations.clear();
		enabled = true;
	}

	public void stopPicking()
	{
		iconLayer.removeAllIcons();
		routingPointsAnnotations.clear();
		
		enabled = false;
	}

	public Polyline getRoutePolyline()
	{
		return routePolyline;
	}

	public void setRoutePolyline(Polyline routePolyline)
	{
		this.routePolyline = routePolyline;
	}

	public void clearPositions()
	{
		try
		{
		iconLayer.removeAllIcons();
		routingPointsAnnotations.clear();
		} catch (Exception e)
		{
			System.out.println("Error in clearing route points but well... shit happens..");
		}
	}
	
}
