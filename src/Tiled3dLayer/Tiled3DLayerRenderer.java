package Tiled3dLayer;

import geometryFormat.Feature;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import iScope.Props;
import iScope.WWJApplet;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL4;

import wfs.WfsManager;
import Pilot.Pilot;
import Pilot.PilotManager;

public class Tiled3DLayerRenderer extends RenderableLayer
{
	RetrieverInterface layerRetriever;
	public static boolean is3DActive = true;
	Pilot pilot;
	public static boolean reloadShader = false;
	public static int colorShader = -1;
	public static boolean shaderInitialized = false;
	private boolean mouseClicked;
	public static int[] selectColor1 =
	{ 0, 0, 0, 0 };
	public static int[] selectColor2 =
	{ 0, 0, 0, 0 };
	public Feature lastFeature = null;
	public Feature lastClickedFeature = null;
	private WfsManager wfsManager;
	MouseAdapter mouseAdapter;
	public Tiled3DLayerRenderer(Pilot pilot)
	{
		this.pilot = pilot;
		Tile3D.oneTileWasLoaded = false;
		this.setName(pilot.getName() + "Renderer");
		this.setEnabled(true);
		layerRetriever = Fixed3dTileListRetriever.fromPilot(this, pilot);
		WWJApplet.getWWD().getModel().getLayers().add(0, this);
		
		mouseAdapter = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent mouseEvent)
			{
				mouseClicked = true;
				mouseEvent.consume();
			}
		};
		WWJApplet.getWWD().getInputHandler().addMouseListener(mouseAdapter);
		wfsManager = new WfsManager();
	}

	@Override
	protected void doRender(DrawContext dc)
	{
		
		if (layerRetriever == null || layerRetriever.getTiles() == null)
			return;
		if (destroyed)
		{
			destroyTiles(dc);
			return;
		}
		if (!is3DActive)
			return;
		GL2 gl = (GL2) dc.getGL();
		if (colorShader < 0)
		{
			colorShader = LoadShader((GL4) dc.getGL(), "shaders/shader");
			return;
		}
		if (!shaderInitialized)
		{
			gl.glUseProgram(colorShader);
			List<String> ambient = Props.getStrList("3DambientColor");
			List<String> diffuse = Props.getStrList("3DdiffuseColor");
			List<String> highLight = Props.getStrList("3DhighLightColor");
			int ambientColor = gl.glGetUniformLocation(colorShader, "ambientColor");
			gl.glUniform4f(ambientColor, Float.parseFloat(ambient.get(0)), Float.parseFloat(ambient.get(1)), Float.parseFloat(ambient.get(2)), 1);
			int diffuseColor = gl.glGetUniformLocation(colorShader, "diffuseColor");
			gl.glUniform4f(diffuseColor, Float.parseFloat(diffuse.get(0)), Float.parseFloat(diffuse.get(1)), Float.parseFloat(diffuse.get(2)), 1);
			int highLightColor1 = gl.glGetUniformLocation(colorShader, "highLightColor1");
			gl.glUniform4f(highLightColor1, Float.parseFloat(highLight.get(0)), Float.parseFloat(highLight.get(1)), Float.parseFloat(highLight.get(2)), 1);
			int highLightColor2 = gl.glGetUniformLocation(colorShader, "highLightColor2");
			// PASS CURRENT CAMERA POSITION TO SHADER
			Vec4 currentCameraPosition = WWJApplet.getWWD().getView().getEyePoint();
			int currentCameraPositionID = gl.glGetUniformLocation(colorShader, "currentCameraPosition");
			gl.glUniform3f(currentCameraPositionID, (float) currentCameraPosition.x, (float) currentCameraPosition.y, (float) currentCameraPosition.z);
			gl.glUniform4f(highLightColor2, 0.2f, 0.8f, 0.2f, 1f);
			shaderInitialized = true;
		}
		if (!dc.isPickingMode())
			Tile3D.oneTileWasLoaded = false;
		gl.glUseProgram(colorShader);
		int isPicking = gl.glGetUniformLocation(colorShader, "isPicking");
		gl.glUniform1f(isPicking, -1f);
		for (Tile3D tile3d : layerRetriever.getTiles())
		{
			tile3d.render(dc);
		}
		gl.glUseProgram(0);
	}

	@Override
	protected void doPick(DrawContext dc, Point arg1)
	{

		if (PilotManager.currentPilot != null && WWJApplet.getApplet().activeLayers.contains(PilotManager.currentPilot.getnFproduct().getTileLayer()))
			return;
		if (layerRetriever.getTiles() == null)
			return;
		if (destroyed)
		{
			destroyTiles(dc);
			return;
		}
		if (!is3DActive)
			return;
		GL2 gl = (GL2) dc.getGL();
		gl.glUseProgram(colorShader);
		int isPicking = gl.glGetUniformLocation(colorShader, "isPicking");
		gl.glUniform1f(isPicking, 1f);
		for (Tile3D tile3d : layerRetriever.getTiles())
		{
			tile3d.render(dc);
		}
		gl.glUseProgram(0);
		dc.getGL().glFlush();
		int pickColor = dc.getPickColorAtPoint(arg1);
		Feature f = (Feature) PickSupport.doPickWithWWDColor(pickColor);
		doFireFeatureHover(f, PickSupport.getARGBFromInt(pickColor));
		mouseClicked = false;
	}

	private void doFireFeatureHover(Feature feature, int[] pickColor)
	{
		if (feature != lastFeature)
			doFireFeatureHoverChanged(feature, pickColor);
		if (mouseClicked)
			doFireFeatureClicked(feature, pickColor);
		if (feature != null)
			selectColor1 = pickColor;
		else
		{
			selectColor1 = new int[]
			{ 0, 0, 0, 0 };
		}
		// if(feature == null) wfsManager.removeAllAnnotations(); //rimuove la screen annotation
	}

	boolean firstIn = true;
	long begintime;
	long timeout = 2000;
	private boolean destroyed = false;

	private void doFireFeatureHoverChanged(Feature feature, int[] pickColor)
	{

	}

	private void doFireFeatureClicked(Feature feature, int[] pickColor)
	{
		if (lastClickedFeature != feature)
			doFireFeatureClickedChanged(feature, pickColor);
		if (feature != null)
		{
			System.out.println("Feature Clicked: " + feature);
			System.out.println("Parent feature Clicked: " + feature.parent.id);
		}
		if (WWJApplet.enablePickHighLight)
		{
			if (feature != null)
			{
				WWJApplet.getJS().call("getBuildingID", new String[]
				{ feature.parent.id, feature.id });
				selectColor2 = pickColor;
				WWJApplet.enablePickHighLight = false;
			}
		}
		if (feature != null && WWJApplet.retriveWfsSolarInfos)
		{
			wfsManager.removeAllAnnotations();
			selectColor2 = pickColor;
			System.out.println("Sending WFS solar requests");
			Runnable solarThread = wfsManager.new wfsSolarRequestThread(feature);
			new Thread(solarThread).start();
		}
		if (feature != null && WWJApplet.retriveWfsBldInfos)
		{
			wfsManager.removeAllAnnotations();
			selectColor2 = pickColor;
			System.out.println("Sending WFS bldInfo requests");
			Runnable roofThread = wfsManager.new wfsRoofAttRequestThread(feature);
			new Thread(roofThread).start();
		}
		if (feature == null)
		{
			wfsManager.removeAllAnnotations(); // rimuove la screen annotation
			selectColor2 = new int[]
			{ 0, 0, 0, 0 };
		}
	}

	private void doFireFeatureClickedChanged(Feature feature, int[] pickColor)
	{
		lastClickedFeature = feature;
		System.out.println("ClickChanged");
	}

	public int LoadShader(GL4 gl, String shaderName)
	{
		String shaderLocation = Props.getStr("shadersLocation");
		int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		int shaderprogram = -1;
		boolean bothCompiled = true;
		;
		try
		{
			InputStream inputStream = new URL(shaderLocation + ".vs").openStream();
			BufferedReader brv = new BufferedReader(new InputStreamReader(inputStream));
			String vsrc = "";
			String line;
			while ((line = brv.readLine()) != null)
				vsrc += line + "\n";
			String[] vShaderSource =
			{ vsrc };
			gl.glShaderSource(v, 1, vShaderSource, null);
			gl.glCompileShader(v);
			// CHECK COMPIALTION ERRORS
			int[] compiled = new int[1];
			gl.glGetShaderiv(v, GL4.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == GL4.GL_FALSE)
			{
				int[] logLength = new int[1];
				gl.glGetShaderiv(v, GL4.GL_INFO_LOG_LENGTH, logLength, 0);
				byte[] log = new byte[logLength[0]];
				gl.glGetShaderInfoLog(v, logLength[0], (int[]) null, 0, log, 0);
				System.err.println("Error compiling the Vertex shader: " + new String(log));
				gl.glDeleteShader(v);
				bothCompiled = false;
			}
			// LOADING FRAGMENT SHADER
			InputStream inputStream2 = new URL(shaderLocation + ".fs").openStream();
			BufferedReader brf = new BufferedReader(new InputStreamReader(inputStream2));
			String fsrc = "";
			while ((line = brf.readLine()) != null)
				fsrc += line + "\n";
			String[] fShaderSource =
			{ fsrc };
			gl.glShaderSource(f, 1, fShaderSource, null);
			gl.glCompileShader(f);
			// CHECK COMPIALTION ERRORS
			compiled = new int[1];
			gl.glGetShaderiv(f, GL4.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == GL4.GL_FALSE)
			{
				int[] logLength = new int[1];
				gl.glGetShaderiv(f, GL4.GL_INFO_LOG_LENGTH, logLength, 0);
				byte[] log = new byte[logLength[0]];
				gl.glGetShaderInfoLog(f, logLength[0], (int[]) null, 0, log, 0);
				System.err.println("Error compiling the Fragment shader: " + new String(log));
				gl.glDeleteShader(f);
				bothCompiled = false;
			}
			// CREATING PROGRAM, if both vertex and ragment shaders compiled
			if (bothCompiled)
			{
				shaderprogram = gl.glCreateProgram();
				gl.glAttachShader(shaderprogram, v);
				gl.glAttachShader(shaderprogram, f);
				gl.glLinkProgram(shaderprogram);
				gl.glValidateProgram(shaderprogram);
			} else
			{
				if (Props.getBool("debugMode"))
					System.out.println("Program could not be created.");
			}
		} catch (FileNotFoundException e)
		{
			if (Props.getBool("debugMode"))
				System.out.println("Error Opening Shader File");
			e.printStackTrace();
		} catch (IOException e)
		{
			if (Props.getBool("debugMode"))
				System.out.println("Error Reading Shader File");
			e.printStackTrace();
		}
		return shaderprogram;
	}

	public void unload()
	{
		destroyed = true;
	}

	private void destroyTiles(DrawContext dc)
	{
		for (Tile3D tile3d : layerRetriever.getTiles())
		{
			tile3d.destroyVBO(dc);
		}
		layerRetriever.destroy();
		layerRetriever.getTiles().clear();
		layerRetriever = null;

		WWJApplet.getWWD().getModel().getLayers().remove(this);
		WWJApplet.getWWD().getInputHandler().removeMouseListener(mouseAdapter);
		colorShader = -1;
		shaderInitialized = false;
	}
}
