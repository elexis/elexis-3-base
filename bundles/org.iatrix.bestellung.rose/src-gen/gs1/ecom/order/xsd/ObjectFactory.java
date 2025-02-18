//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.order.xsd;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gs1.ecom.order.xsd._3 package. 
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

    private final static QName _OrderMessage_QNAME = new QName("urn:gs1:ecom:order:xsd:3", "orderMessage");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gs1.ecom.order.xsd._3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OrderMessageType }
     * 
     */
    public OrderMessageType createOrderMessageType() {
        return new OrderMessageType();
    }

    /**
     * Create an instance of {@link OrderLineItemDetailType }
     * 
     */
    public OrderLineItemDetailType createOrderLineItemDetailType() {
        return new OrderLineItemDetailType();
    }

    /**
     * Create an instance of {@link OrderLineItemType }
     * 
     */
    public OrderLineItemType createOrderLineItemType() {
        return new OrderLineItemType();
    }

    /**
     * Create an instance of {@link OrderPackagingInstructionsType }
     * 
     */
    public OrderPackagingInstructionsType createOrderPackagingInstructionsType() {
        return new OrderPackagingInstructionsType();
    }

    /**
     * Create an instance of {@link OrderType }
     * 
     */
    public OrderType createOrderType() {
        return new OrderType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderMessageType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OrderMessageType }{@code >}
     */
    @XmlElementDecl(namespace = "urn:gs1:ecom:order:xsd:3", name = "orderMessage")
    public JAXBElement<OrderMessageType> createOrderMessage(OrderMessageType value) {
        return new JAXBElement<OrderMessageType>(_OrderMessage_QNAME, OrderMessageType.class, null, value);
    }

}
