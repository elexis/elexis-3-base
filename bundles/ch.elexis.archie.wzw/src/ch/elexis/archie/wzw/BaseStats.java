package ch.elexis.archie.wzw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
	
	private List<IFilter> filters;
	
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
	
	protected List<IEncounter> getConses(final IProgressMonitor monitor){
		filters = new ArrayList<IFilter>();
		HUGE_NUMBER = 1000000;
		monitor.beginTask("Sammle Konsultationsdaten ", HUGE_NUMBER);
		monitor.subTask("Datenbankabfrage und PostQueryFilters");
		IQuery<IEncounter> query = CoreModelServiceHolder.get().getQuery(IEncounter.class);
		String dateFrom =
			new TimeTool(getStartDate().getTime().getTime()).toString(TimeTool.DATE_COMPACT);
		String dateUntil =
			new TimeTool(getEndDate().getTimeInMillis()).toString(TimeTool.DATE_COMPACT);
		
		final TimeTool from = new TimeTool(dateFrom);
		final TimeTool to = new TimeTool(dateUntil);
		
		if (getDateType().equals("Konsultationsdatum")) {
			query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.GREATER_OR_EQUAL,
				from.toLocalDate());
			query.and(ModelPackage.Literals.IENCOUNTER__DATE, COMPARATOR.LESS_OR_EQUAL,
				to.toLocalDate());
		} else if (getDateType().equals("Rechnungsdatum")) {
			query.and(ModelPackage.Literals.IENCOUNTER__INVOICE, COMPARATOR.NOT_EQUALS, null);
			filters.add(new IFilter() {
				@Override
				public boolean select(Object element){
					IEncounter k = (IEncounter) element;
					monitor.subTask(k.getDate().toString());
					monitor.worked(1);
					HUGE_NUMBER--;
					
					IInvoice rn = k.getInvoice();
					TimeTool rndate = new TimeTool(rn.getDate());
					return rndate.isAfterOrEqual(from) && rndate.isBeforeOrEqual(to);
				}
			});
		} else if (getDateType().equals("Zahlungsdatum")) {
			query.and(ModelPackage.Literals.IENCOUNTER__INVOICE, COMPARATOR.NOT_EQUALS, null);
			filters.add(new IFilter() {
				@Override
				public boolean select(Object element){
					IEncounter k = (IEncounter) element;
					monitor.subTask(k.getLabel());
					monitor.worked(1);
					HUGE_NUMBER--;
					
					IInvoice rn = k.getInvoice();
					if (rn.getState() == InvoiceState.PAID
						|| rn.getState() == InvoiceState.EXCESSIVE_PAYMENT) {
						TimeTool rndate = new TimeTool(rn.getDate());
						return rndate.isAfterOrEqual(from) && rndate.isBeforeOrEqual(to);
					}
					return false;
				}
			});
			
		}
		if (bOnlyActiveMandator) {
			query.and(ModelPackage.Literals.IENCOUNTER__MANDATOR, COMPARATOR.EQUALS,
				ContextServiceHolder.get().getActiveMandator().get());
		}
		List<IEncounter> ret = query.execute();
		if(!filters.isEmpty()) {
			for (IFilter filter : filters) {
				ret = ret.stream().filter(e -> filter.select(e)).collect(Collectors.toList());
			}
		}
		return ret;
	}
	
}
