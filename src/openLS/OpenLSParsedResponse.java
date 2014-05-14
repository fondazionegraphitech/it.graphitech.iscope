package openLS;

import java.util.LinkedList;

public class OpenLSParsedResponse
{
	public String responseID;
	public String requestID;
	/**
	 * Total route, containing all position to reach destination
	 */
	private LinkedList<OpenLSPosition> positions = new LinkedList<OpenLSPosition>(); 
	/**
	 * List of sub components of the route containing small pieces of routes with relative instructions.
	 */
	private LinkedList<OpenLSInstruction> Instructions = new LinkedList<OpenLSInstruction>();
	
	private OpenLSPosition centralPosition;

	public OpenLSPosition getCentralPosition()
	{
		return centralPosition;
	}

	public void setCentralPosition(OpenLSPosition centralPosition)
	{
		this.centralPosition = centralPosition;
	}

	public LinkedList<OpenLSPosition> getPositions()
	{
		return positions;
	}

	public LinkedList<OpenLSInstruction> getInstructions()
	{
		return Instructions;
	}

}
