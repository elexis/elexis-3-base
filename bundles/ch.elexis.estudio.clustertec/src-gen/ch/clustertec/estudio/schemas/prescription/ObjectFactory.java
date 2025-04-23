//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.clustertec.estudio.schemas.prescription package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.clustertec.estudio.schemas.prescription
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeliveryAddress }
     * 
     */
    public DeliveryAddress createDeliveryAddress() {
        return new DeliveryAddress();
    }

    /**
     * Create an instance of {@link AddressComplexType }
     * 
     */
    public AddressComplexType createAddressComplexType() {
        return new AddressComplexType();
    }

    /**
     * Create an instance of {@link BillingAddress }
     * 
     */
    public BillingAddress createBillingAddress() {
        return new BillingAddress();
    }

    /**
     * Create an instance of {@link PrescriptorAddress }
     * 
     */
    public PrescriptorAddress createPrescriptorAddress() {
        return new PrescriptorAddress();
    }

    /**
     * Create an instance of {@link PatientAddress }
     * 
     */
    public PatientAddress createPatientAddress() {
        return new PatientAddress();
    }

    /**
     * Create an instance of {@link Posology }
     * 
     */
    public Posology createPosology() {
        return new Posology();
    }

    /**
     * Create an instance of {@link Insurance }
     * 
     */
    public Insurance createInsurance() {
        return new Insurance();
    }

    /**
     * Create an instance of {@link Product }
     * 
     */
    public Product createProduct() {
        return new Product();
    }

    /**
     * Create an instance of {@link Interaction }
     * 
     */
    public Interaction createInteraction() {
        return new Interaction();
    }

    /**
     * Create an instance of {@link Prescription }
     * 
     */
    public Prescription createPrescription() {
        return new Prescription();
    }

}
