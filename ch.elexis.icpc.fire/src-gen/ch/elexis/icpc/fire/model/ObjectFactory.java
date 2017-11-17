//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.17 um 10:56:23 AM CET 
//


package ch.elexis.icpc.fire.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.elexis.icpc.fire.model package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.elexis.icpc.fire.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Report }
     * 
     */
    public Report createReport() {
        return new Report();
    }

    /**
     * Create an instance of {@link TConsultation }
     * 
     */
    public TConsultation createTConsultation() {
        return new TConsultation();
    }

    /**
     * Create an instance of {@link Report.Consultations }
     * 
     */
    public Report.Consultations createReportConsultations() {
        return new Report.Consultations();
    }

    /**
     * Create an instance of {@link Report.Patients }
     * 
     */
    public Report.Patients createReportPatients() {
        return new Report.Patients();
    }

    /**
     * Create an instance of {@link Report.Doctors }
     * 
     */
    public Report.Doctors createReportDoctors() {
        return new Report.Doctors();
    }

    /**
     * Create an instance of {@link TStatus }
     * 
     */
    public TStatus createTStatus() {
        return new TStatus();
    }

    /**
     * Create an instance of {@link TPatient }
     * 
     */
    public TPatient createTPatient() {
        return new TPatient();
    }

    /**
     * Create an instance of {@link TVital }
     * 
     */
    public TVital createTVital() {
        return new TVital();
    }

    /**
     * Create an instance of {@link TLabor }
     * 
     */
    public TLabor createTLabor() {
        return new TLabor();
    }

    /**
     * Create an instance of {@link TDoctor }
     * 
     */
    public TDoctor createTDoctor() {
        return new TDoctor();
    }

    /**
     * Create an instance of {@link TMedi }
     * 
     */
    public TMedi createTMedi() {
        return new TMedi();
    }

    /**
     * Create an instance of {@link TDiagnose }
     * 
     */
    public TDiagnose createTDiagnose() {
        return new TDiagnose();
    }

    /**
     * Create an instance of {@link TConsultation.Diagnoses }
     * 
     */
    public TConsultation.Diagnoses createTConsultationDiagnoses() {
        return new TConsultation.Diagnoses();
    }

    /**
     * Create an instance of {@link TConsultation.Labors }
     * 
     */
    public TConsultation.Labors createTConsultationLabors() {
        return new TConsultation.Labors();
    }

    /**
     * Create an instance of {@link TConsultation.Medis }
     * 
     */
    public TConsultation.Medis createTConsultationMedis() {
        return new TConsultation.Medis();
    }

}
