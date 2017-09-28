package ch.elexis.ebanking.parser;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import camt.AccountIdentification4Choice;
import camt.AccountNotification12;
import camt.ActiveOrHistoricCurrencyAndAmount;
import camt.BankTransactionCodeStructure4;
import camt.BatchInformation2;
import camt.CashAccount25;
import camt.CreditorReferenceInformation2;
import camt.DateAndDateTimeChoice;
import camt.Document;
import camt.EntryDetails7;
import camt.EntryTransaction8;
import camt.ObjectFactory;
import camt.RemittanceInformation11;
import camt.ReportEntry8;
import camt.StructuredRemittanceInformation13;

public class Camt054Parser {
	/**
	 * Parse a CAMT.054 formatted bank statement from a given input stream.
	 *
	 * @param inputStream
	 *            input stream containing the CAMT.054 formatted bank statement
	 * @return document holding CAMT.054 parsed bank statement
	 * @throws JAXBException
	 */
	public Document parse(InputStream inputStream) throws Camet054Exception{
		try {
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
			XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
			XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
			Unmarshaller um = jc.createUnmarshaller();
			return ((JAXBElement<Document>) um.unmarshal(xr)).getValue();
		} catch (Exception e) {
			throw new Camet054Exception("parse error", e);
		}
	}
	
	class XMLReaderWithoutNamespace extends StreamReaderDelegate {
		public XMLReaderWithoutNamespace(XMLStreamReader reader){
			super(reader);
		}
		
		@Override
		public String getAttributeNamespace(int arg0){
			return "";
		}
		
		@Override
		public String getNamespaceURI(){
			return "urn:iso:std:iso:20022:tech:xsd:camt.054.001.06";
		}
	}
	
	public List<Camt054Record> parseRecords(InputStream inputStream) throws Camet054Exception{
		List<Camt054Record> records = new ArrayList<>();
		Document doc = parse(inputStream);
		List<AccountNotification12> notifications = doc.getBkToCstmrDbtCdtNtfctn().getNtfctn();
		for (AccountNotification12 accountNotification12 : notifications) {
			
			// B-LEVEL
			CashAccount25 cashAccount25 = accountNotification12.getAcct();
			AccountIdentification4Choice accountIdentification4Choice = cashAccount25.getId();
			//Entspricht dem Gutschriftskonto, nicht der Teilnehmernummer oder ESR- IBAN.
			String iban = accountIdentification4Choice.getIBAN();
			
			// C-LEVEL
			List<ReportEntry8> reportEntry8s = accountNotification12.getNtry();
			for (ReportEntry8 reportEntry8 : reportEntry8s) {
				
				//Betrag und Währung der ESR Sammelgutschrift
				ActiveOrHistoricCurrencyAndAmount entryAmt = reportEntry8.getAmt();
				
				//Entspricht dem Buchungsdatum
				DateAndDateTimeChoice bookingDate = reportEntry8.getBookgDt();
				
				//Entspricht dem Gutschriftdatum
				DateAndDateTimeChoice valDate = reportEntry8.getValDt();
				
				//RvslInd Optional Sofern es sich um eine ESR-Storno handelt, wird «true» geliefert, sonst «false» oder Element nicht mitliefern.
				boolean storno = Boolean.TRUE.equals(reportEntry8.isRvslInd());
				
				//TODO
				// BTC besteht aus 3 Feldern: Domain, Family und Sub-Family. Folgende Codes werden verwendet: Gutschrift: Domain = PMNT / Family = RCDT / Sub-Family = VCOM Storno: Domain = PMNT / Family = RCDT / Sub-Family = CAJT 
				BankTransactionCodeStructure4 bankTransactionCodeStructure4 =
					reportEntry8.getBkTxCd();
				
				//TODO optional field for teilnehmernummer ?
				String esrTn = reportEntry8.getNtryRef();
				
				//D-LEVEL
				List<EntryDetails7> entryDetails7s = reportEntry8.getNtryDtls();
				for (EntryDetails7 entryDetails7 : entryDetails7s) {
					BatchInformation2 batchInformation2 = entryDetails7.getBtch();
					List<EntryTransaction8> entryTransaction8s = entryDetails7.getTxDtls();
					for (EntryTransaction8 entryTransaction8 : entryTransaction8s) {
						ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount =
							entryTransaction8.getAmt();
						//waehrung
						String currency = activeOrHistoricCurrencyAndAmount.getCcy();
						// wert
						BigDecimal amount = activeOrHistoricCurrencyAndAmount.getValue();
						
						RemittanceInformation11 remittanceInformation11 =
							entryTransaction8.getRmtInf();
						List<StructuredRemittanceInformation13> structuredRemittanceInformation13s =
							remittanceInformation11.getStrd();
						
						for (StructuredRemittanceInformation13 structuredRemittanceInformation13 : structuredRemittanceInformation13s) {
							CreditorReferenceInformation2 creditorReferenceInformation2 =
								structuredRemittanceInformation13.getCdtrRefInf();
							
							//ESR-Referenznummer oder Creditor Reference nach ISO11649
							String ref = creditorReferenceInformation2.getRef();
							records.add(new Camt054Record(storno ? "005" : "002",
								amount.toString().replaceAll("[\\.,]", ""), ref, esrTn,
								bookingDate.getDt().toGregorianCalendar().getTime(),
								valDate.getDt().toGregorianCalendar().getTime()));
						}
					}
				}
				
				// sammelgutschrift
				records.add(new Camt054Record("999", entryAmt != null ? entryAmt.getValue().toString().replaceAll("[\\.,]", "") : null,
					null, esrTn, bookingDate.getDt().toGregorianCalendar().getTime(),
					valDate.getDt().toGregorianCalendar().getTime()));
			}
			
		}
		
		return records;
	}
	
}
