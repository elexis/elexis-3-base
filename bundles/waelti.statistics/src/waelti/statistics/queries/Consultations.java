package waelti.statistics.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import waelti.statistics.queries.annotations.GetProperty;
import waelti.statistics.queries.annotations.SetProperty;
import waelti.statistics.queries.providers.QueryContentProvider;
import waelti.statistics.queries.providers.QueryLabelProvider;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

/**
 * This class is responsible for retaining all information about costs per consultation in a given
 * time period and for specified cohorts. <br>
 * The resulting list contains following columns:
 * <ul>
 * <li>First year of birth of the cohort</li>
 * <li>Total costs of this cohort in the given time period</li>
 * <li>Number of consultations</li>
 * <li>Average costs</li>
 * </ul>
 * 
 * @author michael waelti
 * @see AbstractQuery
 */
public class Consultations extends AbstractTimeSeries {
	
	/** Query which loads all patients from the database */
	private Query patientQuery = new Query<Patient>(Patient.class);
	
	/**
	 * This value is the first birth cohort. The value is determined when iterating through all
	 * patients. The initial value is set to the actual year.
	 */
	private int firstCohort;
	
	/** The cohort's size. */
	private int cohortSize;
	
	/** The list containing all header strings for each column. */
	private List<String> header;
	
	private IProgressMonitor monitor;
	
	/**
	 * The size of this computation. Counted as the numbers of patients to be searched for data.
	 */
	private int size = 0;
	
	/** empty constructor */
	public Consultations(){
		super("Konsultationsauswertung");
		this.initHeader();
	}
	
	@Override
	protected void initData(){
		super.initData();
		this.cohortSize = 1;
	}
	
	private void initHeader(){
		this.header = new ArrayList<String>(7);
		this.header.add("Geburtsjahr");
		this.header.add("Anzahl F");
		this.header.add("Gesamtkosten F");
		this.header.add("Konsultationen F");
		this.header.add("Kosten/Konsultation F");
		this.header.add("Kosten/Patient F");
		this.header.add("Anzahl M");
		this.header.add("Gesamtkosten M");
		this.header.add("Konsultationen M");
		this.header.add("Kosten/Konsultation M");
		this.header.add("Kosten/Patient M");
	}
	
	@Override
	public List<String> getTableHeadings(){
		return this.header;
	}
	
	/**
	 * This method executes the query and creates the content which is then accessible through
	 * getContent.
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor){
		{
			this.monitor = monitor;
			this.createContent();
			this.initProvider();
			monitor.done();
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * Initializes the label and content providers. Called after creating the content.
	 */
	private void initProvider(){
		QueryContentProvider content = new QueryContentProvider(this.getList());
		QueryLabelProvider label = new QueryLabelProvider();
		this.setContentProvider(content);
		this.setLabelProvider(label);
	}
	
	/**
	 * This method queries the database and returns the results in an ArrayList with each entry in
	 * the list representing a cohort.
	 */
	private List<String[]> createContent(){
		
		Object[] patients = this.patientQuery.execute().toArray();
		this.size = patients.length; // required by BackgroundJob
		this.monitor.beginTask("querying database", this.size); // monitor
		
		this.initList(patients);
		
		this.calculateContent(patients);
		
		this.convertList();
		
		return QueryUtil.convertToString(this.getList());
	}
	
	private void convertList(){
		// all values are converted to doubles
		this.setList(QueryUtil.createCohorts(this.getList(), this.cohortSize));
		
		this.setList(QueryUtil.addAverage(this.getList(), 2, 3, 4));
		this.setList(QueryUtil.addAverage(this.getList(), 7, 8, 9));
		
		this.setList(QueryUtil.addAverage(this.getList(), 2, 1, 5));
		this.setList(QueryUtil.addAverage(this.getList(), 7, 6, 10));
		
		// consultation count:
		QueryUtil.convertDoubleToInteger(this.getList(), 3);
		QueryUtil.convertDoubleToInteger(this.getList(), 8);
		
		// patient count:
		QueryUtil.convertDoubleToInteger(this.getList(), 1);
		QueryUtil.convertDoubleToInteger(this.getList(), 6);
		
		QueryUtil.convertToCurrency(this.getList(), 2);
		QueryUtil.convertToCurrency(this.getList(), 4);
		QueryUtil.convertToCurrency(this.getList(), 5);
		QueryUtil.convertToCurrency(this.getList(), 7);
		QueryUtil.convertToCurrency(this.getList(), 9);
		QueryUtil.convertToCurrency(this.getList(), 10);
	}
	
	private void calculateContent(Object[] patients){
		for (int i = 0; i < patients.length; i++) {
			Patient patient = (Patient) patients[i];
			
			if (!this.validate_data(patient)) {
				continue;
			}
			
			int year = QueryUtil.extractYear(patient.getGeburtsdatum());
			if (year != 0) {
				
				if (this.handleCases(patient, year)) {
					this.increment_patient_counter(patient, year);
				}
			}
			
			this.monitor.worked(1); // monitor
		}
	}
	
	/**
	 * Determines the first birth year of all patients and initializes the list containing all
	 * information. The first year in the resulting list is determined by the modulo 5 of the oldest
	 * patients birth year.
	 */
	private void initList(Object[] patients){
		this.firstCohort = Calendar.getInstance().get(Calendar.YEAR);
		
		for (int i = 0; i < patients.length; i++) {
			int birthYear = QueryUtil.extractYear(((Patient) patients[i]).getGeburtsdatum());
			
			if (this.firstCohort > birthYear && birthYear > 0) {
				this.firstCohort = this.determineFirstYear(birthYear);
			}
		}
		
		// year, total costs (consultation), No of consultation, average costs
		this.setList(QueryUtil.initiateYears(this.firstCohort, 11));
	}
	
