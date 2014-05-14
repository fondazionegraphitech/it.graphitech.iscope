package Tiled3dLayer;

import geometry.Vector3;
import geometryFormat.Feature;
import geometryFormat.InternalFormat;
import geometryFormat.dataFormats.C3DFormat;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import httpConnection.HTTPconnection;
import iScope.Props;
import iScope.WWJApplet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipInputStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL4;

import Pilot.Pilot;

import com.jogamp.common.nio.Buffers;

public class Tile3D implements Renderable
{
	private Box geometryExtent;
	private InternalFormat internalFormat = null;
	private String request;
	private boolean ready = false;
	private boolean geometryLoadedInGPU = false;
	private int[] vbo = new int[4];
	double minx = 180;
	double maxx = -180;
	double miny = 180;
	double maxy = -180;
	double counter = 0;
	Vec4 referencePoint = null;
	int verticesCount = 0;
	private FloatBuffer vertices;
	private FloatBuffer colors;
	private FloatBuffer normals;
	private FloatBuffer solar;
	public int numberOfDraws = 0;
	public static boolean oneTileWasLoaded = false;
	private static ExecutorService executorService = Executors.newFixedThreadPool(1);
	private Pilot pilot;
	private String cacheLocation = Configuration.getSystemTempDirectory() + "";
	public static float memoryOnGPU = 0;

	public Tile3D(String request, Pilot pilot)
	{
		this.request = request;
		this.pilot = pilot;
		executorService.submit(loadTile);
	}

	Runnable loadTile = new Runnable()
	{
		@Override
		public void run()
		{
			retrieveTile();
		}
	};

	private void retrieveTile()
	{
		InputStream tileDataInputStream = null;
		try
		{
			System.out.println("Tile Request : " + request);
			tileDataInputStream = getTileInputStream(request);// HTTPconnection.sendGetRequestInputStream(request);
			// tileDataInputStream = getTileInputStream(request);
		} catch (Exception e)
		{
			return;
		}
		if (tileDataInputStream != null)
		{
			C3DFormat c3dFormat = new C3DFormat();
			try
			{
				ZipInputStream zipInputStream = new ZipInputStream(tileDataInputStream);
				zipInputStream.getNextEntry();
				internalFormat = c3dFormat.loadFrom(zipInputStream);
				zipInputStream.closeEntry();
				zipInputStream.close();
				if (internalFormat != null)
				{
					int verticesCount = 0;
					for (Feature bldg : internalFormat.rootFeature.features.get(0).features)
					{
						for (Feature srfc : bldg.features)
						{
							verticesCount += srfc.vertices.size();
						}
					}
					vertices = Buffers.newDirectFloatBuffer(verticesCount);
					colors = Buffers.newDirectFloatBuffer(verticesCount);
					normals = Buffers.newDirectFloatBuffer(verticesCount);
					solar = Buffers.newDirectFloatBuffer(verticesCount / 3);
					double[] triangle = new double[9];
					for (Feature bldg : internalFormat.rootFeature.features.get(0).features)
					{
						for (Feature srfc : bldg.features)
						{
							int[] c = PickSupport.getUniqueColor(srfc);
							int triangleCoordinateCount = 0;
							for (Double d : srfc.vertices)
							{
								triangle[triangleCoordinateCount] = d;
								triangleCoordinateCount++;
								if (triangleCoordinateCount == 9)
								{
									triangleCoordinateCount = 0;
									Position p1 = Position.fromDegrees(triangle[1] + pilot.dataSet3dOffSet.y, triangle[0] + pilot.dataSet3dOffSet.x, triangle[2] + pilot.dataSet3dOffSet.z);
									Position p2 = Position.fromDegrees(triangle[4] + pilot.dataSet3dOffSet.y, triangle[3] + pilot.dataSet3dOffSet.x, triangle[5] + pilot.dataSet3dOffSet.z);
									Position p3 = Position.fromDegrees(triangle[7] + pilot.dataSet3dOffSet.y, triangle[6] + pilot.dataSet3dOffSet.x, triangle[8] + pilot.dataSet3dOffSet.z);
									// Position p1 = Position.fromDegrees(triangle[1] , triangle[0], triangle[2]);
									// Position p2 = Position.fromDegrees(triangle[1] , triangle[0], triangle[2]);
									// Position p3 = Position.fromDegrees(triangle[1] , triangle[0], triangle[2]);
									Vec4 v1 = WWJApplet.getWWD().getModel().getGlobe().computePointFromPosition(p1);
									Vec4 v2 = WWJApplet.getWWD().getModel().getGlobe().computePointFromPosition(p2);
									Vec4 v3 = WWJApplet.getWWD().getModel().getGlobe().computePointFromPosition(p3);
									if (referencePoint == null)
										referencePoint = v1;
									if (p1.getLongitude().getDegrees() < minx)
										minx = p1.getLongitude().getDegrees();
									if (p1.getLongitude().getDegrees() > maxx)
										maxx = p1.getLongitude().getDegrees();
									if (p1.getLatitude().getDegrees() < miny)
										miny = p1.getLatitude().getDegrees();
									if (p1.getLatitude().getDegrees() > maxy)
										maxy = p1.getLatitude().getDegrees();
									vertices.put((float) (v1.x - referencePoint.x));
									vertices.put((float) (v1.y - referencePoint.y));
									vertices.put((float) (v1.z - referencePoint.z));
									vertices.put((float) (v2.x - referencePoint.x));
									vertices.put((float) (v2.y - referencePoint.y));
									vertices.put((float) (v2.z - referencePoint.z));
									vertices.put((float) (v3.x - referencePoint.x));
									vertices.put((float) (v3.y - referencePoint.y));
									vertices.put((float) (v3.z - referencePoint.z));
									colors.put(c[1] / 255f);
									colors.put(c[2] / 255f);
									colors.put(c[3] / 255f);
									colors.put(c[1] / 255f);
									colors.put(c[2] / 255f);
									colors.put(c[3] / 255f);
									colors.put(c[1] / 255f);
									colors.put(c[2] / 255f);
									colors.put(c[3] / 255f);
									Vector3 normal = Vector3.GetNormal(new Vector3(v1.x, v1.y, v1.z), new Vector3(v2.x, v2.y, v2.z), new Vector3(v3.x, v3.y, v3.z));
									normals.put((float) normal.x);
									normals.put((float) normal.y);
									normals.put((float) normal.z);
									normals.put((float) normal.x);
									normals.put((float) normal.y);
									normals.put((float) normal.z);
									normals.put((float) normal.x);
									normals.put((float) normal.y);
									normals.put((float) normal.z);
								}
							}
							srfc.vertices.clear();
							srfc.vertices = null;
						}
					}
					geometryExtent = Sector.computeBoundingBox(WWJApplet.getWWD().getModel().getGlobe(), 1, Sector.fromDegrees(miny, maxy, minx, maxx));
				}
			} catch (IOException e)
			{
				System.err.println("Error preparing Tile. Unloading Data");
				return;
			}
		}
		vertices.flip();
		colors.flip();
		normals.flip();
		solar.flip();
		if (vertices.limit() > 0)
		{
			ready = true;
			System.out.println("Tile coming from request " + request + " Prepared");
		} else
			System.err.println("Error with tile coming from reqest " + request);
	}

