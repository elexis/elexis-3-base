
package ch.pharmedsolutions.www.apothekenservice;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the
 * https.www_pharmedsolutions_ch.apothekenservice package.
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
	 * derived classes for package: https.www_pharmedsolutions_ch.apothekenservice
	 *
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Apotheken }
	 *
	 */
	public Apotheken createApotheken() {
		return new Apotheken();
	}

	/**
	 * Create an instance of {@link ArrayOfApotheken }
	 *
	 */
	public ArrayOfApotheken createArrayOfApotheken() {
		return new ArrayOfApotheken();
	}

	/**
	 * Create an instance of {@link ApothekenRequest }
	 *
	 */
	public ApothekenRequest createApothekenRequest() {
		return new ApothekenRequest();
	}

	/**
	 * Create an instance of {@link Apotheke }
	 *
	 */
	public Apotheke createApotheke() {
		return new Apotheke();
	}

}
