package iScope;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.util.StatusBar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import noise.NoiseManager;
import openLS.OpenLSManager;
import picking.RoutingPickingManager;
import DebugConsole.DebugConsoleManager;
import Kml.KMLManager;
import Pilot.PilotManager;

public class WWJApplet extends JApplet
{
	private static final long serialVersionUID = 7149111932500752611L;
	private static WWJApplet applet;
	private static WorldWindowGLCanvas wwd;
	public static GUILayer GUI;
	public static AnnotationLayer annotationLayer;
	public static boolean QUERYBLDWFS;
	private static PilotManager pilotManager;
	public static DebugConsoleManager debugConsoleManger;
	public static boolean enablePickHighLight = false;
	public static boolean retriveWfsSolarInfos = false; // letto in tile3dlayerrenderer per valutare se fare o meno il picking e chiamre il wfs
	public static boolean retriveWfsBldInfos = false; // letto in tile3dlayerrenderer per valutare se fare o meno il picking e chiamre il wfs

	public static PilotManager getPilotManager()
	{
		return pilotManager;
	}

	public static boolean DEBUG = false;
	public static JS jS;
	public LayerList activeLayers = new LayerList();
	public static RoutingPickingManager routingPickingManager;
	public OpenLSManager openLSManager;
	public NoiseManager noiseManager;
	public KMLManager kmlManager;
	private boolean fullscreen = false;
	private Dimension size;