	private InputStream getTileInputStream(String request) throws IOException
	{
		String requestHash = "" + request.hashCode();
		String cacheFileLocation = cacheLocation + pilot.getName() + "_" + requestHash + ".cache";
		System.out.println("Cache File: " + cacheFileLocation);
		File cacheFile = new File(cacheFileLocation);
		if (cacheFile.exists() && Props.getBool("3DtileCacheEnabled"))
		{
			FileInputStream cacheFileInputStream = new FileInputStream(cacheFile);
			return cacheFileInputStream;
		} else
		{
			InputStream is = HTTPconnection.sendGetRequestInputStream(request);
			FileOutputStream fileOutputStream = new FileOutputStream(new File(cacheFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024 * 1024];
			while ((read = is.read(bytes)) > -1)
			{
				fileOutputStream.write(bytes, 0, read);
			}
			fileOutputStream.close();
			FileInputStream cacheFileInputStream = new FileInputStream(cacheFile);
			return cacheFileInputStream;
		}
	}

	long startingTime;
	long timeCounter;

	@Override
	public void render(DrawContext dc)
	{
		if (!ready)
			return;
		if (!geometryExtent.intersects(dc.getView().getFrustumInModelCoordinates()))
			return;
		if (!geometryLoadedInGPU)
		{
			if (!oneTileWasLoaded)
			{
				oneTileWasLoaded = true;
				loadGeometryInGPU(dc);
				startingTime = System.currentTimeMillis();
				return;
			}
			if (!(vbo[0] != 0 && vbo[1] != 0 && vbo[2] != 0))
			{
				return;
			}
		}
		timeCounter = System.currentTimeMillis() - startingTime;
		int sdr = Tiled3DLayerRenderer.colorShader;
		GL4 gl = (GL4) dc.getGL();
		int solarEnabled = gl.glGetUniformLocation(sdr, "solarEnabled");
		gl.glUniform1f(solarEnabled, 1f);
		// timeSinceTileCreation
		int timeSinceTileCreation = gl.glGetUniformLocation(sdr, "timeSinceTileCreation");
		gl.glUniform1f(timeSinceTileCreation, timeCounter / 1000f);
		int referencePoint = gl.glGetUniformLocation(sdr, "referencePoint");
		gl.glUniform3f(referencePoint, (float) this.referencePoint.x, (float) this.referencePoint.y, (float) this.referencePoint.z);
		// PASS SELECTED SURFACE
		int selectedColor = gl.glGetUniformLocation(sdr, "selectedColor1");
		gl.glUniform3f(selectedColor, Tiled3DLayerRenderer.selectColor1[1] / 255f, Tiled3DLayerRenderer.selectColor1[2] / 255f, Tiled3DLayerRenderer.selectColor1[3] / 255f);
		// PASS SELECTED SURFACE
		int selectedColor2 = gl.glGetUniformLocation(sdr, "selectedColor2");
		gl.glUniform3f(selectedColor2, Tiled3DLayerRenderer.selectColor2[1] / 255f, Tiled3DLayerRenderer.selectColor2[2] / 255f, Tiled3DLayerRenderer.selectColor2[3] / 255f);
		int modelPositionVertexAttribArray = gl.glGetAttribLocation(sdr, "modelPosition");
		int colorVertexAttribArray = gl.glGetAttribLocation(sdr, "color");
		int normalVertexAttribArray = gl.glGetAttribLocation(sdr, "normal");
		// int solarVertexAttribArray = gl.glGetAttribLocation(sdr, "solar");
		gl.glEnableVertexAttribArray(modelPositionVertexAttribArray);
		gl.glEnableVertexAttribArray(colorVertexAttribArray);
		gl.glEnableVertexAttribArray(normalVertexAttribArray);
		// gl.glEnableVertexAttribArray(solarVertexAttribArray);
		gl.glEnable(GL.GL_CULL_FACE);
		// LOADING VERTICES
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]); // Bind GL_ARRAY_BUFFER to our handle
		gl.glVertexAttribPointer(modelPositionVertexAttribArray, 3, GL4.GL_FLOAT, false, 0, 0);
		// LOADING COLORS
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(colorVertexAttribArray, 3, GL4.GL_FLOAT, false, 0, 0);
		// LOADING NORMALS
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(normalVertexAttribArray, 3, GL4.GL_FLOAT, false, 0, 0);
		// LOADING SOLAR VALUE
		// gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
		// gl.glVertexAttribPointer(solarVertexAttribArray, 1, GL4.GL_FLOAT, false, 0, 0);
		// RENDERING
		WWJApplet.getWWD().getView().pushReferenceCenter(dc, this.referencePoint);
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, numberOfDraws);
		WWJApplet.getWWD().getView().popReferenceCenter(dc);
		gl.glDisableVertexAttribArray(modelPositionVertexAttribArray); // ?
		gl.glDisableVertexAttribArray(colorVertexAttribArray);
		gl.glDisableVertexAttribArray(normalVertexAttribArray);
		// gl.glDisableVertexAttribArray(solarVertexAttribArray);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0); // Unbind
	}

	private void loadGeometryInGPU(DrawContext dc)
	{
		numberOfDraws = vertices.limit() / 3;
		GL2 gl = (GL2) dc.getGL();
		gl.glGenBuffers(3, vbo, 0);
		// BINDING VERTICES
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.limit() * 4, vertices, GL2.GL_STATIC_DRAW); // EACH VERICES CONTAINS 6 DOUBLES so 6
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[1]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, colors.limit() * 4, colors, GL2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[2]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, normals.limit() * 4, normals, GL2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[3]);
		// gl.glBufferData(GL2.GL_ARRAY_BUFFER, solar.limit() * 4, solar, GL2.GL_STATIC_DRAW);
		// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		float geometryMB = (float) (vertices.limit() * 4) / (float) (1024 * 1024);
		float colorsMB = (float) (colors.limit() * 4) / (float) (1024 * 1024);
		float normalsMB = (float) (normals.limit() * 4) / (float) (1024 * 1024);
		// float solarMB = (float) (solar.limit() * 4) / (float) (1024 * 1024);
		float tileGPUMemorySize = geometryMB + colorsMB + normalsMB;
		memoryOnGPU = memoryOnGPU + tileGPUMemorySize;
		System.out.println("Tile GPU Memory: " + tileGPUMemorySize + "MB, total: " + memoryOnGPU + "MB");
		try
		{
			destroyBuffer(vertices);
			destroyBuffer(colors);
			destroyBuffer(normals);
			// destroyBuffer(solar);
		} catch (Exception e)
		{
			System.err.println("Error in deallocating CPU to GPU Buffers");
		}
		geometryLoadedInGPU = true;
	}

	public void destroyBuffer(Buffer buffer) throws Exception
	{
		if (buffer.isDirect())
		{
			try
			{
				if (!buffer.getClass().getName().equals("java.nio.DirectByteBuffer"))
				{
					Field attField = buffer.getClass().getDeclaredField("att");
					attField.setAccessible(true);
					buffer = (Buffer) attField.get(buffer);
				}
				Method cleanerMethod = buffer.getClass().getMethod("cleaner");
				cleanerMethod.setAccessible(true);
				Object cleaner = cleanerMethod.invoke(buffer);
				Method cleanMethod = cleaner.getClass().getMethod("clean");
				cleanMethod.setAccessible(true);
				cleanMethod.invoke(cleaner);
			} catch (Exception e)
			{
				throw new Exception("Could not destroy direct buffer " + buffer, e);
			}
		}
	}

	public void destroyVBO(DrawContext dc)
	{
		dc.getGL().glDeleteBuffers(3, vbo, 0);
		geometryExtent = null;
		internalFormat = null;
		vbo = null;
		referencePoint = null;
		vertices=null;
		colors=null;
		normals=null;
		solar=null;
		//executorService;
		//executorService = null;
		pilot = null;
	}

}
