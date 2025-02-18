//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für processingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="processingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transport" type="{http://www.forum-datenaustausch.ch/invoice}transportType"/>
 *         &lt;element name="instructions" type="{http://www.forum-datenaustausch.ch/invoice}instructionsType" minOccurs="0"/>
 *         &lt;element name="demand" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="tc_demand_id" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="tc_token" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_100" />
 *                 &lt;attribute name="insurance_demand_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="insurance_demand_id" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="print_at_intermediate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="print_copy_to_guarantor" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processingType", propOrder = {
    "transport",
    "instructions",
    "demand"
})
public class ProcessingType {

    @XmlElement(required = true)
    protected TransportType transport;
    protected InstructionsType instructions;
    protected ProcessingType.Demand demand;
    @XmlAttribute(name = "print_at_intermediate")
    protected Boolean printAtIntermediate;
    @XmlAttribute(name = "print_copy_to_guarantor")
    protected Boolean printCopyToGuarantor;

    /**
     * Ruft den Wert der transport-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportType }
     *     
     */
    public TransportType getTransport() {
        return transport;
    }

    /**
     * Legt den Wert der transport-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportType }
     *     
     */
    public void setTransport(TransportType value) {
        this.transport = value;
    }

    /**
     * Ruft den Wert der instructions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InstructionsType }
     *     
     */
    public InstructionsType getInstructions() {
        return instructions;
    }

    /**
     * Legt den Wert der instructions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InstructionsType }
     *     
     */
    public void setInstructions(InstructionsType value) {
        this.instructions = value;
    }

    /**
     * Ruft den Wert der demand-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProcessingType.Demand }
     *     
     */
    public ProcessingType.Demand getDemand() {
        return demand;
    }

    /**
     * Legt den Wert der demand-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessingType.Demand }
     *     
     */
    public void setDemand(ProcessingType.Demand value) {
        this.demand = value;
    }

    /**
     * Ruft den Wert der printAtIntermediate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrintAtIntermediate() {
        if (printAtIntermediate == null) {
            return false;
        } else {
            return printAtIntermediate;
        }
    }

    /**
     * Legt den Wert der printAtIntermediate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrintAtIntermediate(Boolean value) {
        this.printAtIntermediate = value;
    }

    /**
     * Ruft den Wert der printCopyToGuarantor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrintCopyToGuarantor() {
        if (printCopyToGuarantor == null) {
            return false;
        } else {
            return printCopyToGuarantor;
        }
    }

    /**
     * Legt den Wert der printCopyToGuarantor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrintCopyToGuarantor(Boolean value) {
        this.printCopyToGuarantor = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="tc_demand_id" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="tc_token" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_100" />
     *       &lt;attribute name="insurance_demand_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *       &lt;attribute name="insurance_demand_id" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = StringUtils.EMPTY)
    public static class Demand {

        @XmlAttribute(name = "tc_demand_id", required = true)
        protected long tcDemandId;
        @XmlAttribute(name = "tc_token", required = true)
        protected String tcToken;
        @XmlAttribute(name = "insurance_demand_date", required = true)
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar insuranceDemandDate;
        @XmlAttribute(name = "insurance_demand_id")
        protected String insuranceDemandId;

        /**
         * Ruft den Wert der tcDemandId-Eigenschaft ab.
         * 
         */
        public long getTcDemandId() {
            return tcDemandId;
        }

        /**
         * Legt den Wert der tcDemandId-Eigenschaft fest.
         * 
         */
        public void setTcDemandId(long value) {
            this.tcDemandId = value;
        }

        /**
         * Ruft den Wert der tcToken-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTcToken() {
            return tcToken;
        }

        /**
         * Legt den Wert der tcToken-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTcToken(String value) {
            this.tcToken = value;
        }

        /**
         * Ruft den Wert der insuranceDemandDate-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getInsuranceDemandDate() {
            return insuranceDemandDate;
        }

        /**
         * Legt den Wert der insuranceDemandDate-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setInsuranceDemandDate(XMLGregorianCalendar value) {
            this.insuranceDemandDate = value;
        }

        /**
         * Ruft den Wert der insuranceDemandId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInsuranceDemandId() {
            return insuranceDemandId;
        }

        /**
         * Legt den Wert der insuranceDemandId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInsuranceDemandId(String value) {
            this.insuranceDemandId = value;
        }

    }

}
