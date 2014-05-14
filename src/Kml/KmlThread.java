package Kml;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.retrieve.RetrievalService;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.layertree.KMLLayerTreeNode;
import gov.nasa.worldwind.util.layertree.KMLNetworkLinkTreeNode;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwindx.examples.kml.KMLApplicationController;
import gov.nasa.worldwindx.examples.util.BalloonController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

public class KmlThread extends Thread
{
	/**
	 * Indicates the source of the KML file loaded by this thread. Initialized during construction.
	 */
	protected Object kmlSource;
	protected WorldWindowGLCanvas wwd;
	protected KMLApplicationController kmlAppController;
	private BalloonController balloonController;
	private LayerTree layerTree;
	private boolean useBallon = true;
	private KMLController kmlController;
	RenderableLayer kmlLayer = new RenderableLayer();
	
	public KmlThread(Object kmlSource, WorldWindowGLCanvas wwd)
	{
		this.kmlSource = kmlSource;
		this.wwd = wwd;
	}
	
	public RenderableLayer getKmlLayer()
	{
		return kmlLayer;
	}

	public KMLController getKmlController()
	{
		return kmlController;
	}

	
	/**
	 * Loads this worker thread's KML source into a new <code>{@link gov.nasa.worldwind.ogc.kml.KMLRoot}</code>, then adds the new <code>KMLRoot</code> to this
	 * worker thread's <code>AppFrame</code>. The <code>KMLRoot</code>'s <code>AVKey.DISPLAY_NAME</code> field contains a display name created from either the
	 * KML source or the KML root feature name.
	 * <p/>
	 * If loading the KML source fails, this prints the exception and its stack trace to the standard error stream, but otherwise does nothing.
	 */
	public void run()
	{
		try
		{
			// Add the on-screen layer tree, refreshing model with the
			// WorldWindow's current layer list. We intentionally refresh the tree's model before adding the layer that
			// contains the tree itself. This
			// prevents the tree's layer from being displayed in the tree itself.
			this.layerTree = new LayerTree(new Offset(20d, 160d, AVKey.PIXELS, AVKey.INSET_PIXELS));
			this.layerTree.getModel().refresh(wwd.getModel().getLayers());
			// Set up a layer to display the on-screen layer tree in the
			// WorldWindow. This layer is not displayed in
			// the layer tree's model. Doing so would enable the user to hide the
			// layer tree display with no way of
			// bringing it back.
			// // Add a controller to handle input events on the layer selector and
			// on browser balloons.
			// this.hotSpotController = new HotSpotController(wwd);
			// Add a controller to handle common KML application events.
			this.kmlAppController = new KMLApplicationController(wwd);
			if (useBallon)
			{
				// Add a controller to display balloons when placemarks are clicked.
				// We override the method addDocumentLayer
				// so that loading a KML document by clicking a KML balloon link
				// displays an entry in the on-screen layer
				// tree.
				this.balloonController = new BalloonController(wwd)
				{
					@Override
					protected void addDocumentLayer(KMLRoot document)
					{
						addKMLLayer(document);
					}
				};
				// Give the KML app controller a reference to the BalloonController
				// so that the app controller can open
				// KML feature balloons when feature's are selected in the on-screen
				// layer tree.
				this.kmlAppController.setBalloonController(balloonController);
			}
			// Set up to receive SSLHandshakeExceptions that occur during resource
			// retrieval.
			WorldWind.getRetrievalService().setSSLExceptionListener(new RetrievalService.SSLExceptionListener()
			{
				public void onException(Throwable e, String path)
				{
					System.out.println(path);
					System.out.println(e);
				}
			});
			KMLRoot kmlRoot = this.parse();
			// Set the document's display name
			kmlRoot.setField(AVKey.DISPLAY_NAME, formName(this.kmlSource, kmlRoot));
			// Schedule a task on the EDT to add the parsed document to a layer
			final KMLRoot finalKMLRoot = kmlRoot;
//			KMLAbstractFeature kmlAbstractFeature = finalKMLRoot.getFeature();
//			System.out.println();
			
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					addKMLLayer(finalKMLRoot);
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Parse the KML document.
	 * 
	 * @return The parsed document.
	 * 
	 * @throws IOException
	 *            if the document cannot be read.
	 * @throws XMLStreamException
	 *            if document cannot be parsed.
	 */
	protected KMLRoot parse() throws IOException, XMLStreamException
	{
		// KMLRoot.createAndParse will attempt to parse the document using a
		// namespace aware parser, but if that
		// fails due to a parsing error it will try again using a namespace
		// unaware parser. Note that this second
		// step may require the document to be read from the network again if the
		// kmlSource is a stream.
		return KMLRoot.createAndParse(this.kmlSource);
	}

	protected void addKMLLayer(KMLRoot kmlRoot)
	{
		// Create a KMLController to adapt the KMLRoot to the World Wind
		// renderable interface.
		kmlController = new KMLController(kmlRoot);
		// Adds a new layer containing the KMLRoot to the end of the WorldWindow's
		// layer list. This
		// retrieves the layer name from the KMLRoot's DISPLAY_NAME field.
		
		kmlLayer.setName((String) kmlRoot.getField(AVKey.DISPLAY_NAME));
		kmlLayer.addRenderable(kmlController);
		
		wwd.getModel().getLayers().add(kmlLayer);
		// Adds a new layer tree node for the KMLRoot to the on-screen layer tree,
		// and makes the new node visible
		// in the tree. This also expands any tree paths that represent open KML
		// containers or open KML network
		// links.
		KMLLayerTreeNode layerNode = new KMLLayerTreeNode(kmlLayer, kmlRoot);
		this.layerTree.getModel().addLayer(layerNode);
		this.layerTree.makeVisible(layerNode.getPath());
		layerNode.expandOpenContainers(this.layerTree);
		// Listens to refresh property change events from KML network link nodes.
		// Upon receiving such an event this
		// expands any tree paths that represent open KML containers. When a KML
		// network link refreshes, its tree
		// node replaces its children with new nodes created from the refreshed
		// content, then sends a refresh
		// property change event through the layer tree. By expanding open
		// containers after a network link refresh,
		// we ensure that the network link tree view appearance is consistent with
		// the KML specification.
		layerNode.addPropertyChangeListener(AVKey.RETRIEVAL_STATE_SUCCESSFUL, new PropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent event)
			{
				if (event.getSource() instanceof KMLNetworkLinkTreeNode)
				{
					// Manipulate the tree on the EDT.
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((KMLNetworkLinkTreeNode) event.getSource()).expandOpenContainers(layerTree);
							wwd.redraw();
						}
					});
				}
			}
		});
	}

	protected static String formName(Object kmlSource, KMLRoot kmlRoot)
	{
		KMLAbstractFeature rootFeature = kmlRoot.getFeature();
		if (rootFeature != null && !WWUtil.isEmpty(rootFeature.getName()))
			return rootFeature.getName();
		if (kmlSource instanceof File)
			return ((File) kmlSource).getName();
		if (kmlSource instanceof URL)
			return ((URL) kmlSource).getPath();
		if (kmlSource instanceof String && WWIO.makeURL((String) kmlSource) != null)
			return WWIO.makeURL((String) kmlSource).getPath();
		return "KML Layer";
	}
}
