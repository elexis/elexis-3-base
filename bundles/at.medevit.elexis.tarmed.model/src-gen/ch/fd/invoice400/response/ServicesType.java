//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for servicesType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servicesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="record_tarmed" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordTarmedType"/>
 *         &lt;element name="record_cantonal" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordCantonalType"/>
 *         &lt;element name="record_unclassified" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordUnclassifiedType"/>
 *         &lt;element name="record_lab" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordLabType"/>
 *         &lt;element name="record_migel" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordMigelType"/>
 *         &lt;element name="record_physio" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordPhysioType"/>
 *         &lt;element name="record_drug" type="{http://www.xmlData.ch/xmlInvoice/XSD}recordDrugType"/>
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
	"recordTarmedOrRecordCantonalOrRecordUnclassified"
})
public class ServicesType {
	
	@XmlElements({
		@XmlElement(name = "record_lab", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordLabType.class),
		@XmlElement(name = "record_unclassified", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordUnclassifiedType.class),
		@XmlElement(name = "record_tarmed", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordTarmedType.class),
		@XmlElement(name = "record_physio", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordPhysioType.class),
		@XmlElement(name = "record_drug", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordDrugType.class),
		@XmlElement(name = "record_cantonal", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordCantonalType.class),
		@XmlElement(name = "record_migel", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true, type = RecordMigelType.class)
	})
	protected List<Object> recordTarmedOrRecordCantonalOrRecordUnclassified;
	
	/**
	 * Gets the value of the recordTarmedOrRecordCantonalOrRecordUnclassified property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any
	 * modification you make to the returned list will be present inside the JAXB object. This is
	 * why there is not a <CODE>set</CODE> method for the
	 * recordTarmedOrRecordCantonalOrRecordUnclassified property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRecordTarmedOrRecordCantonalOrRecordUnclassified().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link RecordLabType }
	 * {@link RecordUnclassifiedType } {@link RecordTarmedType } {@link RecordPhysioType }
	 * {@link RecordDrugType } {@link RecordCantonalType } {@link RecordMigelType }
	 * 
	 * 
	 */
	public List<Object> getRecordTarmedOrRecordCantonalOrRecordUnclassified(){
		if (recordTarmedOrRecordCantonalOrRecordUnclassified == null) {
			recordTarmedOrRecordCantonalOrRecordUnclassified = new ArrayList<Object>();
		}
		return this.recordTarmedOrRecordCantonalOrRecordUnclassified;
	}
	
}
