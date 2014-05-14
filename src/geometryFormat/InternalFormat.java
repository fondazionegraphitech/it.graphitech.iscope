package geometryFormat;

import geometry.Vector3;

import java.util.LinkedList;

public class InternalFormat
{
	public Feature rootFeature;
	/**
	 * VERTEXFORMAT character represent a float if lowercase and a double if capital<br>
	 * v = vertex | n = normal | t = texture coordinate | o = other
	 */
	public String vertexFormat = "vvvnnntt";

	public InternalFormat(String vertexFormat)
	{
		this.vertexFormat = vertexFormat;
	}

	public InternalFormat()
	{
	}

	public int getVertexSize()
	{
		return vertexFormat.length();
	}

	public static LinkedList<Vector3> getVertexList(LinkedList<Vector3> buffer, Feature f)
	{
		LinkedList<Vector3> vectors = new LinkedList<>();
		return addVertexList(vectors, f, null);
	}

	public static LinkedList<Vector3> addVertexList(LinkedList<Vector3> buffer, Feature f, CustomCoordTransform customCoordTransform)
	{
		if (buffer == null) throw new NullPointerException("Buffer must be an instance of LinkedList<Vector3>");
		if (customCoordTransform == null)
		{
			buffer.addAll(getVectorsFromVertices3(f));
			for (Feature subFeature : f.features)
			{
				addVertexList(buffer, subFeature, null);
			}
		} else
		{
			buffer.addAll(customCoordTransform.transformArray(getVectorsFromVertices3(f)));
			for (Feature subFeature : f.features)
			{
				addVertexList(buffer, subFeature, customCoordTransform);
			}
		}
		return buffer;
	}
	
	public static LinkedList<Vector3> getVectorsFromVertices3( Feature f )
	{
		LinkedList<Vector3> vector3s = new LinkedList<>();
		int count = 0;
		Vector3 newvector = null;
		for (Double d : f.vertices)
		{
			count ++;
			switch (count)
			{
			case  1:
				newvector = new Vector3();
				newvector.x = d;
			break;
			case  2:
				newvector.y = d;
			break;
			case  3:
				newvector.z = d;
				vector3s.add(newvector);
				count = 0;
			break;
			}
		}
		if (vector3s.size() % 3 > 0 ) System.err.println("NOT DIVISIBLE TRIANGEL FEATURE:" + f.id );
		return vector3s;
		
	}
}