	/**
	 * Returns an integer with the property: integer % 5 == 0 && integer <= birthYear.
	 */
	private int determineFirstYear(int birthYear){
		if (birthYear % 5 == 0) {
			return birthYear;
		} else {
			int diff = birthYear % 5;
			assert ((birthYear - diff) % 5 == 0);
			return (birthYear - diff);
		}
	}
	
	private boolean handleCases(Patient patient, int birthYear){
		Fall[] faelle = patient.getFaelle();
		boolean male = this.determineSex(patient);
		boolean result = false;
		
		for (int i = 0; i < faelle.length; i++) {
			if (this.handleConsultation(faelle[i], birthYear, male)) {
				result = true;
			}
		}
		return result;
	}
	
	private boolean determineSex(Patient patient){
		return patient.getGeschlecht().equals("m");
	}
	
	/**
	 * returns true if the given date is in the period defined. Format: dd.mm.yyyy
	 */
	private boolean inPeriod(String date){
		
		Calendar fallDate = Calendar.getInstance();
		
		try {
			fallDate = QueryUtil.convertToCalendar(date);
		} catch (Exception e) {
			// TODO log
			e.printStackTrace();
		}
		
		return (fallDate.compareTo(this.getStartDate()) >= 0 && fallDate.compareTo(this
			.getEndDate()) <= 0);
	}
	
	private boolean handleConsultation(Fall fall, int birthYear, boolean male){
		Konsultation[] consultations = fall.getBehandlungen(false);
		boolean result = false;
		
		for (int i = 0; i < consultations.length; i++) {
			Konsultation cons = consultations[i];
			
			if (this.inPeriod(cons.getDatum())) {
				result = true;
				if (male) {
					this.add_male(birthYear, cons);
				} else {
					this.add_female(birthYear, cons);
				}
			}
		}
		return result;
	}
	
	private void increment_patient_counter(Patient patient, int year){
		int index = this.determineSex(patient) ? 6 : 1;
		this.getList().get(year - this.firstCohort)[index] =
			((Integer) this.getList().get(year - this.firstCohort)[index]) + 1;
	}
	
	private void add_male(int birthYear, Konsultation cons){
		this.getList().get(birthYear - this.firstCohort)[7] =
			(Integer) this.getList().get(birthYear - this.firstCohort)[7]
				+ ((Double) cons.getUmsatz()).intValue();
		
		this.getList().get(birthYear - this.firstCohort)[8] =
			(Integer) this.getList().get(birthYear - this.firstCohort)[8] + 1;
	}
	
	private void add_female(int birthYear, Konsultation cons){
		this.getList().get(birthYear - this.firstCohort)[2] =
			(Integer) this.getList().get(birthYear - this.firstCohort)[2]
				+ ((Double) cons.getUmsatz()).intValue();
		
		this.getList().get(birthYear - this.firstCohort)[3] =
			(Integer) this.getList().get(birthYear - this.firstCohort)[3] + 1;
	}
	
	private boolean validate_data(Patient patient){
		int birthYear = QueryUtil.extractYear(patient.getGeburtsdatum());
		int actualYear = Calendar.getInstance().get(Calendar.YEAR);
		
		boolean birth = birthYear <= actualYear ? true : false;
		
		boolean gender = patient.getGeschlecht().equals("m") || patient.getGeschlecht().equals("w");
		
		boolean result = birth && gender;
		
		if (!birth) {
			this.showValidationError(patient);
		}
		return result;
	}
	
	private void showValidationError(Patient patient){
		String patientData =
			"Bei der Auswertung der Patientendaten ist ein "
				+ "Fehler aufgetreten.\nDieser Patient wird in der Auswertung "
				+ "nicht berücksichtigt.\n\nFolgender Patient hat ein "
				+ "Geburtsdatum in der Zukunft:\n\n";
		patientData += patient.getLabel();
		
		SWTHelper.showError("Fehlerhafte Patientendaten", patientData);
	}
	
	@Override
	public int getSize(){
		// TODO determine size
		return 0;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getDescription(){
		
		return ("Erstellt eine Liste mit der Anzahl an Konsultationen, den totalen "
			+ "sowie den durchschnittlichen Kosten pro Konsultation. Die "
			+ "Kohortengrösse ist definiert als die Anzahl Jahrgänge, die in einer "
			+ "Kohorte zusammengefasst werden sollen.");
	}
	
	/** {@inheritDoc} */
	@Override
	public String getTitle(){
		return "Konsultationsauswertung";
	}
	
	/* -------------------------- meta data --------------------------------- */
	/*
	 * The following methods are getter and setter pairs which describe all fields which can be
	 * changed by the user. Each pair is annotated since all information is gained by reflection
	 * with the java reflection framework.
	 */
	
	@GetProperty(value = "Kohortengrösse", index = 2)
	public String getCohortSize(){
		return "" + this.cohortSize;
	}
	
	@SetProperty("Kohortengrösse")
	public void setCohortSize(String cohortSize) throws SetDataException{
		Integer size = new Integer(1);
		try {
			size = new Integer(cohortSize);
			if (size < 1) {
				throw new Exception(); // size must be greater or equal 1
			}
			this.cohortSize = size;
		} catch (Exception e) {
			throw new SetDataException("Kohortengrösse muss eine positive Ganzzahl sein.");
		}
		
	}
}
