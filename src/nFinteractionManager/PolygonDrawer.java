package nFinteractionManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PreRenderable;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.util.Logging;
import iScope.WWJApplet;

public class PolygonDrawer extends AVListImpl{
	
	protected ArrayList<Position> positions = new ArrayList<Position>();
	public static final String CONTROL_TYPE_LOCATION_INDEX = "MeasureTool.ControlTypeLocationIndex";
    private static final String PROPERTY_CHANGE_SUPPORT = "avlist.PropertyChangeSupport";
	public static final String EVENT_POSITION_ADD = "MeasureTool.AddPosition";
    protected ArrayList<Renderable> controlPoints = new ArrayList<Renderable>();
    protected CustomRenderableLayer controlPointsLayer;
    protected AnnotationAttributes controlPointsAttributes;
    protected AnnotationAttributes controlPointWithLeaderAttributes;
    protected ShapeAttributes leaderAttributes;
    protected SurfaceShape surfaceShape;
    protected CustomRenderableLayer shapeLayer;
    protected final WorldWindow wwd;
    protected RenderableLayer applicationLayer;
    protected CustomRenderableLayer layer;
    protected boolean showControlPoints = true;
        
    protected Color lineColor = Color.YELLOW;
    protected Color fillColor = new Color(.6f, .6f, .4f, .5f);
    protected double lineWidth = 2;
    
    protected static class CustomRenderableLayer extends RenderableLayer implements PreRenderable, Renderable
    {
        public void render(DrawContext dc)
        {
            if (dc.isPickingMode() && !this.isPickEnabled())
                return;
            if (!this.isEnabled())
                return;

            super.render(dc);
        }
    }
    
