package org.o2i2b2.test;

import junit.framework.TestCase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.o2i2b2.i2b2.schema.Concepts;
import org.o2i2b2.i2b2.schema.Meta;
import org.o2i2b2.i2b2.schema.O2I2B2;
import org.o2i2b2.i2b2.schema.Ontology;
import org.o2i2b2.i2b2.schema.Source;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: May 7, 2006
 * Time: 11:00:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParseTest extends TestCase {
    private long memstart;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.gc();
        System.gc();
        memstart = Runtime.getRuntime().freeMemory();
    }

    public void testParseEntireDocument() throws JAXBException {
    	JAXBContext ctx = JAXBContext.newInstance(new Class[]{O2I2B2.class});
        Unmarshaller um = ctx.createUnmarshaller();
        O2I2B2 o2i2b2 = (O2I2B2) um.unmarshal(new File("c:/Users/slorenz/Desktop/i2b2Export.xml"));
        assertEquals("PIH-MWI", o2i2b2.getOrgCode());

        System.gc();
        System.gc();
        long memend = Runtime.getRuntime().freeMemory();
        System.out.println("Memory used: " + (memstart - memend));
    }

    public void testParseEfficiently() throws IOException, XMLStreamException, JAXBException {
        // Parse the data, filtering out the start elements
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        FileReader fr = new FileReader("c:/Users/slorenz/Desktop/i2b2Export.xml");
        XMLEventReader xmler = xmlif.createXMLEventReader(fr);
        EventFilter filter = new EventFilter() {
            public boolean accept(XMLEvent event) {
                return event.isStartElement();
            }
        };
        XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, filter);

        // Jump to the first element in the document, the enclosing BugCollection
        StartElement e = (StartElement) xmlfer.nextEvent();
        assertEquals("o2i2b2", e.getName().getLocalPart());

        // Parse into typed objects
        JAXBContext ctx = JAXBContext.newInstance("org.o2i2b2.i2b2.schema");
        Unmarshaller um = ctx.createUnmarshaller();
        int bugs = 0;
//        BugInstance bi = null;
        while (xmlfer.peek() != null) {
            Object o = um.unmarshal(xmler);
            
            if (o instanceof Ontology) {
            	Ontology ontology = (Ontology) o;
                // process the bug instance
                System.out.println("ontology: " + ontology.toString());
                
                for (Meta meta : ontology.getMeta()) {
                	System.out.println("Meta: " + meta.getName());
                }
                	
                
                bugs++;
            }
        }
//        assertEquals(180, bugs);
        fr.close();

        System.gc();
        System.gc();
        long memend = Runtime.getRuntime().freeMemory();
        System.out.println("Memory used: " + (memstart - memend));
//        assertNotNull(bi);
    }
}
