package geometryFormat;

import java.util.LinkedList;
import geometry.Vector3;

public abstract class CustomCoordTransform
{
	public abstract Vector3 transform( Vector3 in);
	
	public LinkedList<Vector3> transformArray( LinkedList<Vector3> vectors )
	{
		LinkedList<Vector3> transVector3s = new LinkedList<>();
		
		for (Vector3 v : vectors)
		{
			transVector3s.add( transform(v));
		}
		return transVector3s;
	}
}
