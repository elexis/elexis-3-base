package ch.elexis.ebanking.parser;

import java.util.Date;

/**
 * Holds values from a Camt054 File
 * 
 * @author med1
 *
 */
public class Camt054Record {
	// wert
	private String amount;
	//ESR-Referenznummer oder Creditor Reference nach ISO11649
	private String reference;
	
	//Teilnehmernummer
	private String tn;
	
	private Date bookingDate;
	private Date valuDate;
	
	public Camt054Record(String amount, String reference, String tn, Date bookingDate,
		Date valueDate) throws Camet054Exception{
		super();
		this.amount = amount;
		this.reference = reference;
		this.tn = tn;
		this.bookingDate = bookingDate;
		this.valuDate = valueDate;
		
		validate();
	}
	
	private void validate() throws Camet054Exception{
		try {
			if (Integer.parseInt(amount) < 0) {
				throw new Camet054Exception("amount is negativ: " + amount);
			}
		} catch (NumberFormatException e) {
			throw new Camet054Exception("amount not valid", e);
		}
		
		if (reference == null || reference.length() != 27) {
			throw new Camet054Exception("reference is not valid: " + reference);
		}
		
		if (bookingDate == null || bookingDate.before(new Date(0))) {
			throw new Camet054Exception("booking date is not valid: " + bookingDate);
		}
		
		if (valuDate == null || valuDate.before(new Date(0))) {
			throw new Camet054Exception("valu date is not valid: " + valuDate);
		}
	}
	
	public String getAmount(){
		return amount;
	}
	
	public void setAmount(String amount){
		this.amount = amount;
	}
	
	public String getReference(){
		return reference;
	}
	
	public void setReference(String reference){
		this.reference = reference;
	}
	
	public String getTn(){
		return tn;
	}
	
	public void setTn(String tn){
		this.tn = tn;
	}
	
	public Date getBookingDate(){
		return bookingDate;
	}
	
	public Date getValuDate(){
		return valuDate;
	}
	
	public void setBookingDate(Date bookingDate){
		this.bookingDate = bookingDate;
	}
	
	public void setValuDate(Date valuDate){
		this.valuDate = valuDate;
	}
	
	@Override
	public String toString(){
		return "Camt054Record [amount=" + amount + ", reference=" + reference + ", tn=" + tn
			+ ", bookingDate=" + bookingDate + ", valuDate=" + valuDate + "]";
	}
}
