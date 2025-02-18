//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package org.unece.cefact.namespaces.standardbusinessdocumentheader;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BusinessService complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BusinessService"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BusinessServiceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ServiceTransaction" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ServiceTransaction" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessService", propOrder = {
    "businessServiceName",
    "serviceTransaction"
})
public class BusinessService {

    @XmlElement(name = "BusinessServiceName")
    protected String businessServiceName;
    @XmlElement(name = "ServiceTransaction")
    protected ServiceTransaction serviceTransaction;

    /**
     * Ruft den Wert der businessServiceName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessServiceName() {
        return businessServiceName;
    }

    /**
     * Legt den Wert der businessServiceName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessServiceName(String value) {
        this.businessServiceName = value;
    }

    /**
     * Ruft den Wert der serviceTransaction-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceTransaction }
     *     
     */
    public ServiceTransaction getServiceTransaction() {
        return serviceTransaction;
    }

    /**
     * Legt den Wert der serviceTransaction-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceTransaction }
     *     
     */
    public void setServiceTransaction(ServiceTransaction value) {
        this.serviceTransaction = value;
    }

}
