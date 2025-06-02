//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.09 um 01:08:44 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.clustertec.estudio.schemas.order package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.clustertec.estudio.schemas.order
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PatientOrder }
     * 
     */
    public PatientOrder createPatientOrder() {
        return new PatientOrder();
    }

    /**
     * Create an instance of {@link Product }
     * 
     */
    public Product createProduct() {
        return new Product();
    }

    /**
     * Create an instance of {@link PatientOrderB2C }
     * 
     */
    public PatientOrderB2C createPatientOrderB2C() {
        return new PatientOrderB2C();
    }

    /**
     * Create an instance of {@link Order }
     * 
     */
    public Order createOrder() {
        return new Order();
    }

    /**
     * Create an instance of {@link OrderResponse }
     * 
     */
    public OrderResponse createOrderResponse() {
        return new OrderResponse();
    }

    /**
     * Create an instance of {@link PatientOrders }
     * 
     */
    public PatientOrders createPatientOrders() {
        return new PatientOrders();
    }

}
