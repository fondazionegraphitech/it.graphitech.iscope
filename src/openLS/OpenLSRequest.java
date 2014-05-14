package openLS;

import iScope.WWJApplet;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class OpenLSRequest
{
	private String OLSserver;

	public OpenLSRequest(String server)
	{
		OLSserver = server;
	}

	public OpenLSParsedResponse doRequest(OpenLSRequestPreferences openLSRequestPreferences)
	{
		if (openLSRequestPreferences.positions.size() < 2)
		{
			System.err.println("No Destination and position defined, exiting.");
			return null;
		}
		try
		{
			String openLSRequestString = this.getStringRequest(openLSRequestPreferences);
			if (WWJApplet.DEBUG)
				System.out.println("Sending Request...");
			if (WWJApplet.DEBUG)
				System.out.println("-- REQUEST --");
			if (WWJApplet.DEBUG)
				System.out.println(openLSRequestString);
			if (WWJApplet.DEBUG)
				System.out.println("--   END   --");
			String openLSResponse = httpConnection.httpPost.sendExportPostrequest(OLSserver, openLSRequestString);
			if (WWJApplet.DEBUG)
				System.out.println("Parsing Response...");
			OpenLSResponseParser parser = new OpenLSResponseParser();
			if (WWJApplet.DEBUG)
				System.out.println("-- RESPONSE --");
			if (WWJApplet.DEBUG)
				System.out.println(openLSResponse);
			if (WWJApplet.DEBUG)
				System.out.println("--   END   --");
			return parser.parseString(openLSResponse);
		} catch (IOException e)
		{
			System.err.println("IoException while sending request.");
		} catch (ParserConfigurationException e)
		{
			System.err.println("Parser Exception While reading OpenLS Response.");
		} catch (SAXException e)
		{
			System.err.println("SAX Exception While reading OpenLS Response.");
		} catch (TransformerException e)
		{
			System.err.println("Error while creating OpenLS Request.");
		} catch (BadOpenLSResponseException e)
		{
			System.err.println("Error While parsing Response, probably no route was found.");
		}
		return null;
	}

	private String getStringRequest(OpenLSRequestPreferences lsRequestPreferences) throws ParserConfigurationException, TransformerException
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		document.setXmlVersion("1.0");
		Element xls = document.createElement("xls:XLS");
		document.appendChild(xls);
		Attr attr = document.createAttribute("xmlns:xls");
		attr.setValue("http://www.opengis.net/xls");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:xsch");
		attr.setValue("http://www.ascc.net/xml/schematron");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:gml");
		attr.setValue("http://www.opengis.net/gml");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:xlink");
		attr.setValue("http://www.w3.org/1999/xlink");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:schemaLocation");
		attr.setValue("http://www.opengis.net/xls http://schemas.opengis.net/ols/1.1.0/RouteService.xsd");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("version");
		attr.setValue("1.1");
		xls.setAttributeNode(attr);
		attr = document.createAttribute("xls:lang");
		attr.setValue("en");
		xls.setAttributeNode(attr);
		// END BASE ATTRIBUTES
		/*
		 * <xls:SecondaryRoutePreferences> <surfaceQuality>Cobblestones</surfaceQuality> <maxSlope>3</maxSlope> </xls:SecondaryRoutePreferences>
		 */
		Element requestHeader = document.createElement("xls:RequestHeader");
		xls.appendChild(requestHeader);
		Element request = document.createElement("xls:Request");
		xls.appendChild(request);
		attr = document.createAttribute("methodName");
		attr.setValue("routeRequest");
		request.setAttributeNode(attr);
		attr = document.createAttribute("requestID");
		attr.setValue("" + lsRequestPreferences.requestId);
		request.setAttributeNode(attr);
		attr = document.createAttribute("version");
		attr.setValue("" + lsRequestPreferences.version);
		request.setAttributeNode(attr);
		Element determineRouteRequest = document.createElement("xls:DetermineRouteRequest");
		request.appendChild(determineRouteRequest);
		attr = document.createAttribute("provideRouteHandle");
		attr.setValue("" + lsRequestPreferences.provideRouteHandle);
		determineRouteRequest.setAttributeNode(attr);
		attr = document.createAttribute("distanceUnit");
		attr.setValue("" + lsRequestPreferences.distanceUnit);
		determineRouteRequest.setAttributeNode(attr);
		Element routePlan = document.createElement("xls:RoutePlan");
		determineRouteRequest.appendChild(routePlan);
		attr = document.createAttribute("useRealTimeTraffic");
		attr.setValue("" + lsRequestPreferences.useRealTimeTraffic);
		routePlan.setAttributeNode(attr);
		/*
		 * attr = document.createAttribute("ExpectedStartTime"); attr.setValue(""+lsRequestPreferences.expectedStartTime); routePlan.setAttributeNode(attr);
		 * 
		 * attr = document.createAttribute("ExpectedEndTime"); attr.setValue(""+lsRequestPreferences.expectedEndTime); routePlan.setAttributeNode(attr);
		 */
		Element routePreference = document.createElement("xls:RoutePreference");
		routePreference.setTextContent(lsRequestPreferences.routePreference);
		routePlan.appendChild(routePreference);
		//SECONDARYROUTEPREFERENCE
		Element secondaryRoutePreference = document.createElement("xls:SecondaryRoutePreferences");
		routePreference.appendChild(secondaryRoutePreference);		
		if (lsRequestPreferences.surfaceQuality != null)
		{
			Element surfaceQuality = document.createElement("surfaceQuality");
			surfaceQuality.setTextContent(lsRequestPreferences.surfaceQuality);
			secondaryRoutePreference.appendChild(surfaceQuality);
		}
		if (lsRequestPreferences.maxSlope != null)
		{
			Element maxSlope = document.createElement("maxSlope");
			maxSlope.setTextContent(lsRequestPreferences.maxSlope);
			secondaryRoutePreference.appendChild(maxSlope);
		}
		if (lsRequestPreferences.tactilePavingRequired != null)
		{
			Element tactilePavingRequired = document.createElement("tactilePavingRequired");
			tactilePavingRequired.setTextContent(lsRequestPreferences.tactilePavingRequired);
			secondaryRoutePreference.appendChild(tactilePavingRequired);
		}
		Element wayPointList = document.createElement("xls:WayPointList");
		routePlan.appendChild(wayPointList);
		for (OpenLSPosition p : lsRequestPreferences.positions)
		{
			String pointType = "";
			if (p == lsRequestPreferences.positions.get(0))
				pointType = "StartPoint";
			else if (p == lsRequestPreferences.positions.get(lsRequestPreferences.positions.size() - 1))
				pointType = "EndPoint";
			else
				pointType = "ViaPoint";
			Element point = document.createElement("xls:" + pointType);
			wayPointList.appendChild(point);
			attr = document.createAttribute("stop");
			attr.setValue("" + lsRequestPreferences.stopAtPoints);
			point.setAttributeNode(attr);
			Element position = document.createElement("xls:Position");
			point.appendChild(position);
			Element gmlPoint = document.createElement("gml:Point");
			position.appendChild(gmlPoint);
			attr = document.createAttribute("srsName");
			attr.setValue("" + lsRequestPreferences.srsName);
			gmlPoint.setAttributeNode(attr);
			Element gmlPosition = document.createElement("gml:pos");
			gmlPosition.setTextContent(p.toString());
			gmlPoint.appendChild(gmlPosition);
		}
		Element routeGeometryRequest = document.createElement("xls:RouteGeometryRequest");
		determineRouteRequest.appendChild(routeGeometryRequest);
		attr = document.createAttribute("scale");
		attr.setValue("1");
		routeGeometryRequest.setAttributeNode(attr);
		attr = document.createAttribute("provideStartingPortion");
		attr.setValue("" + lsRequestPreferences.provideStartingPortion);
		routeGeometryRequest.setAttributeNode(attr);
		attr = document.createAttribute("maxPoints");
		attr.setValue("" + lsRequestPreferences.maxPoints);
		routeGeometryRequest.setAttributeNode(attr);
		/*
		 * Element routeInstructionsRequest = document.createElement("xls:RouteInstructionsRequest"); determineRouteRequest.appendChild(routeInstructionsRequest);
		 * 
		 * attr = document.createAttribute("format"); attr.setValue("text/plain"); routeInstructionsRequest.setAttributeNode(attr);
		 * 
		 * attr = document.createAttribute("provideGeometry"); attr.setValue(Props.getStr("provideGeometry")); routeInstructionsRequest.setAttributeNode(attr);
		 * 
		 * attr = document.createAttribute("provideBoundingBox"); attr.setValue(Props.getStr("provideBoundingBox"));
		 * routeInstructionsRequest.setAttributeNode(attr);
		 * 
		 * Element routeMapRequest = document.createElement("xls:RouteMapRequest"); determineRouteRequest.appendChild(routeMapRequest);
		 * 
		 * Element xlsoutput = document.createElement("xls:Output"); routeMapRequest.appendChild(xlsoutput);
		 */
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "YES");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String output = writer.getBuffer().toString();
		return output;
	}
}
