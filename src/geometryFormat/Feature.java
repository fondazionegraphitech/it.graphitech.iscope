package geometryFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Feature
{
	//Parent Feature
	public Feature parent;
	
	//IDENTIFIER
	public String id;
	public String metaData;
	
	//GEOMETRY
	public List<Integer> indices;
	public List<Double> vertices;
	public List<Double> referencePoint;
	public List<String> textures;

	//SUBFEATURES
	public List<Feature> features;
	
	public Feature()
	{
		//indices = new LinkedList<>();
		vertices = new ArrayList<>(18);
		referencePoint = new LinkedList<>();
		//textures = new LinkedList<>();
		features = new LinkedList<>();
	}
	
	public String toString()
	{
		String buf = "ID: " + id + " MetaData:" + metaData + "\n";
		if (features != null)
		buf += "Number of SubFeatures: " + features.size() + "\n";
		if (vertices != null)
		buf += "Number of vertices: " + vertices.size() + "\n";
		return buf;
	}
	
}
