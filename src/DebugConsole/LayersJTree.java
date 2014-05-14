package DebugConsole;

import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import iScope.WWJApplet;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class LayersJTree extends JTree
{
	private static final long serialVersionUID = -955601092468057595L;
	private LayerList layers;
	public static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Layers");
	public static DefaultMutableTreeNode lastSelectedNode;

	public LayersJTree()
	{
		super(root);
		this.layers = WWJApplet.getWWD().getModel().getLayers();
		this.setRootVisible(true);
		this.updateList();
		
			
		this.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				lastSelectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();				
			}
		});
	}

	public void updateList()
	{
		root.removeAllChildren();
		for (Layer layer : layers)
		{
			DefaultMutableTreeNode dMTN = new DefaultMutableTreeNode(layer.getName());
			dMTN.setUserObject(layer);
			root.add(dMTN);
		}
	}
}