    public PolygonDrawer(final WorldWindow wwd){
    	
    	if (wwd == null)
        {
            String msg = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.wwd = wwd;       

        // Set up layers

        this.layer = createCustomRenderableLayer();
        this.shapeLayer = createCustomRenderableLayer();
        this.controlPointsLayer = createCustomRenderableLayer();

        this.shapeLayer.setPickEnabled(false);
        this.layer.setName("Measure Tool");
        this.layer.addRenderable(this.shapeLayer);          // add shape layer to render layer
        this.layer.addRenderable(this.controlPointsLayer);  // add control points layer to render layer
        this.controlPointsLayer.setEnabled(this.showControlPoints);
        this.layer.setEnabled(true);
        this.wwd.getModel().getLayers().add(this.layer);    // add render layer to the globe model
    	
    	 this.controlPointsLayer = createCustomRenderableLayer();
    	
    	 // Init control points rendering attributes
        this.controlPointsAttributes = new AnnotationAttributes();
        // Define an 8x8 square centered on the screen point
        this.controlPointsAttributes.setFrameShape(AVKey.SHAPE_RECTANGLE);
        this.controlPointsAttributes.setLeader(AVKey.SHAPE_NONE);
        this.controlPointsAttributes.setAdjustWidthToText(AVKey.SIZE_FIXED);
        this.controlPointsAttributes.setSize(new Dimension(8, 8));
        this.controlPointsAttributes.setDrawOffset(new Point(0, -4));
        this.controlPointsAttributes.setInsets(new Insets(0, 0, 0, 0));
        this.controlPointsAttributes.setBorderWidth(0);
        this.controlPointsAttributes.setCornerRadius(0);
        this.controlPointsAttributes.setBackgroundColor(Color.BLUE);    // Normal color
        this.controlPointsAttributes.setTextColor(Color.GREEN);         // Highlighted color
        this.controlPointsAttributes.setHighlightScale(1.2);
        this.controlPointsAttributes.setDistanceMaxScale(1);            // No distance scaling
        this.controlPointsAttributes.setDistanceMinScale(1);
        this.controlPointsAttributes.setDistanceMinOpacity(1);
    	
        this.controlPointWithLeaderAttributes = new AnnotationAttributes();
    	 this.controlPointWithLeaderAttributes.setDefaults(this.controlPointsAttributes);
    	 
    	 this.leaderAttributes = new BasicShapeAttributes();
         this.leaderAttributes.setOutlineMaterial(Material.WHITE);
         this.leaderAttributes.setOutlineOpacity(0.7);
         this.leaderAttributes.setOutlineWidth(3);
         
      
         
         
    }   
    
    
    protected static class ControlPointWithLeader extends ControlPoint implements PreRenderable
    {
        protected SurfacePolyline leaderLine;

        public ControlPointWithLeader(Position position, AnnotationAttributes controlPointAttributes,
            ShapeAttributes leaderAttributes, PolygonDrawer parent)
        {
            super(position, controlPointAttributes, parent);

            this.leaderLine = new SurfacePolyline(leaderAttributes);
        }

        public void preRender(DrawContext dc)
        {
            if (dc == null)
            {
                String message = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            if (this.leaderLine != null)
                this.leaderLine.preRender(dc);
        }

        @Override
        public void render(DrawContext dc)
        {
            if (dc == null)
            {
                String message = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            if (this.leaderLine != null)
                this.leaderLine.render(dc);

            super.render(dc);
        }

        public void setLeaderLocations(LatLon begin, LatLon end)
        {
            if (begin == null)
            {
                String message = Logging.getMessage("nullValue.BeginIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            if (end == null)
            {
                String message = Logging.getMessage("nullValue.EndIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            this.leaderLine.setLocations(Arrays.asList(begin, end));
        }
    }
    
    protected CustomRenderableLayer createCustomRenderableLayer()
    {
        return new CustomRenderableLayer();
    }
    
    protected void addControlPointWithLeader(Position position, String controlKey, Object control, String leaderKey,
            Object leader)
        {
            ControlPointWithLeader controlPoint = new ControlPointWithLeader(new Position(position, 0),
                this.controlPointWithLeaderAttributes, this.leaderAttributes, this);
            controlPoint.setValue(controlKey, control);
            controlPoint.setValue(leaderKey, leader);
            this.doAddControlPoint(controlPoint);
        }
	
    public AnnotationAttributes getControlPointsAttributes()
    {
        return this.controlPointsAttributes;
    }    
 
	
	 protected void addControlPoint(Position position, String key, Object value)
	    {
	        ControlPoint controlPoint = new ControlPoint(new Position(position, 0), this.controlPointsAttributes, this);
	        controlPoint.setValue(key, value);
	        this.doAddControlPoint(controlPoint);
	    }
	 
	 protected void doAddControlPoint(ControlPoint controlPoint)
	    {
	        this.controlPoints.add(controlPoint);
	        this.controlPointsLayer.setRenderables(this.controlPoints);
	    }
	
	/** Add a control point to the current measure shape at the cuurrent WorldWindow position. */
    public void addControlPoint()
    {
        Position curPos = WWJApplet.getWWD().getCurrentPosition();
        if (curPos == null)
            return;

		if (this.positions.size() <= 1) {
			// Line, path or polygons with less then two points
			this.positions.add(curPos);
			addControlPoint(this.positions.get(this.positions.size() - 1),
					CONTROL_TYPE_LOCATION_INDEX, this.positions.size() - 1);
			if (this.positions.size() == 2) {
				// Once we have two points of a polygon, add an extra position
				// to loop back to the first position and have a closed shape
				this.positions.add(this.positions.get(0));
			}			
		} else {
			// For polygons with more then 2 points, the last position is the
			// same as the first, so insert before it
			this.positions.add(positions.size() - 1, curPos);
			addControlPoint(this.positions.get(this.positions.size() - 2),
					CONTROL_TYPE_LOCATION_INDEX, this.positions.size() - 2);
		}

        // Update screen shapes
        updateMeasureShape();
        this.firePropertyChange(EVENT_POSITION_ADD, null, curPos);
        WWJApplet.getWWD().redraw();
    }
    
    public Color getFillColor()
    {
        return this.fillColor;
    }
    
    public Color getLineColor()
    {
        return this.lineColor;
    }
    
    public double getLineWidth()
    {
        return this.lineWidth;
    }
 
    
    
    protected void updateMeasureShape()
 {
		if (this.positions.size() >= 4 && this.surfaceShape == null) {
			// Init surface shape
			this.surfaceShape = new SurfacePolygon(this.positions);
			ShapeAttributes attr = new BasicShapeAttributes();
			attr.setInteriorMaterial(new Material(this.getFillColor()));
			attr.setInteriorOpacity(this.getFillColor().getAlpha() / 255d);
			attr.setOutlineMaterial(new Material(this.getLineColor()));
			attr.setOutlineOpacity(this.getLineColor().getAlpha() / 255d);
			attr.setOutlineWidth(this.getLineWidth());
			this.surfaceShape.setAttributes(attr);
			this.shapeLayer.addRenderable(this.surfaceShape);
		}
		if (this.positions.size() <= 3 && this.surfaceShape != null) {
			// Remove surface shape if only three positions or less - last is
			// same as first
			this.shapeLayer.removeRenderable(this.surfaceShape);
			this.surfaceShape = null;
		}
		if (this.surfaceShape != null) {
			// Update current shape
			((SurfacePolygon) this.surfaceShape).setLocations(this.positions);
		}
	}
        
    
 // *** Control points ***
    public static class ControlPoint extends GlobeAnnotation
    {
    	PolygonDrawer parent;

        public ControlPoint(Position position, AnnotationAttributes attributes, PolygonDrawer parent)
        {
            super("", position, attributes);
            this.parent = parent;
        }

        public PolygonDrawer getParent()
        {
            return this.parent;
        }
    }



	
	
		
	

	
	
}
