package openLS;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenLSResponseParser
{
	public OpenLSParsedResponse parseString(String openLSResponse) throws ParserConfigurationException, SAXException, IOException, BadOpenLSResponseException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		ByteArrayInputStream byteArray = new ByteArrayInputStream(openLSResponse.getBytes("utf-8"));
		InputSource inputSource = new InputSource(byteArray);
		Document doc = dBuilder.parse(inputSource);
		// PARSING PATH COORDINATES
		NodeList routeGeometrys = doc.getElementsByTagName("xls:RouteGeometry");
		if (routeGeometrys == null)
			throw new BadOpenLSResponseException(openLSResponse);
		Node routeGeometry = routeGeometrys.item(0);
		if (routeGeometry == null)
			throw new BadOpenLSResponseException(openLSResponse);
		String coords = routeGeometry.getTextContent();
		String[] splitStr = coords.split("\\s+");
		OpenLSParsedResponse response = new OpenLSParsedResponse();
		for (int i = 0; i < splitStr.length; i = i + 2)
		{
			if (splitStr[i].length() == 0)
				i++;
			Double lon = Double.parseDouble(splitStr[i]);
			Double lat = Double.parseDouble(splitStr[i + 1]);
			response.getPositions().add(OpenLSPosition.fromDegrees(lat, lon, 0));
			
		}
		Element xlsResponse = (Element) (doc.getElementsByTagName("xls:Response").item(0));
		response.requestID = xlsResponse.getAttribute("requestID");
		response.responseID = xlsResponse.getAttribute("responseID");
     
		return response;
	}
}
