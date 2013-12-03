//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.24 at 09:58:25 PM EDT 
//


package org.o2i2b2.xsd;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}Version"/>
 *         &lt;element ref="{}CreationDateTime"/>
 *         &lt;element ref="{}TestID"/>
 *         &lt;element ref="{}TestName"/>
 *         &lt;element ref="{}DataType"/>
 *         &lt;element ref="{}CodeType"/>
 *         &lt;element ref="{}Loinc"/>
 *         &lt;element ref="{}Flagstouse"/>
 *         &lt;element ref="{}Oktousevalues"/>
 *         &lt;element ref="{}MaxStringLength"/>
 *         &lt;element ref="{}LowofLowValue"/>
 *         &lt;element ref="{}HighofLowValue"/>
 *         &lt;element ref="{}LowofHighValue"/>
 *         &lt;element ref="{}HighofHighValue"/>
 *         &lt;element ref="{}LowofToxicValue"/>
 *         &lt;element ref="{}HighofToxicValue"/>
 *         &lt;element ref="{}EnumValues"/>
 *         &lt;element ref="{}CommentsDeterminingExclusion"/>
 *         &lt;element ref="{}UnitValues"/>
 *         &lt;element ref="{}Analysis"/>
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
    "version",
    "creationDateTime",
    "testID",
    "testName",
    "dataType",
    "codeType",
    "loinc",
    "flagstouse",
    "oktousevalues",
    "maxStringLength",
    "lowofLowValue",
    "highofLowValue",
    "lowofHighValue",
    "highofHighValue",
    "lowofToxicValue",
    "highofToxicValue",
    "enumValues",
    "commentsDeterminingExclusion",
    "unitValues",
    "analysis"
})
@XmlRootElement(name = "ValueMetadata")
public class ValueMetadata {

