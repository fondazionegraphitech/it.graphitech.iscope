package solar;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SolarAssesment {
	
	public static void sendSolarAssesmentrequest(){
		
	}

	public static String generateSolarAssesmentRequest(boolean requestByBldID, boolean requestByBB, String buildingID, String bBminX, String bBminY, String bBmaxnX, String bBmaxY, String calculationStart, String calculationEnd, String minIrradiationP, String minRoofAreaP, String panelCoverageP, String operationCostsP, String energyCostsP, String feedInTariffP, String CO2consumptionP, String efficiencyP, String aquisitionCostsP, String transmissionLossP, String showRoofSurfaceP) throws Throwable{
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		
		Document document = documentBuilder.newDocument();
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);
		Element wpsExecuteRootElement = document.createElement("wps:Execute");
		document.appendChild(wpsExecuteRootElement);
		Attr attr = document.createAttribute("service");
		attr.setValue("WPS");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("version");
		attr.setValue("1.0.0");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:wps");
		attr.setValue("http://www.opengis.net/wps/1.0.0");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:ows");
		attr.setValue("http://www.opengis.net/ows/1.1");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:xlink");
		attr.setValue("http://www.w3.org/1999/xlink");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		wpsExecuteRootElement.setAttributeNode(attr);
		attr = document.createAttribute("xsi:schemaLocation");
		attr.setValue("http://www.opengis.net/wps/1.0.0 wpsExecute_request.xsd");
		wpsExecuteRootElement.setAttributeNode(attr);
		// END BASE ATTRIBUTES
		Element owsIdentifier = document.createElement("ows:Identifier");
		owsIdentifier.setTextContent("SolarAssessment");		
		wpsExecuteRootElement.appendChild(owsIdentifier);
		
		Element dataInputs = document.createElement("wps:DataInputs");
		wpsExecuteRootElement.appendChild(dataInputs);
		Element inputs = document.createElement("wps:Input");
		dataInputs.appendChild(inputs);
		Element identifier = document.createElement("ows:Identifier");
		identifier.setTextContent("productMnemonic");
		inputs.appendChild(identifier);
		Element data = document.createElement("wps:Data");
		inputs.appendChild(data);
		Element literalData = document.createElement("wps:LiteralData");
		attr = document.createAttribute("dataType");
		attr.setValue("string");
		literalData.setAttributeNode(attr);
		literalData.setTextContent("INFOTN"); //set the name of the pilot
		data.appendChild(literalData);
		
		Element wpsInput2 = document.createElement("wps:Input");
		dataInputs.appendChild(wpsInput2);
		Element identifi = document.createElement("ows:Identifier");
		identifi.setTextContent("InputXML");
		wpsInput2.appendChild(identifi);
		Element data2 = document.createElement("wps:Data");
		wpsInput2.appendChild(data2);
		Element complexData = document.createElement("wps:ComplexData");
		data2.appendChild(complexData);
		Element assesmentReq = document.createElement("assessmentRequest");
		complexData.appendChild(assesmentReq);
			if (requestByBldID){
				Element bldID = document.createElement("buildingID");
				bldID.setTextContent(buildingID);
				assesmentReq.appendChild(bldID);
			}
			else if(requestByBB){
				Element bb = document.createElement("boundingBox");
				assesmentReq.appendChild(bb);
				Element xmin = document.createElement("xmin");
				xmin.setTextContent(bBminX);
				bb.appendChild(xmin);
				Element ymin = document.createElement("ymin");
				ymin.setTextContent(bBminY);
				bb.appendChild(ymin);
				Element xmax = document.createElement("xmax");
				xmax.setTextContent(bBmaxnX);
				bb.appendChild(xmax);
				Element ymax = document.createElement("ymax");
				ymax.setTextContent(bBmaxY);
				bb.appendChild(ymax);			
			}
			Element assset = document.createElement("assessmentSettings");
			assesmentReq.appendChild(assset);
			
				Element calcStart = document.createElement("calculationStart");
				calcStart.setTextContent(calculationStart);
				assset.appendChild(calcStart);
				Element calcEnd = document.createElement("calculationEnd");
				calcEnd.setTextContent(calculationEnd);
				assset.appendChild(calcEnd);
				Element minIrradiation = document.createElement("minIrradiation");
				minIrradiation.setTextContent(minIrradiationP);
				assset.appendChild(minIrradiation);
				Element minRoofArea = document.createElement("minRoofArea");
				minRoofArea.setTextContent(minRoofAreaP);
				assset.appendChild(minRoofArea);
				Element  panelCoverage= document.createElement("panelCoverage");
				panelCoverage.setTextContent(panelCoverageP);
				assset.appendChild(panelCoverage);
				Element panel = document.createElement("panel");
				assset.appendChild(panel);
				
					Element efficiency = document.createElement("efficiency");
					efficiency.setTextContent(efficiencyP);
					panel.appendChild(efficiency);
					Element aquisitionCosts = document.createElement("aquisitionCosts");
					aquisitionCosts.setTextContent(aquisitionCostsP);
					panel.appendChild(aquisitionCosts);		
				
				Element transmissionLoss = document.createElement("transmissionLoss");
				transmissionLoss.setTextContent(transmissionLossP);
				assset.appendChild(transmissionLoss);
				Element operationCosts  = document.createElement("operationCosts");
				operationCosts.setTextContent(operationCostsP);
				assset.appendChild(operationCosts);
				Element energyCosts = document.createElement("energyCosts");
				energyCosts.setTextContent(energyCostsP);
				assset.appendChild(energyCosts);
				Element feedInTariff = document.createElement("feedInTariff");
				feedInTariff.setTextContent(feedInTariffP);
				assset.appendChild(feedInTariff);
				Element CO2consumption = document.createElement("CO2consumption");
				CO2consumption.setTextContent(CO2consumptionP);
				assset.appendChild(CO2consumption);
			Element showRoofSurface = document.createElement("showRoofSurface");
			showRoofSurface.setTextContent(showRoofSurfaceP);
			assesmentReq.appendChild(showRoofSurface);
		Element responeForm = document.createElement("wps:ResponseForm");
		wpsExecuteRootElement.appendChild(responeForm);
			Element responseDoc = document.createElement("wps:ResponseDocument");
			responeForm.appendChild(responseDoc);
				Element output = document.createElement("wps:Output");
				responseDoc.appendChild(output);
					Element identifier2 = document.createElement("ows:Identifier");
					identifier2.setTextContent("OutputXML");
					output.appendChild(identifier2);
					
				
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "YES");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String outputXML = writer.getBuffer().toString();		
		System.out.println(outputXML);	
		return outputXML;
	}
	
	

}
