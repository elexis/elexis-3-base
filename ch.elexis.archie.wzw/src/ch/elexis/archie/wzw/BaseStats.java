package ch.elexis.archie.wzw;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.TimeTool;
import ch.unibe.iam.scg.archie.annotations.GetProperty;
import ch.unibe.iam.scg.archie.annotations.SetProperty;
import ch.unibe.iam.scg.archie.model.AbstractTimeSeries;
import ch.unibe.iam.scg.archie.ui.widgets.WidgetTypes;

public abstract class BaseStats extends AbstractTimeSeries {
	protected String desc;
	protected String[] headings;
	private String dateMethod = "Rechnungsdatum";
	private boolean bOnlyActiveMandator;
	protected int clicksPerRound;
	protected int HUGE_NUMBER = 1000000;
	
	public BaseStats(String name, String desc, String[] headings){
		super(name);
		this.headings = headings;
		this.desc = desc;
	}
	
	@GetProperty(name = "Nur aktueller Mandant", widgetType = WidgetTypes.BUTTON_CHECKBOX, index = 1)
	public boolean getOnlyActiveMandator(){
		return bOnlyActiveMandator;
	}
	
	@SetProperty(name = "Nur aktueller Mandant", index = 1)
	public void setOnlyActiveMandator(boolean val){
		bOnlyActiveMandator = val;
	}
	
	@GetProperty(widgetType = WidgetTypes.COMBO, description = "Interpretation des Datums", index = 1, items = {
		"Rechnungsdatum", "Zahlungsdatum", "Konsultationsdatum"
	}, name = "Datum-Typ")
	public String getDateType(){
		return dateMethod;
	}
	
	@SetProperty(index = 1, name = "Datum-Typ")
	public void setDateType(String dT){
		dateMethod = dT;
	}
	
	@Override
	public String getDescription(){
		return desc;
	}
	
	@Override
	protected List<String> createHeadings(){
		return Arrays.asList(headings);
	}
	
	protected List<Konsultation> getConses(final IProgressMonitor monitor){
		HUGE_NUMBER = 1000000;
		monitor.beginTask("Sammle Konsultationsdaten ", HUGE_NUMBER);
		monitor.subTask("Datenbankabfrage und PostQueryFilters");
		Query<Konsultation> qbe = new Query<Konsultation>(Konsultation.class);
		String dateFrom =
			new TimeTool(getStartDate().getTime().getTime()).toString(TimeTool.DATE_COMPACT);
		String dateUntil =
			new TimeTool(getEndDate().getTimeInMillis()).toString(TimeTool.DATE_COMPACT);
		
		final TimeTool from = new TimeTool(dateFrom);
		final TimeTool to = new TimeTool(dateUntil);
		
		if (getDateType().equals("Konsultationsdatum")) {
			qbe.addPostQueryFilter(new IFilter() {
				@Override
				public boolean select(Object element){
					Konsultation k = (Konsultation) element;
					monitor.subTask(k.getDatum());
					monitor.worked(1);
					HUGE_NUMBER--;
					
					TimeTool konsDatum = new TimeTool(k.getDatum());
					return konsDatum.isAfterOrEqual(from) && konsDatum.isBeforeOrEqual(to);
				}
			});
		} else if (getDateType().equals("Rechnungsdatum")) {
			qbe.add(Konsultation.FLD_BILL_ID, "NOT", null);
			qbe.addPostQueryFilter(new IFilter() {
				@Override
				public boolean select(Object element){
					Konsultation k = (Konsultation) element;
					monitor.subTask(k.getDatum());
					monitor.worked(1);
					HUGE_NUMBER--;
					
					Rechnung rn = k.getRechnung();
					TimeTool rndate = new TimeTool(rn.getDatumRn());
					return rndate.isAfterOrEqual(from) && rndate.isBeforeOrEqual(to);
				}
			});
		} else if (getDateType().equals("Zahlungsdatum")) {
			qbe.add(Konsultation.FLD_BILL_ID, "NOT", null);
			qbe.addPostQueryFilter(new IFilter() {
				@Override
				public boolean select(Object element){
					Konsultation k = (Konsultation) element;
					monitor.subTask(k.getLabel());
					monitor.worked(1);
					HUGE_NUMBER--;
					
					Rechnung rn = k.getRechnung();
					if (rn.getStatus() == RnStatus.BEZAHLT
						|| rn.getStatus() == RnStatus.ZUVIEL_BEZAHLT) {
						TimeTool rndate = new TimeTool(rn.getDatumRn());
						return rndate.isAfterOrEqual(from) && rndate.isBeforeOrEqual(to);
					}
					return false;
				}
			});
			
		}
		if (bOnlyActiveMandator) {
			qbe.add(Konsultation.FLD_MANDATOR_ID, Query.EQUALS, CoreHub.actMandant.getId());
		}
		return qbe.execute();
	}
	
}
