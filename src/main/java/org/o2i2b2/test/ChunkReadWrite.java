package org.o2i2b2.test;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.FileOutputStream;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.o2i2b2.i2b2.schema.Concept;
import org.o2i2b2.i2b2.schema.Encounter;
import org.o2i2b2.i2b2.schema.Meta;
import org.o2i2b2.i2b2.schema.Observation;
import org.o2i2b2.i2b2.schema.Patient;
import org.o2i2b2.i2b2.schema.Provider;
import org.o2i2b2.i2b2.schema.Source;

public class ChunkReadWrite {

	public static void main(String[] args) throws Exception {

//		String inFile = "/tmp/contacts.xml";
		//		String outFile = "/tmp/contactSt.xml";

		// set up a StAX reader
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader xmlr = xmlif.createXMLStreamReader(new FileReader("c:/Users/slorenz/Desktop/i2b2Export.xml"));

		//set up JAXB contexts
		JAXBContext jaxbContextSource = JAXBContext.newInstance(Source.class);
		Unmarshaller unmarshallerSource = jaxbContextSource.createUnmarshaller();

		JAXBContext jaxbContextMeta = JAXBContext.newInstance(Meta.class);
		Unmarshaller unmarshallerMeta = jaxbContextMeta.createUnmarshaller();

		JAXBContext jaxbContextConcept = JAXBContext.newInstance(Concept.class);
		Unmarshaller unmarshallerConcept = jaxbContextConcept.createUnmarshaller();

		JAXBContext jaxbContextPatient = JAXBContext.newInstance(Patient.class);
		Unmarshaller unmarshallerPatient = jaxbContextPatient.createUnmarshaller();

		JAXBContext jaxbContextProvider = JAXBContext.newInstance(Provider.class);
		Unmarshaller unmarshallerProvider = jaxbContextProvider.createUnmarshaller();

		JAXBContext jaxbContextEncounter = JAXBContext.newInstance(Encounter.class);
		Unmarshaller unmarshallerEncounter = jaxbContextEncounter.createUnmarshaller();

		JAXBContext jaxbContextObservation = JAXBContext.newInstance(Observation.class);
		Unmarshaller unmarshallerObservation = jaxbContextObservation.createUnmarshaller();

		//Set up out put file. 
		//		FileOutputStream outputStream = new FileOutputStream( outFile );  
		//		XMLOutputFactory outputFactory=XMLOutputFactory.newInstance();
		//		XMLStreamWriter xmlStreamWriter= outputFactory.createXMLStreamWriter(outputStream,"UTF-8");

		//If we want to indent StAX out put  
		//com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter sw = new IndentingXMLStreamWriter(defaultWriter);
		//sw.setIndentStep("    ");

		// Set up JAXB for Contact class
//		Marshaller marshaller = JAXBContext.newInstance( Concept.class ).createMarshaller();
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		marshaller.setProperty("jaxb.fragment", Boolean.TRUE);

		// move to the root element and check its name.
		xmlr.nextTag();
		xmlr.require(START_ELEMENT, null, "o2i2b2");

		//Pre-fill the file
		//		xmlStreamWriter.writeStartDocument("UTF-8","1.0");
		//		xmlStreamWriter.writeComment(" A test for StAX ");
		//		xmlStreamWriter.writeStartElement("addressBook");
		//		outputStream.flush();

		String parentElementName = null;
		
		
		while (true) {
			int event = xmlr.next();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				xmlr.close();
				break;
			}
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (xmlr.getLocalName().equalsIgnoreCase("sources")) parentElementName = xmlr.getLocalName();
				else if (xmlr.getLocalName().equalsIgnoreCase("ontology")) parentElementName = xmlr.getLocalName();
				else if (xmlr.getLocalName().equalsIgnoreCase("concepts")) parentElementName = xmlr.getLocalName();
				else if (xmlr.getLocalName().equalsIgnoreCase("patients")) parentElementName = xmlr.getLocalName();
				else if (xmlr.getLocalName().equalsIgnoreCase("encounters")) parentElementName = xmlr.getLocalName();
				else if (xmlr.getLocalName().equalsIgnoreCase("observations")) parentElementName = xmlr.getLocalName();

				if (xmlr.getLocalName().equalsIgnoreCase("source") && parentElementName.equalsIgnoreCase("sources")) {
					Source source = (Source) unmarshallerSource.unmarshal(xmlr);
					System.out.println("  Source ==> " + source.getName());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("meta")) {
					Meta meta = (Meta) unmarshallerMeta.unmarshal(xmlr);
					System.out.println("  Meta ==> " + meta.getName());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("concept")) {
					Concept concept = (Concept) unmarshallerConcept.unmarshal(xmlr);
					System.out.println("  Concept ==> " + concept.getName());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("patient")) {
					Patient patient = (Patient) unmarshallerPatient.unmarshal(xmlr);
					System.out.println("  Patient ==> " + patient.getId());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("provider")) {
					Provider provider = (Provider) unmarshallerProvider.unmarshal(xmlr);
					System.out.println("  Provider ==> " + provider.getId());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("encounter")) {
					Encounter encounter = (Encounter) unmarshallerEncounter.unmarshal(xmlr);
					System.out.println("  Encounter ==> " + encounter.getId());
				} else	if (xmlr.getLocalName().equalsIgnoreCase("observation")) {
					Observation observation = (Observation) unmarshallerObservation.unmarshal(xmlr);
					System.out.println("  Observation ==> " + observation.getConceptCode());
				}


			}
		}

		// move to the first contact element.
		//		xmlr.nextTag(); 
		//		while (xmlr.getEventType() == START_ELEMENT) {
		//			
		//			System.out.println("here: " + xmlr.getLocalName());
		//			xmlr..nextTag();

		//			xmlr.require(START_ELEMENT, null, "sources");
		// unmarshall one contact element into a JAXB Contact object
		//			Concept concept = (Concept) um.unmarshal(xmlr);

		//			System.out.println("Concept: " + concept.getName());

		//Do any processing ....

		//write out the contact object 
		//			marshaller.marshal(concept, xmlStreamWriter);

		//marshaller.marshal(contact, System.out);

		// skip the whitespace between contacts.
		//			if (xmlr.getEventType() == CHARACTERS) {
		//				xmlr.next(); 
		//			}
		//		}

		//		xmlStreamWriter.writeEndElement();
		//		outputStream.flush();
		//		outputStream.close();
	}
}

