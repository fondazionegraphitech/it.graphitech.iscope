package mangeXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class XMLconverter {
	
	public static String convertXMLFileToString(String fileName) 
	{ 
	   try{ 
	       DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); 
	       InputStream inputStream = new FileInputStream(new File(fileName)); 
	       org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream); 
	       StringWriter stw = new StringWriter(); 
	       Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
	       serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
	       return stw.toString(); 
	   } 
	   catch (Exception e) { 
	       e.printStackTrace(); 
	   } 
	   return null; 
	}
	
	public static String convertDocumentToString(org.w3c.dom.Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
         
        return null;
    }
	
	public static org.w3c.dom.Document convertStringToDocument(String xmlStr) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	        DocumentBuilder builder;  
	        try 
	        {  
	            builder = factory.newDocumentBuilder();  
	            org.w3c.dom.Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
	            return doc;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	        return null;
	    }	

}
