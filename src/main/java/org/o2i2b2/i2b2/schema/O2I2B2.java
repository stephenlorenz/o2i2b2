//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.09 at 07:29:30 AM EDT 
//


package org.o2i2b2.i2b2.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}sources"/>
 *         &lt;element ref="{}ontology"/>
 *         &lt;element ref="{}concepts"/>
 *         &lt;element ref="{}patients"/>
 *         &lt;element ref="{}encounters"/>
 *         &lt;element ref="{}providers"/>
 *         &lt;element ref="{}observations"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orgCode" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="orgName" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sources",
    "ontology",
    "concepts",
    "patients",
    "encounters",
    "providers",
    "observations"
})
@XmlRootElement(name = "o2i2b2")
public class O2I2B2 {

    @XmlElement(required = true)
    protected Sources sources;
    @XmlElement(required = true)
    protected Ontology ontology;
    @XmlElement(required = true)
    protected Concepts concepts;
    @XmlElement(required = true)
    protected Patients patients;
    @XmlElement(required = true)
    protected Encounters encounters;
    @XmlElement(required = true)
    protected Providers providers;
    @XmlElement(required = true)
    protected Observations observations;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String orgCode;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String orgName;

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link Sources }
     *     
     */
    public Sources getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sources }
     *     
     */
    public void setSources(Sources value) {
        this.sources = value;
    }

    /**
     * Gets the value of the ontology property.
     * 
     * @return
     *     possible object is
     *     {@link Ontology }
     *     
     */
    public Ontology getOntology() {
        return ontology;
    }

    /**
     * Sets the value of the ontology property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ontology }
     *     
     */
    public void setOntology(Ontology value) {
        this.ontology = value;
    }

    /**
     * Gets the value of the concepts property.
     * 
     * @return
     *     possible object is
     *     {@link Concepts }
     *     
     */
    public Concepts getConcepts() {
        return concepts;
    }

    /**
     * Sets the value of the concepts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Concepts }
     *     
     */
    public void setConcepts(Concepts value) {
        this.concepts = value;
    }

    /**
     * Gets the value of the patients property.
     * 
     * @return
     *     possible object is
     *     {@link Patients }
     *     
     */
    public Patients getPatients() {
        return patients;
    }

    /**
     * Sets the value of the patients property.
     * 
     * @param value
     *     allowed object is
     *     {@link Patients }
     *     
     */
    public void setPatients(Patients value) {
        this.patients = value;
    }

    /**
     * Gets the value of the encounters property.
     * 
     * @return
     *     possible object is
     *     {@link Encounters }
     *     
     */
    public Encounters getEncounters() {
        return encounters;
    }

    /**
     * Sets the value of the encounters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Encounters }
     *     
     */
    public void setEncounters(Encounters value) {
        this.encounters = value;
    }

    /**
     * Gets the value of the providers property.
     * 
     * @return
     *     possible object is
     *     {@link Providers }
     *     
     */
    public Providers getProviders() {
        return providers;
    }

    /**
     * Sets the value of the providers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Providers }
     *     
     */
    public void setProviders(Providers value) {
        this.providers = value;
    }

    /**
     * Gets the value of the observations property.
     * 
     * @return
     *     possible object is
     *     {@link Observations }
     *     
     */
    public Observations getObservations() {
        return observations;
    }

    /**
     * Sets the value of the observations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Observations }
     *     
     */
    public void setObservations(Observations value) {
        this.observations = value;
    }

    /**
     * Gets the value of the orgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the value of the orgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgCode(String value) {
        this.orgCode = value;
    }

    /**
     * Gets the value of the orgName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * Sets the value of the orgName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgName(String value) {
        this.orgName = value;
    }

}
