//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.24 at 09:58:25 PM EDT 
//


package org.o2i2b2.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}Enums"/>
 *         &lt;element ref="{}Counts"/>
 *         &lt;element ref="{}New"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "enums",
    "counts",
    "_new"
})
@XmlRootElement(name = "Analysis")
public class Analysis {

    @XmlElement(name = "Enums", required = true)
    protected Enums enums;
    @XmlElement(name = "Counts", required = true)
    protected Counts counts;
    @XmlElement(name = "New", required = true)
    protected New _new;

    /**
     * Gets the value of the enums property.
     * 
     * @return
     *     possible object is
     *     {@link Enums }
     *     
     */
    public Enums getEnums() {
        return enums;
    }

    /**
     * Sets the value of the enums property.
     * 
     * @param value
     *     allowed object is
     *     {@link Enums }
     *     
     */
    public void setEnums(Enums value) {
        this.enums = value;
    }

    /**
     * Gets the value of the counts property.
     * 
     * @return
     *     possible object is
     *     {@link Counts }
     *     
     */
    public Counts getCounts() {
        return counts;
    }

    /**
     * Sets the value of the counts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Counts }
     *     
     */
    public void setCounts(Counts value) {
        this.counts = value;
    }

    /**
     * Gets the value of the new property.
     * 
     * @return
     *     possible object is
     *     {@link New }
     *     
     */
    public New getNew() {
        return _new;
    }

    /**
     * Sets the value of the new property.
     * 
     * @param value
     *     allowed object is
     *     {@link New }
     *     
     */
    public void setNew(New value) {
        this._new = value;
    }

}