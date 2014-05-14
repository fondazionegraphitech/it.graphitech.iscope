package nFinteractionManager;

import java.awt.geom.Point2D;
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

public class Export {

	public static String createExportJobXML(String productP, String accountP,
			String srsP, Point2D.Double[] vertex, boolean exportByTileId,
			boolean exportByBB, String selectedTile, String namingPattern,
			String mode, String lodsP,String subdivisionId,String lodmodeP, String expoFormat, boolean addPointCloud ) throws Throwable {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();

		Document document = documentBuilder.newDocument();
		document.setXmlVersion("1.0");
		
		Element wpsExecute = document.createElement("wps:Execute");
		wpsExecute.setAttribute("service", "WPS");
		wpsExecute.setAttribute("version", "1.0.0");
		wpsExecute.setAttribute("xmlns:wps", "http://www.opengis.net/wps/1.0.0");
		wpsExecute.setAttribute("xmlns:ows", "http://www.opengis.net/ows/1.1");
		wpsExecute.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		wpsExecute.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		wpsExecute.setAttribute("xsi:schemaLocation", "http://www.opengis.net/wps/1.0.0 wpsExecute_request.xsd");
		document.appendChild(wpsExecute);
		
			Element identifi = document.createElement("ows:Identifier");
			identifi.setTextContent("Export");
			wpsExecute.appendChild(identifi);			
			Element dataInputs = document.createElement("wps:DataInputs");			
			wpsExecute.appendChild(dataInputs);
				Element wpsInput = document.createElement("wps:Input");
				dataInputs.appendChild(wpsInput);
					Element identifi2 = document.createElement("ows:Identifier");
					identifi2.setTextContent("JobParameter");
					wpsInput.appendChild(identifi2);	
					Element title = document.createElement("ows:Title");
					title.setTextContent("Export CityGML " + productP);
					wpsInput.appendChild(title);	
					Element data = document.createElement("wps:Data");					
					wpsInput.appendChild(data);	
						Element complexData = document.createElement("wps:ComplexData");					
						data.appendChild(complexData);	
							Element EXPORT_JOB = document.createElement("EXPORT_JOB");									
							Attr attr = document.createAttribute("version");
							attr.setValue("1.0.0");
							EXPORT_JOB.setAttributeNode(attr);
							complexData.appendChild(EXPORT_JOB);
							// END BASE ATTRIBUTES
								Element job = document.createElement("job");
								EXPORT_JOB.appendChild(job);
									Element initiator = document.createElement("initiator");
									initiator.setTextContent("iSCOPEwebClient");  //This number indicates a folder in the general novaFACTORY-Export-Path. General this number should be the same for all Export-Jobs of this WPS-Service, so all exported files are collected within this folder.
									job.appendChild(initiator);
									Element jobNumb = document.createElement("jobnumber");
									jobNumb.setTextContent(productP + "_CGML" ); //The name of the job and also the name of the folder within the initiator-Folder. If this jobnumber already exists, a unique number is automatically added so that no existing export job is overwritten. For example: I started an WPS Exportjob with the jobnumber NCC_CGML. It already existed and so it was automatically renamed in NCC_CGML456.
									job.appendChild(jobNumb);
									Element account = document.createElement("account");
									account.setTextContent(accountP);  //CustomerNumberValue
									job.appendChild(account);
								Element product = document.createElement("product");
								product.setTextContent(productP);
								EXPORT_JOB.appendChild(product);
								Element layers = document.createElement("layers");			
								layers.setAttribute("color", "0");
								layers.setAttribute("mono", "0");
								layers.setAttribute("plotframe", "0");
								layers.setAttribute("single", "1");
								layers.setAttribute("plotLabelSrs", "-1");
								EXPORT_JOB.appendChild(layers);
								Element layer = document.createElement("layer");
									layer.setAttribute("name", "build");
									layer.setAttribute("product", productP);  
									layer.setAttribute("style", "#000000");
									layers.appendChild(layer);
								Element srs = document.createElement("srs");
								srs.setTextContent(srsP); //define de prj sys to use in output
								EXPORT_JOB.appendChild(srs);
								Element extent = document.createElement("extent");
								extent.setAttribute("merge_mapsheets", "off");  //merge mapsheet attribute
								EXPORT_JOB.appendChild(extent);			
								    if(exportByBB){
										Element poly = document.createElement("polygon");
										poly.setAttribute("mode", mode); //mode can be "polygon" wich means exactly that poly or unit which means all tiles that are attached by this polygon 
										poly.setAttribute("simplify", "1");
										poly.setAttribute("srs", srsP);
										extent.appendChild(poly);
											Element ring = document.createElement("ring");
											poly.appendChild(ring);	
												Element exterior = document.createElement("exterior");
												ring.appendChild(exterior);
												    for (int i=0; i<vertex.length; i++){ //aggiungo i vertici del polygono/ quadorato di export
												    	Element vertexX = document.createElement("vertex");
												    	vertexX.setAttribute("x", String.valueOf(vertex[i].getX()));
												    	vertexX.setAttribute("y", String.valueOf(vertex[i].getY()));
												    	exterior.appendChild(vertexX);
												    }
									    }
								    else if (exportByTileId){
								    	Element unit = document.createElement("unit");
								    	unit.setAttribute("exterior", "0");
								    	unit.setAttribute("frame", "0");
								    	unit.setAttribute("select_mapsheets", "0");
								    	unit.setAttribute("subdivision", subdivisionId);
								    	unit.setTextContent(selectedTile);
								    	extent.appendChild(unit);			    	
								    }
											    
								Element resolution = document.createElement("resolution");
								resolution.setTextContent("100");
								EXPORT_JOB.appendChild(resolution);
								
								Element scale = document.createElement("scale");
								scale.setTextContent("1000");			
								EXPORT_JOB.appendChild(scale);
								
								Element format = document.createElement("format");
								format.setAttribute("alphalinscale", "0.0");
								format.setAttribute("alphascale", "1.0");
								format.setAttribute("citygml_actfunc", "undef");
								format.setAttribute("citygml_apptheme", "");
								format.setAttribute("citygml_elemclasses", "true");
								format.setAttribute("citygml_lodmode", lodmodeP);
								format.setAttribute("citygml_lods", lodsP);
								format.setAttribute("citygml_metadata", "true");
								format.setAttribute("citygml_outmode", "normal");
								format.setAttribute("dtm", "false");
								if (expoFormat.equals("CityGML")) format.setAttribute("foredit", "false");
								if (expoFormat.equals("SketchUP")) format.setAttribute("foredit", "true");
								format.setAttribute("materialcopymode", "none");
								format.setAttribute("polyopts_reverse", "false");
								format.setAttribute("relcoords", "false");
								format.setAttribute("rooftxr", "false");			
								format.setAttribute("roundcoords", "3");
								format.setAttribute("schemetxr", "false");
								if (expoFormat.equals("SketchUP")){
									format.setAttribute("skp_hideedges", "false");
									format.setAttribute("skp_skipline", "false");
									format.setAttribute("skp_skipti", "false");							
								}								        
								format.setAttribute("solar", "false");
								format.setAttribute("solargeoplex", "false");
								format.setAttribute("tex", "false");
								format.setAttribute("tolod1", "false");
								if (addPointCloud) {
									format.setAttribute("xyz", "true");
									format.setAttribute("xyz_layer", "");
									format.setAttribute("xyz_prod", "");
								}
								format.setAttribute("xyz", "false");
								if (expoFormat.equals("CityGML")) format.setTextContent("CityGML");
								if (expoFormat.equals("SketchUP")) format.setTextContent("SKP");
								
								EXPORT_JOB.appendChild(format);
								
								Element exportmeta = document.createElement("exportmetadata");
								exportmeta.setAttribute("calibration", "0");
								exportmeta.setAttribute("xmetadata", "0");
								exportmeta.setTextContent("1");
								EXPORT_JOB.appendChild(exportmeta);
								
								Element addFile = document.createElement("addfile");
								addFile.setAttribute("col", "0");
								addFile.setAttribute("eck", "0");
								EXPORT_JOB.appendChild(addFile);
								
								Element nodata = document.createElement("usenodatamask");
								nodata.setTextContent("0");
								EXPORT_JOB.appendChild(nodata);
								
								Element usepdctborderpoly = document.createElement("usepdctborderpoly");
								usepdctborderpoly.setTextContent("0");
								EXPORT_JOB.appendChild(usepdctborderpoly);
								
								Element dhkresolvereferences = document.createElement("dhkresolvereferences");
								if (expoFormat.equals("CityGML")) dhkresolvereferences.setTextContent("0");
								if (expoFormat.equals("SketchUP")) dhkresolvereferences.setTextContent("1");
								
								EXPORT_JOB.appendChild(dhkresolvereferences);
								
								Element zipresult = document.createElement("zipresult");
								zipresult.setTextContent("1");
								EXPORT_JOB.appendChild(zipresult);
								
								Element UserDescriptionContent = document.createElement("userdescription");
								UserDescriptionContent.setTextContent("The parameter passed to the export function are:" + productP + "-"+ accountP +"-"+ srsP+"-"+ vertex+"-"+ exportByTileId +"-"+
										exportByBB+"-"+ selectedTile +"-"+ namingPattern +"-"+ mode +"-"+ lodsP +"-"+ subdivisionId +"-"+ lodmodeP + expoFormat);
								EXPORT_JOB.appendChild(UserDescriptionContent);
								
								Element namingpattern = document.createElement("namingpattern");
								namingpattern.setTextContent(namingPattern);   //naming pattern
								EXPORT_JOB.appendChild(namingpattern);	
								
								if (expoFormat.equals("SketchUP")){								
								Element tiles = document.createElement("tilestiles");
								tiles.setAttribute("sizex","6000");
								tiles.setAttribute("sizey","6000");
								tiles.setAttribute("units","pixel");
								tiles.setAttribute("xoffset","0");
								tiles.setAttribute("yoffset","0");								
								EXPORT_JOB.appendChild(tiles);	
								}
								
				Element responeForm = document.createElement("wps:ResponseForm");
				wpsExecute.appendChild(responeForm);
					Element responseDoc = document.createElement("wps:ResponseDocument");
					responseDoc.setAttribute("storeExecuteResponse", "true");
					responseDoc.setAttribute("lineage", "true");
					responseDoc.setAttribute("status", "true");
					responeForm.appendChild(responseDoc);
						Element output = document.createElement("wps:Output");
						output.setAttribute("mimeType", "application/zip");
						responseDoc.appendChild(output);
							Element identifier2 = document.createElement("ows:Identifier");							
				     		identifier2.setTextContent("ExportResult");
							output.appendChild(identifier2);
							Element title2 = document.createElement("ows:Title");							
							title2.setTextContent("Export result");
							output.appendChild(title2);
							Element owsAbstract = document.createElement("ows:Abstract");							
							owsAbstract.setTextContent("An CityGML-File");
							output.appendChild(owsAbstract);
			
		
				
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "YES");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String outputXML = writer.getBuffer().toString();		
		return outputXML;
		
	}

}
