package openLS;

import java.util.LinkedList;

public class OpenLSInstruction
{
    //public static String instructionServer = "http://178.188.103.119/iscope/iscope_instruction_service";
	//PARAMETERS "?routeid=100&lang=en&usertype=wheelchair&visualisation=normal";
	private String instruction;
	private long duration;
	private double distance;
	private LinkedList<OpenLSPosition> positions = new LinkedList<OpenLSPosition>();
	
	
	public String getInstruction()
	{
		return instruction;
	}
	public void setInstruction(String instruction)
	{
		this.instruction = instruction;
	}
	public LinkedList<OpenLSPosition> getPositions()
	{
		return positions;
	}
	public void setPositions(LinkedList<OpenLSPosition> positions)
	{
		this.positions = positions;
	}
	public double getDistance()
	{
		return distance;
	}
	public void setDistance(double distance)
	{
		this.distance = distance;
	}
	public long getDuration()
	{
		return duration;
	}
	public void setDuration(long duration)
	{
		this.duration = duration;
	}
	
	
}
