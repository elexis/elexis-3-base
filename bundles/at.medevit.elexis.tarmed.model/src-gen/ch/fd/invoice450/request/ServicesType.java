//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für servicesType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="servicesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="service_ex" type="{http://www.forum-datenaustausch.ch/invoice}serviceExType"/>
 *         &lt;element name="service" type="{http://www.forum-datenaustausch.ch/invoice}serviceType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servicesType", propOrder = {
    "serviceExOrService"
})
public class ServicesType {

    @XmlElements({
        @XmlElement(name = "service_ex", type = ServiceExType.class),
        @XmlElement(name = "service", type = ServiceType.class)
    })
    protected List<Object> serviceExOrService;

    /**
     * Gets the value of the serviceExOrService property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceExOrService property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceExOrService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceExType }
     * {@link ServiceType }
     * 
     * 
     */
    public List<Object> getServiceExOrService() {
        if (serviceExOrService == null) {
            serviceExOrService = new ArrayList<Object>();
        }
        return this.serviceExOrService;
    }

}
