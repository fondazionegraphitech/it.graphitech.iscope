package noise;

import gov.nasa.worldwind.geom.Position;

public class NoisePoint
{
	private Position position;
	private int deciBel;
	private String aquisitionTime;
	private String user;

	public NoisePoint()
	{
	}
	
	public NoisePoint(Position position, int dB)
	{
		this.position = position;
		this.deciBel = dB;
		this.aquisitionTime = "no data";
		this.user = "no data";
	}

	public NoisePoint(Position position, int deciBel, String acquisitionTime, String user)
	{
		this.position = position;
		this.deciBel = deciBel;
		this.aquisitionTime = acquisitionTime;
		this.user = user;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	public int getDeciBel()
	{
		return deciBel;
	}

	public void setDeciBel(int deciBel)
	{
		this.deciBel = deciBel;
	}

	public String getAquisitionTime()
	{
		return aquisitionTime;
	}

	public void setAquisitionTime(String aquisitionTime)
	{
		this.aquisitionTime = aquisitionTime;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}
}
