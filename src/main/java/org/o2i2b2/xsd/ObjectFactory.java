//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.24 at 09:58:25 PM EDT 
//


package org.o2i2b2.xsd;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.o2i2b2.xsd package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Oktousevalues_QNAME = new QName("", "Oktousevalues");
    private final static QName _DataType_QNAME = new QName("", "DataType");
    private final static QName _CreationDateTime_QNAME = new QName("", "CreationDateTime");
    private final static QName _TestName_QNAME = new QName("", "TestName");
    private final static QName _LowofLowValue_QNAME = new QName("", "LowofLowValue");
    private final static QName _Flagstouse_QNAME = new QName("", "Flagstouse");
    private final static QName _NormalUnits_QNAME = new QName("", "NormalUnits");
    private final static QName _TestID_QNAME = new QName("", "TestID");
    private final static QName _LowofHighValue_QNAME = new QName("", "LowofHighValue");
    private final static QName _HighofHighValue_QNAME = new QName("", "HighofHighValue");
    private final static QName _CodeType_QNAME = new QName("", "CodeType");
    private final static QName _EqualUnits_QNAME = new QName("", "EqualUnits");
    private final static QName _HighofLowValue_QNAME = new QName("", "HighofLowValue");
    private final static QName _Version_QNAME = new QName("", "Version");
    private final static QName _Loinc_QNAME = new QName("", "Loinc");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.o2i2b2.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LowofToxicValue }
     * 
     */
    public LowofToxicValue createLowofToxicValue() {
        return new LowofToxicValue();
    }

    /**
     * Create an instance of {@link ConvertingUnits }
     * 
     */
    public ConvertingUnits createConvertingUnits() {
        return new ConvertingUnits();
    }

    /**
     * Create an instance of {@link New }
     * 
     */
    public New createNew() {
        return new New();
    }

    /**
     * Create an instance of {@link CommentsDeterminingExclusion }
     * 
     */
    public CommentsDeterminingExclusion createCommentsDeterminingExclusion() {
        return new CommentsDeterminingExclusion();
    }

    /**
     * Create an instance of {@link EnumValues }
     * 
     */
    public EnumValues createEnumValues() {
        return new EnumValues();
    }

    /**
     * Create an instance of {@link Units }
     * 
     */
    public Units createUnits() {
        return new Units();
    }

    /**
     * Create an instance of {@link ExcludingUnits }
     * 
     */
    public ExcludingUnits createExcludingUnits() {
        return new ExcludingUnits();
    }

    /**
     * Create an instance of {@link UnitValues }
     * 
     */
    public UnitValues createUnitValues() {
        return new UnitValues();
    }

    /**
     * Create an instance of {@link MaxStringLength }
     * 
     */
    public MaxStringLength createMaxStringLength() {
        return new MaxStringLength();
    }

    /**
     * Create an instance of {@link MultiplyingFactor }
     * 
     */
    public MultiplyingFactor createMultiplyingFactor() {
        return new MultiplyingFactor();
    }

    /**
     * Create an instance of {@link Counts }
     * 
     */
    public Counts createCounts() {
        return new Counts();
    }

    /**
     * Create an instance of {@link Analysis }
     * 
     */
    public Analysis createAnalysis() {
        return new Analysis();
    }

    /**
     * Create an instance of {@link HighofToxicValue }
     * 
     */
    public HighofToxicValue createHighofToxicValue() {
        return new HighofToxicValue();
    }

    /**
     * Create an instance of {@link ValueMetadata }
     * 
     */
    public ValueMetadata createValueMetadata() {
        return new ValueMetadata();
    }

    /**
     * Create an instance of {@link Com }
     * 
     */
    public Com createCom() {
        return new Com();
    }

    /**
     * Create an instance of {@link Enums }
     * 
     */
    public Enums createEnums() {
        return new Enums();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Oktousevalues")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createOktousevalues(String value) {
        return new JAXBElement<String>(_Oktousevalues_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DataType")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createDataType(String value) {
        return new JAXBElement<String>(_DataType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CreationDateTime")
    public JAXBElement<String> createCreationDateTime(String value) {
        return new JAXBElement<String>(_CreationDateTime_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TestName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTestName(String value) {
        return new JAXBElement<String>(_TestName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "LowofLowValue")
    public JAXBElement<BigInteger> createLowofLowValue(BigInteger value) {
        return new JAXBElement<BigInteger>(_LowofLowValue_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Flagstouse")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFlagstouse(String value) {
        return new JAXBElement<String>(_Flagstouse_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "NormalUnits")
    public JAXBElement<String> createNormalUnits(String value) {
        return new JAXBElement<String>(_NormalUnits_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TestID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTestID(String value) {
        return new JAXBElement<String>(_TestID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "LowofHighValue")
    public JAXBElement<BigInteger> createLowofHighValue(BigInteger value) {
        return new JAXBElement<BigInteger>(_LowofHighValue_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "HighofHighValue")
    public JAXBElement<BigInteger> createHighofHighValue(BigInteger value) {
        return new JAXBElement<BigInteger>(_HighofHighValue_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CodeType")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCodeType(String value) {
        return new JAXBElement<String>(_CodeType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "EqualUnits")
    public JAXBElement<String> createEqualUnits(String value) {
        return new JAXBElement<String>(_EqualUnits_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "HighofLowValue")
    public JAXBElement<BigInteger> createHighofLowValue(BigInteger value) {
        return new JAXBElement<BigInteger>(_HighofLowValue_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Version")
    public JAXBElement<BigDecimal> createVersion(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Version_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Loinc")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLoinc(String value) {
        return new JAXBElement<String>(_Loinc_QNAME, String.class, null, value);
    }

}
