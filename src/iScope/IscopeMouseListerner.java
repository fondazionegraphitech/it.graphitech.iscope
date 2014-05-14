package iScope;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import nFinteractionManager.Tile2D;

public class IscopeMouseListerner implements MouseListener {
	
	IscopeSelectController selectController;
	
	public IscopeMouseListerner(IscopeSelectController selectController){
		this.selectController = selectController;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {		
		if (selectController.getLastHighlightObject() instanceof Tile2D){
			Tile2D tile2d = (Tile2D) selectController.getLastHighlightObject();			
			WWJApplet.jS.call("setTileInfos", new java.lang.String[] {java.lang.String.valueOf(tile2d.getTileNumber()),java.lang.String.valueOf(tile2d.getStatus())});
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
