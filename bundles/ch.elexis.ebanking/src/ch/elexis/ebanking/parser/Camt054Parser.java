package ch.elexis.ebanking.parser;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.commons.lang3.StringUtils;

import camt.AccountNotification12;
import camt.ActiveOrHistoricCurrencyAndAmount;
import camt.CreditorReferenceInformation2;
import camt.DateAndDateTimeChoice;
import camt.Document;
import camt.EntryDetails7;
import camt.EntryTransaction8;
import camt.ObjectFactory;
import camt.RemittanceInformation11;
import camt.ReportEntry8;
import camt.StructuredRemittanceInformation13;
import camt.TransactionDates2;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class Camt054Parser {
	/**
	 * Parse a CAMT.054 formatted bank statement from a given input stream.
	 *
	 * @param inputStream input stream containing the CAMT.054 formatted bank
	 *                    statement
	 * @return document holding CAMT.054 parsed bank statement
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Document parse(InputStream inputStream) throws Camet054Exception {
		try {
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
			XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
			XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
			Unmarshaller um = jc.createUnmarshaller();
			return ((JAXBElement<Document>) um.unmarshal(xr)).getValue();
		} catch (Exception e) {
			throw new Camet054Exception("parse error", e); //$NON-NLS-1$
		}
	}

	class XMLReaderWithoutNamespace extends StreamReaderDelegate {
		public XMLReaderWithoutNamespace(XMLStreamReader reader) {
			super(reader);
		}

		@Override
		public String getAttributeNamespace(int arg0) {
			return StringUtils.EMPTY;
		}

		@Override
		public String getNamespaceURI() {
			return "urn:iso:std:iso:20022:tech:xsd:camt.054.001.06"; //$NON-NLS-1$
		}
	}

	public List<Camt054Record> parseRecords(InputStream inputStream) throws Camet054Exception {
		List<Camt054Record> records = new ArrayList<>();
		Document doc = parse(inputStream);
		List<AccountNotification12> notifications = doc.getBkToCstmrDbtCdtNtfctn().getNtfctn();

		for (AccountNotification12 accountNotification12 : notifications) {

			// B- LEVEL
			// Erstellungsdatum und -zeit des Kontoauszugs
			XMLGregorianCalendar credDate = accountNotification12.getCreDtTm();

			// C-LEVEL
			List<ReportEntry8> reportEntry8s = accountNotification12.getNtry();
			for (ReportEntry8 reportEntry8 : reportEntry8s) {

				// Betrag und Währung der ESR Sammelgutschrift
				ActiveOrHistoricCurrencyAndAmount entryAmt = reportEntry8.getAmt();

				// Entspricht dem Buchungsdatum
				DateAndDateTimeChoice bookingDate = reportEntry8.getBookgDt();

				// Entspricht dem Gutschriftdatum
				DateAndDateTimeChoice valDate = reportEntry8.getValDt();

				// RvslInd Optional Sofern es sich um eine ESR-Storno handelt, wird «true»
				// geliefert, sonst «false» oder Element nicht mitliefern.
				boolean storno = Boolean.TRUE.equals(reportEntry8.isRvslInd());

				// optional field for teilnehmernummer
				String esrTn = reportEntry8.getNtryRef();

				// D-LEVEL
				List<EntryDetails7> entryDetails7s = reportEntry8.getNtryDtls();
				for (EntryDetails7 entryDetails7 : entryDetails7s) {
					List<EntryTransaction8> entryTransaction8s = entryDetails7.getTxDtls();
					for (EntryTransaction8 entryTransaction8 : entryTransaction8s) {
						ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = entryTransaction8
								.getAmt();

						// wert
						BigDecimal amount = activeOrHistoricCurrencyAndAmount.getValue();
						RemittanceInformation11 remittanceInformation11 = entryTransaction8.getRmtInf();
						if (remittanceInformation11 != null) {
							List<StructuredRemittanceInformation13> structuredRemittanceInformation13s = remittanceInformation11
									.getStrd();

							// Aufgabedatum
							XMLGregorianCalendar readDate = null;
							TransactionDates2 transactionDates2 = entryTransaction8.getRltdDts();
							if (transactionDates2 != null) {
								readDate = transactionDates2.getAccptncDtTm();
							}
							if (readDate == null) {
								// if readdate is not set we use the booking date if possible
								readDate = bookingDate != null ? bookingDate.getDt() : null;
							}

							for (StructuredRemittanceInformation13 structuredRemittanceInformation13 : structuredRemittanceInformation13s) {
								CreditorReferenceInformation2 creditorReferenceInformation2 = structuredRemittanceInformation13
										.getCdtrRefInf();
								// ESR-Referenznummer oder Creditor Reference nach ISO11649
								String ref = creditorReferenceInformation2 != null
										? creditorReferenceInformation2.getRef()
										: null;
								records.add(new Camt054Record(storno ? "005" : "002", //$NON-NLS-1$ //$NON-NLS-2$
										amount != null ? amount.movePointRight(2).toString().replaceAll("[\\.,]", //$NON-NLS-1$
												StringUtils.EMPTY) : StringUtils.EMPTY,
										ref, esrTn, readDate != null ? readDate.toGregorianCalendar().getTime() : null,
										bookingDate != null ? bookingDate.getDt().toGregorianCalendar().getTime()
												: null,
										valDate != null ? valDate.getDt().toGregorianCalendar().getTime() : null));
							}
						}
					}
				}

				// sammelgutschrift
				records.add(new Camt054Record("999", //$NON-NLS-1$
						entryAmt != null ? entryAmt.getValue().movePointRight(2).toString().replaceAll("[\\.,]", //$NON-NLS-1$
								StringUtils.EMPTY) : null,
						null, esrTn, credDate.toGregorianCalendar().getTime(), credDate.toGregorianCalendar().getTime(),
						credDate.toGregorianCalendar().getTime()));
			}

		}

		return records;
	}

}
