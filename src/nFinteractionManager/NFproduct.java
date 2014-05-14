package nFinteractionManager;

import gov.nasa.worldwind.layers.RenderableLayer;

import java.util.ArrayList;

public class NFproduct {
	
	private String name;	
	private int id;
	private int subId;
	private int srs;
    private double xll, xur, yll, yur;
    public ArrayList<Tile2D> tiles = new ArrayList<>();
    private RenderableLayer tileLayer = new RenderableLayer();    
    private Layer wmsTileLayer;
    

    public NFproduct() {
    }
    
    public NFproduct(String name, int srs, double xll, double xur, double yll,
			double yur, String type, Layer layer) {
		
    	this.name = name;
		this.srs = srs;
		this.xll = xll;
		this.xur = xur;
		this.yll = yll;
		this.yur = yur;		
		this.wmsTileLayer = layer;
	}
    
    
    
    public RenderableLayer getTileLayer() {
    	return tileLayer;
	}

	public void setTileLayer(RenderableLayer tileLayer) {
		this.tileLayer = tileLayer;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSrs() {
		return srs;
	}

	public void setSrs(int srs) {
		this.srs = srs;
	}

	public double getXll() {
		return xll;
	}

	public void setXll(double xll) {
		this.xll = xll;
	}

	public double getXur() {
		return xur;
	}

	public void setXur(double xur) {
		this.xur = xur;
	}

	public double getYll() {
		return yll;
	}

	public void setYll(double yll) {
		this.yll = yll;
	}

	public double getYur() {
		return yur;
	}

	public void setYur(double yur) {
		this.yur = yur;
	}
	

	public Layer getLayer() {
		return wmsTileLayer;
	}

	public void setLayer(Layer layer) {
		this.wmsTileLayer = layer;
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId(int subId) {
		this.subId = subId;
	}


    
    @Override
	public String toString() {
		return "NFproduct [name=" + name + ", id=" + id + ", subId=" + subId
				+ ", srs=" + srs + ", xll=" + xll + ", xur=" + xur + ", yll="
				+ yll + ", yur=" + yur + ", layer=" + wmsTileLayer + "]";
	}  
    

	class Layer {
    	int id;
		String importType;
		String name;

		public Layer() {
		}		
		

		public int getId() {
			return id;
		}


		public void setId(int id) {
			this.id = id;
		}


		public Layer(String importType, String name) {
			this.importType=importType;
			this.name = name;
		}

		public String getImportType() {
			return importType;
		}

		public void setImportType(String importType) {
			this.importType = importType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


		@Override
		public String toString() {
			return "Layer [id=" + id + ", importType=" + importType + ", name="
					+ name + "]";
		}
		

	  }


}