    @XmlElement(name = "Version", required = true)
    protected BigDecimal version;
    @XmlElement(name = "CreationDateTime", required = true)
    protected String creationDateTime;
    @XmlElement(name = "TestID", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String testID;
    @XmlElement(name = "TestName", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String testName;
    @XmlElement(name = "DataType", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String dataType;
    @XmlElement(name = "CodeType", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String codeType;
    @XmlElement(name = "Loinc", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String loinc;
    @XmlElement(name = "Flagstouse", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String flagstouse;
    @XmlElement(name = "Oktousevalues", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String oktousevalues;
    @XmlElement(name = "MaxStringLength", required = true)
    protected MaxStringLength maxStringLength;
    @XmlElement(name = "LowofLowValue", required = true)
    protected BigInteger lowofLowValue;
    @XmlElement(name = "HighofLowValue", required = true)
    protected BigInteger highofLowValue;
    @XmlElement(name = "LowofHighValue", required = true)
    protected BigInteger lowofHighValue;
    @XmlElement(name = "HighofHighValue", required = true)
    protected BigInteger highofHighValue;
    @XmlElement(name = "LowofToxicValue", required = true)
    protected LowofToxicValue lowofToxicValue;
    @XmlElement(name = "HighofToxicValue", required = true)
    protected HighofToxicValue highofToxicValue;
    @XmlElement(name = "EnumValues", required = true)
    protected EnumValues enumValues;
    @XmlElement(name = "CommentsDeterminingExclusion", required = true)
    protected CommentsDeterminingExclusion commentsDeterminingExclusion;
    @XmlElement(name = "UnitValues", required = true)
    protected UnitValues unitValues;
    @XmlElement(name = "Analysis", required = true)
    protected Analysis analysis;

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

    /**
     * Gets the value of the creationDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the value of the creationDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationDateTime(String value) {
        this.creationDateTime = value;
    }

    /**
     * Gets the value of the testID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestID() {
        return testID;
    }

    /**
     * Sets the value of the testID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestID(String value) {
        this.testID = value;
    }

    /**
     * Gets the value of the testName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Sets the value of the testName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestName(String value) {
        this.testName = value;
    }

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the codeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeType() {
        return codeType;
    }

    /**
     * Sets the value of the codeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeType(String value) {
        this.codeType = value;
    }

    /**
     * Gets the value of the loinc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoinc() {
        return loinc;
    }

    /**
     * Sets the value of the loinc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoinc(String value) {
        this.loinc = value;
    }

    /**
     * Gets the value of the flagstouse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlagstouse() {
        return flagstouse;
    }

    /**
     * Sets the value of the flagstouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlagstouse(String value) {
        this.flagstouse = value;
    }

    /**
     * Gets the value of the oktousevalues property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOktousevalues() {
        return oktousevalues;
    }

    /**
     * Sets the value of the oktousevalues property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOktousevalues(String value) {
        this.oktousevalues = value;
    }

    /**
     * Gets the value of the maxStringLength property.
     * 
     * @return
     *     possible object is
     *     {@link MaxStringLength }
     *     
     */
    public MaxStringLength getMaxStringLength() {
        return maxStringLength;
    }

    /**
     * Sets the value of the maxStringLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link MaxStringLength }
     *     
     */
    public void setMaxStringLength(MaxStringLength value) {
        this.maxStringLength = value;
    }

    /**
     * Gets the value of the lowofLowValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLowofLowValue() {
        return lowofLowValue;
    }

    /**
     * Sets the value of the lowofLowValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLowofLowValue(BigInteger value) {
        this.lowofLowValue = value;
    }

    /**
     * Gets the value of the highofLowValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHighofLowValue() {
        return highofLowValue;
    }

    /**
     * Sets the value of the highofLowValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHighofLowValue(BigInteger value) {
        this.highofLowValue = value;
    }

    /**
     * Gets the value of the lowofHighValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLowofHighValue() {
        return lowofHighValue;
    }

    /**
     * Sets the value of the lowofHighValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLowofHighValue(BigInteger value) {
        this.lowofHighValue = value;
    }

    /**
     * Gets the value of the highofHighValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHighofHighValue() {
        return highofHighValue;
    }

    /**
     * Sets the value of the highofHighValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHighofHighValue(BigInteger value) {
        this.highofHighValue = value;
    }

    /**
     * Gets the value of the lowofToxicValue property.
     * 
     * @return
     *     possible object is
     *     {@link LowofToxicValue }
     *     
     */
    public LowofToxicValue getLowofToxicValue() {
        return lowofToxicValue;
    }

    /**
     * Sets the value of the lowofToxicValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link LowofToxicValue }
     *     
     */
    public void setLowofToxicValue(LowofToxicValue value) {
        this.lowofToxicValue = value;
    }

    /**
     * Gets the value of the highofToxicValue property.
     * 
     * @return
     *     possible object is
     *     {@link HighofToxicValue }
     *     
     */
    public HighofToxicValue getHighofToxicValue() {
        return highofToxicValue;
    }

    /**
     * Sets the value of the highofToxicValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link HighofToxicValue }
     *     
     */
    public void setHighofToxicValue(HighofToxicValue value) {
        this.highofToxicValue = value;
    }

    /**
     * Gets the value of the enumValues property.
     * 
     * @return
     *     possible object is
     *     {@link EnumValues }
     *     
     */
    public EnumValues getEnumValues() {
        return enumValues;
    }

    /**
     * Sets the value of the enumValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumValues }
     *     
     */
    public void setEnumValues(EnumValues value) {
        this.enumValues = value;
    }

    /**
     * Gets the value of the commentsDeterminingExclusion property.
     * 
     * @return
     *     possible object is
     *     {@link CommentsDeterminingExclusion }
     *     
     */
    public CommentsDeterminingExclusion getCommentsDeterminingExclusion() {
        return commentsDeterminingExclusion;
    }

    /**
     * Sets the value of the commentsDeterminingExclusion property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommentsDeterminingExclusion }
     *     
     */
    public void setCommentsDeterminingExclusion(CommentsDeterminingExclusion value) {
        this.commentsDeterminingExclusion = value;
    }

    /**
     * Gets the value of the unitValues property.
     * 
     * @return
     *     possible object is
     *     {@link UnitValues }
     *     
     */
    public UnitValues getUnitValues() {
        return unitValues;
    }

    /**
     * Sets the value of the unitValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitValues }
     *     
     */
    public void setUnitValues(UnitValues value) {
        this.unitValues = value;
    }

    /**
     * Gets the value of the analysis property.
     * 
     * @return
     *     possible object is
     *     {@link Analysis }
     *     
     */
    public Analysis getAnalysis() {
        return analysis;
    }

    /**
     * Sets the value of the analysis property.
     * 
     * @param value
     *     allowed object is
     *     {@link Analysis }
     *     
     */
    public void setAnalysis(Analysis value) {
        this.analysis = value;
    }

}