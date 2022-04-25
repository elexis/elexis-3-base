package waelti.statistics.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import waelti.statistics.queries.providers.QueryContentProvider;
import waelti.statistics.queries.providers.QueryLabelProvider;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.Money;

public class PatientCosts extends AbstractTimeSeries {

	private List<String> headings;

	/** Query which loads all patients from the database */
	private final Query<Patient> patientQuery = new Query<Patient>(Patient.class);

	private IProgressMonitor monitor;

	private int size = 0;

	public PatientCosts() {
		super("Kosten pro Patient");
		this.initHeading();
	}

	private void initHeading() {
		this.headings = new ArrayList<String>();
		this.headings.add("Patient");
		this.headings.add("Gesamtkosten");
	}

	@Override
	public IStatus execute(IProgressMonitor monitor) {
		this.monitor = monitor;
		this.createContent();
		this.initProvider();
		this.monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * Initializes the label and content providers. Called after creating the
	 * content.
	 */
	private void initProvider() {
		QueryContentProvider content = new QueryContentProvider(this.getList());
		QueryLabelProvider label = new QueryLabelProvider();
		this.setContentProvider(content);
		this.setLabelProvider(label);
	}

	private void createContent() {
		List<Object[]> list = new ArrayList<Object[]>();

		Object[] patients = this.patientQuery.execute().toArray();
		this.size = patients.length;
		this.monitor.beginTask("querying database", this.size); // monitoring

		for (Object pat : patients) {
			Patient patient = (Patient) pat;
			this.monitor.worked(1); // monitoring
			Money costs = this.handleCases(patient);

			if (costs.getCents() != 0) { // patients without costs are not shown
				Object[] row = { patient.getLabel(), costs };
				list.add(row);
			}
		}

		this.setList(list);
	}

	private Money handleCases(Patient patient) {
		double costs = 0;
		Fall[] faelle = patient.getFaelle();

		for (Fall fall : faelle) {
			costs += this.handleConsultation(fall);
		}
		costs /= 100; // not in cents
		return new Money(costs);
	}

	private double handleConsultation(Fall fall) {
		double costs = 0;
		Konsultation[] cons = fall.getBehandlungen(false);

		for (Konsultation konsultation : cons) {
			if (this.inPeriod(konsultation.getDatum())) {
				costs += konsultation.getUmsatz();
			}
		}

		return costs;
	}

	/**
	 * returns true if the given Fall is in the period defined. Format: dd.mm.yyyy
	 */
	private boolean inPeriod(String date) {

		Calendar givenDate = Calendar.getInstance();

		try {
			givenDate = QueryUtil.convertToCalendar(date);
		} catch (Exception e) {
			// TODO log
			e.printStackTrace();
		}

		return (givenDate.compareTo(this.getStartDate()) >= 0 && givenDate.compareTo(this.getEndDate()) <= 0);
	}

	@Override
	public String getDescription() {
		return "Erstellt eine Liste mit allen Patienten, die in der gegebenen "
				+ "Zeitspanne eine Konsultation hatten und die gesamten "
				+ "Kosten, die bei diesen Patienten anfielen.";
	}

	@Override
	public List<String> getTableHeadings() {
		return this.headings;
	}

	@Override
	public String getTitle() {
		return "Kosten pro Patient";
	}

	@Override
	public String getHeader() {
		String str = super.getHeader();
		str += "\nAuswertungszeitraum: " + QueryUtil.convertFromCalendar(this.getStartDate());
		str += " - " + QueryUtil.convertFromCalendar(this.getEndDate());

		return str;
	}

	@Override
	public int getSize() {
		return this.size;
	}

}
