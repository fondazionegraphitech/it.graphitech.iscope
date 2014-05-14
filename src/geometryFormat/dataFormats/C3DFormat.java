package geometryFormat.dataFormats;

import geometryFormat.Feature;
import geometryFormat.InternalFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class C3DFormat extends AbstractDataFormat
{

	public DecimalFormat df;
	
	public C3DFormat()
	{
		super();
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
		otherSymbols.setDecimalSeparator('.');
		df = new DecimalFormat( "#.0000000", otherSymbols); 
	}
	
	@Override
	public String getFileExtension()
	{
		return "C3D";
	}

	@Override
	public String getFormatName()
	{
		return "C3D - Compact 3D";
	}

	@Override
	public InternalFormat loadFrom(InputStream is) throws IOException
	{
		Reader reader = new InputStreamReader(is);
		BufferedReader r = new BufferedReader(reader);
		InternalFormat newInternalFormat = null;
		String line = null;
		
		line = r.readLine();
		if (line.startsWith("f "))
		{
			String vertexFormat = line.substring(2);
			newInternalFormat = new InternalFormat(vertexFormat);
		} else
		{
			return null;
		}

		if (newInternalFormat != null)
		{
			newInternalFormat.rootFeature = readFeature(r);
		}

		return newInternalFormat;
	}

	public Feature readFeature(BufferedReader r) throws IOException
	{
		Feature newFeature = null;
		String line = null;
		while ((line = r.readLine()) != null)
		{
			if (newFeature == null)
				newFeature = new Feature();
			if (line.length() == 0) continue;
			String prefix = line.substring(0, 1);
			String suffix = null;
			switch (prefix)
			{
			case "p": // IDENTIFIER OF FEATURE
				suffix = line.substring(2);
				newFeature.id = suffix.trim();
				break;
			case "m": // IDENTIFIER OF FEATURE
				suffix = line.substring(2);
				newFeature.metaData = suffix.trim();
				break;
			case "r": // REFERENCE POINT
				suffix = line.substring(2);
				newFeature.referencePoint.add(Double.parseDouble(suffix.trim()));
				break;
			case "i": // INDEX OF VERTEX
				suffix = line.substring(2);
				newFeature.indices.add(Integer.parseInt(suffix.trim()));
				break;
			case "v": // VERTEX DATA
				suffix = line.substring(2);
				newFeature.vertices.add(Double.parseDouble(suffix.trim()));
				break;
			case "t": // TEXTURES
				suffix = line.substring(2);
				newFeature.textures.add(suffix.trim());
				break;
			case ">": // SUBFEATURE
				Feature subFeature = readFeature(r);
				subFeature.parent = newFeature;
				newFeature.features.add(subFeature);
				break; // PARENTFEATURE
			case "<":
				return newFeature;
			}
		}
		return newFeature;
	}

	@Override
	public void saveTo(InternalFormat internalFormat, OutputStream os) throws IOException
	{
		if (internalFormat.rootFeature == null)
			throw new IOException("Empty Model.");
		// WRITING FORMAT OF VERTEX DATA
		os.write(("f " + internalFormat.vertexFormat + "\n").getBytes());
		// SAVING ROOT FEATURE
		saveFeature(internalFormat.rootFeature, os);
	}
	
	public void saveFeature(Feature feature, OutputStream os) throws IOException
	{
		// WRITING ID OF THIS FEATURE

		os.write((">\n").getBytes());
		os.write(("p " + feature.id + "\n").getBytes());
		os.write(("m " + feature.metaData + "\n").getBytes());
		// WRITING//WRITING REFERENCE POINT
		for (double value : feature.referencePoint)
		{
			os.write(("r " + value + "\n").getBytes());
		}
		// WRITING INDICES
		for (int value : feature.indices)
		{
			os.write(("i " + value + "\n").getBytes());
		}
		// WRITING VERTICES
		for (double value : feature.vertices)
		{
			os.write(("v " + df.format(value) + "\n").getBytes());
		}
		// WRITING TEXTURES
		for (String value : feature.textures)
		{
			os.write(("t " + value + "\n").getBytes());
		}
		// WRITING SUBFEATURE CHAR
		if (feature.features.size() > 0)
		{
			for (Feature subFeature : feature.features)
			{

				saveFeature(subFeature, os);
			}
		}

		os.write(("<\n").getBytes());
		os.write(("\n").getBytes());
	}
}