	@SuppressWarnings("static-access")
	public void init()
	{
		System.out.println("V0.9 build");
		new Props();
		String build = Props.getStr("build.number");
		if (build != null)
		{
			System.out.print(build);
		}
		// Create World Window GL Canvas
		// Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());
		// Configuration.setValue(AVKey.VIEW_CLASS_NAME, FlatOrbitView.class.getName());
		wwd = new WorldWindowGLCanvas();
		this.getContentPane().add(wwd, BorderLayout.CENTER);
		// setFullScreen();
		/*
		 * JFrame flyingWindow= new JFrame(); flyingWindow.setAlwaysOnTop(true); flyingWindow.setVisible(true); flyingWindow.add(wwd, BorderLayout.CENTER);
		 */
		wwd.setModel(new BasicModel());
		this.applet = this;
		// Shows the warning telling that WW is downloading something
		GUI = new GUILayer();
		GUI.setEnabled(true);
		GUI.setName("GUI Layer");
		wwd.getModel().getLayers().add(GUI);
		annotationLayer = new AnnotationLayer();
		wwd.getModel().getLayers().add(annotationLayer);
		jS = new JS();
		// /////////NON FARE NULLA PRIMA DI QUESTO PUNTO////////
		pilotManager = new PilotManager();
		StatusBar statusBar = new StatusBar();
		statusBar.setEventSource(wwd);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		IscopeSelectController selectController = new IscopeSelectController(wwd, SelectEvent.ROLLOVER);
		IscopeMouseListerner iscopeMouseListerner = new IscopeMouseListerner(selectController);
		wwd.addSelectListener(selectController);
		wwd.addMouseListener(iscopeMouseListerner);
		DEBUG = Props.getBool("debugMode");
		QUERYBLDWFS = Props.getBool("QueryBldWfs");
		System.setSecurityManager(null);
		if (Props.getBool("showMemoryInfo"))
		{
			showMemoryInfo();
		}
		if (Props.getBool("usingTouchPad"))
		{
			ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
			viewControlsLayer.setLayout(AVKey.HORIZONTAL);
			viewControlsLayer.setShowVeControls(false);
			viewControlsLayer.setName("ViewControlsLayer");
			wwd.getModel().getLayers().add(viewControlsLayer);
			wwd.addSelectListener(new ViewControlsSelectListener(wwd, viewControlsLayer));
		}
		// Loading Noise Data
		if (Props.getBool("NoiseEnabled"))
		{
			noiseManager = new NoiseManager();
		}
		// OPENLS ROUTING INTERFACE INITIALIZATION
		if (Props.getBool("enableRouting"))
		{
			routingPickingManager = new RoutingPickingManager();
			openLSManager = new OpenLSManager();
		}
		// MOVING TO INITIAL POSITION WHEN APPLET STARTS
		if (Props.getBool("moveToInitialPosition"))
		{
			wwd.getView().setEyePosition(new Position(Angle.fromDegrees(Props.getDou("initialLat")), Angle.fromDegrees(Props.getDou("initialLon")), Props.getDou("initialEle")));
		}
		// ADD DEBUG CONSOLE
		if (Props.getBool("enableDebugConsole"))
		{
			debugConsoleManger = new DebugConsoleManager(this);
		}
		// RENDER CONTINUOSLY?
		wwd.addRenderingListener(new RenderingListener()
		{
			public void stageChanged(RenderingEvent event)
			{
				if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && event.getSource() instanceof WorldWindow)
				{
					((WorldWindow) event.getSource()).redraw();
				}
			}
		});
	}

	private void showMemoryInfo()
	{
		long maxHeapSize = Runtime.getRuntime().maxMemory();
		long freeHeapSize = Runtime.getRuntime().freeMemory();
		double totalHeapSize = Runtime.getRuntime().totalMemory();
		System.out.println("Max Heap Size = " + String.valueOf(maxHeapSize / (1024l * 1024l)) + " Mbyte");
		System.out.println("Free Heap Size = " + freeHeapSize / (1024d * 1024d) + " Mbyte");
		System.out.println("Total Heap Size = " + totalHeapSize / (1024d * 1024d) + " Mbyte");
		System.out.println(System.getProperty("java.version"));
		System.out.println(System.getProperty("sun.arch.data.model"));
	}

	public void start()
	{
		jS.call("appletStarted", null);
	}

	public void stop()
	{
	}

	public static WWJApplet getApplet()
	{
		return applet;
	}

	public static WorldWindowGLCanvas getWWD()
	{
		return wwd;
	}

	public static JS getJS()
	{
		return jS;
	}

	public void setFullScreen()
	{
		if (!this.fullscreen)
		{
			frame = new JFrame(this.getGraphicsConfiguration());
			//frame.setUndecorated(true);
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			// frame.setUndecorated(true);
			this.remove(wwd);
			frame.setVisible(true);
			frame.setSize(this.getWidth(), this.getHeight());
			frame.add(wwd, BorderLayout.CENTER);
			// frame.setUndecorated(false);
			// size = this.getSize();
			// if (this.parent == null)
			// {
			// this.parent = getParent();
			// }
			// this.frame = new JFrame();

			// this.frame.add(this);
			// this.frame.setVisible(true);
			// GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			// GraphicsDevice[] devices = ge.getScreenDevices();
			// devices[0].setFullScreenWindow(this.frame);
			this.fullscreen = true;
			this.revalidate();
		} else
		{
			frame.remove(wwd);
			frame.dispose();
			this.getContentPane().add(wwd, BorderLayout.CENTER);
			this.fullscreen = false;
			this.setSize(size);
			this.revalidate();
			this.fullscreen = false;
		}
		//Tiled3DLayerRenderer.reloadShader = true;
		// this.requestFocus();
	}

	private Frame frame = new Frame(this.getGraphicsConfiguration());
	private Container parent = getParent();

	public void setFullScreen2()
	{
		System.out.println("SWITCH");
		if (!fullscreen)
		{
			if (this.parent == null)
				this.parent = getParent();
			frame.setUndecorated(true);
			frame.add(this);
			frame.setVisible(true);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = ge.getScreenDevices();
			devices[0].setFullScreenWindow(frame);
			fullscreen = true;
		} else
		{
			parent.add(this);
			frame.dispose();
			fullscreen = false;
		}
		//Tiled3DLayerRenderer.reloadShader = true;
		this.requestFocus();
	}
}
