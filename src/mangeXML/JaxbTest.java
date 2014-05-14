package mangeXML;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class JaxbTest{
	
	static String XMLrequestFileName = "E:\\iSCOPE\\WFS\\wfsGetFeatureRequestBIDs.xml";
	
	public static void read() throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance("my.package.name");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		 Object myFile = unmarshaller.unmarshal(new File(XMLrequestFileName));			
		 System.out.println("tst");
	}
	 
}
