package waelti.statistics.queries;

import java.util.Calendar;

import waelti.statistics.queries.annotations.GetProperty;
import waelti.statistics.queries.annotations.SetProperty;

/**
 * This class implements the basic features a query with a time series needs. For a description of
 * the requirements for a query see AbstractQuery.
 * 
 * @author michael waelti
 * @see AbstractQuery
 */
public abstract class AbstractTimeSeries extends AbstractQuery {
	
	/**
	 * The first date of the calculation period. Default value is the 1.1. of the actual year.
	 */
	private Calendar startDate = null;
	/**
	 * The last date of the calculation period. Default value is the 31.12. of the actual year.
	 */
	private Calendar endDate = null;
	
	public AbstractTimeSeries(String name){
		super(name);
		this.initData();
	}
	
	/**
	 * Initializes the standard data for this query. The standard is the whole actual year.
	 */
	protected void initData(){
		this.setStartDate(Calendar.getInstance());
		this.getStartDate().set(getStartDate().get(Calendar.YEAR), Calendar.JANUARY, 1);
		
		this.setEndDate(Calendar.getInstance());
		this.getEndDate().set(this.getEndDate().get(Calendar.YEAR), Calendar.DECEMBER, 31);
		
	}
	
	public void setStartDate(Calendar startDate){
		this.startDate = startDate;
	}
	
	public Calendar getStartDate(){
		return startDate;
	}
	
	public void setEndDate(Calendar endDate){
		this.endDate = endDate;
	}
	
	public Calendar getEndDate(){
		return endDate;
	}
	
	/** Get the start date of this query. Inclusive. */
	@GetProperty(value = "Anfangsdatum", index = 0)
	public String metaGetStartDate(){
		return QueryUtil.convertFromCalendar(this.getStartDate());
	}
	
	/*------------------------meta accessor methods---------------------------*/
	/**
	 * Set the start date of this query. Inclusive the given date. Format of the string has to be
	 * d[d].m[m].yyyy
	 * 
	 * @throws SetDataException
	 */
	@SetProperty(value = "Anfangsdatum", index = 0)
	public void metaSetStartDate(String startDate) throws SetDataException{
		Calendar cal;
		try {
			cal = QueryUtil.convertToCalendar(startDate);
			cal.get(Calendar.DAY_OF_MONTH); // these throw IllegalArgument...
			cal.get(Calendar.MONTH);
			cal.get(Calendar.YEAR);
		} catch (NumberFormatException e) { // converting failure
			throw new SetDataException("Anfangsdatum nicht im richtigen Format. "
				+ "Bitte in folgendem Format angeben: dd.mm.yyy");
		} catch (IllegalArgumentException e) { // illegal date
			throw new SetDataException("Das Anfangsdatum ist kein valides Datum.");
		}
		this.setStartDate(cal);
	}
	
	/** Get the end date of this query. Inclusive. */
	@GetProperty(value = "Enddatum", index = 1)
	public String metaGetEndDate(){
		return QueryUtil.convertFromCalendar(this.getEndDate());
	}
	
	/**
	 * Set the end date of this query. Inclusive the given date. The string has to be in this
	 * format: d[d].m[m].yyyy
	 * 
	 * @throws SetDataException
	 */
	@SetProperty(value = "Enddatum", index = 1)
	public void metaSetEndDate(String endDate) throws SetDataException{
		Calendar cal;
		try {
			cal = QueryUtil.convertToCalendar(endDate);
			cal.get(Calendar.DAY_OF_MONTH);// these throw IllegalArgument...
			cal.get(Calendar.MONTH);
			cal.get(Calendar.YEAR);
		} catch (NumberFormatException e) { // converting failure
			throw new SetDataException("Enddatum nicht im richtigen Format. "
				+ "Bitte in folgendem Format angeben: dd.mm.yyyy");
		} catch (IllegalArgumentException e) { // illegal date
			throw new SetDataException("Das Enddatum ist kein gültiges Datum. "
				+ "Bitte geben Sie ein gültiges Datum ein.");
		}
		if (cal.compareTo(this.getStartDate()) < 0) {
			throw new SetDataException("Enddatum vor Anfangsdatum. Bitte ändern Sie das Start- "
				+ "oder Enddatum der Auswertung.");
		}
		this.setEndDate(cal);
	}
	
}
