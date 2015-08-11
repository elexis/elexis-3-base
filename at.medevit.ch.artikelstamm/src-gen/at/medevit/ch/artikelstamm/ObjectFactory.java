//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.22 um 01:22:22 PM CEST 
//


package at.medevit.ch.artikelstamm;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.medevit.ch.artikelstamm package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.medevit.ch.artikelstamm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ARTIKELSTAMM }
     * 
     */
    public ARTIKELSTAMM createARTIKELSTAMM() {
        return new ARTIKELSTAMM();
    }

    /**
     * Create an instance of {@link ARTIKELSTAMM.ITEM }
     * 
     */
    public ARTIKELSTAMM.ITEM createARTIKELSTAMMITEM() {
        return new ARTIKELSTAMM.ITEM();
    }

    /**
     * Create an instance of {@link ARTIKELSTAMM.ITEM.COMP }
     * 
     */
    public ARTIKELSTAMM.ITEM.COMP createARTIKELSTAMMITEMCOMP() {
        return new ARTIKELSTAMM.ITEM.COMP();
    }

}
