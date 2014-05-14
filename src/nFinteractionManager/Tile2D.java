package nFinteractionManager;

import gov.nasa.worldwind.render.SurfacePolygon;

public class Tile2D extends SurfacePolygon{	
//		SurfacePolygon surfacePolygon;
		int tileNumber;
		int status;
		boolean ishighlited = false;
		
		public Tile2D(int tileNumber, int status){	
			super();
			this.tileNumber = tileNumber;
			this.status = status;
			}			


//		public SurfacePolygon getSurfacePolygon() {
//			return surfacePolygon;
//		}
//
//		public void setSurfacePolygon(SurfacePolygon surfacePolygon) {
//			this.surfacePolygon = surfacePolygon;
//		}

		public int getTileNumber() {
			return tileNumber;
		}

		public void setTileNumber(int tileNumber) {
			this.tileNumber = tileNumber;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}


		@Override
		public String toString() {
			return "Tile number" + tileNumber +", status=" + status;
		}
//
//
//		@Override
//		public boolean isHighlighted() {			
//			return ishighlited;
//		}
//
//
//		@Override
//		public void setHighlighted(boolean arg0) {
//			ishighlited = arg0;			
//		}				
//		
}


