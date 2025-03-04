
package ch.pharmedsolutions.www.rezeptserver;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the https.www_pharmedsolutions_ch.rezeptserver
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema
	 * derived classes for package: https.www_pharmedsolutions_ch.rezeptserver
	 *
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Prescription }
	 *
	 */
	public Prescription createPrescription() {
		return new Prescription();
	}

	/**
	 * Create an instance of {@link ArrayOfProduct }
	 *
	 */
	public ArrayOfProduct createArrayOfProduct() {
		return new ArrayOfProduct();
	}

	/**
	 * Create an instance of {@link Patient }
	 *
	 */
	public Patient createPatient() {
		return new Patient();
	}

	/**
	 * Create an instance of {@link PrescriptionResponse }
	 *
	 */
	public PrescriptionResponse createPrescriptionResponse() {
		return new PrescriptionResponse();
	}

	/**
	 * Create an instance of {@link Product }
	 *
	 */
	public Product createProduct() {
		return new Product();
	}

}
