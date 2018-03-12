package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import at.medevit.elexis.agenda.ui.model.Event;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.data.TerminUtil;
import ch.elexis.core.model.IPeriod;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class LoadEventsFunction extends AbstractBrowserFunction {
	
	private Gson gson;
	
	private Set<String> resources = new HashSet<String>();
	
	private ScriptingHelper scriptingHelper;
	
	public LoadEventsFunction(Browser browser, String name){
		super(browser, name);
		gson = new GsonBuilder().create();
		scriptingHelper = new ScriptingHelper(browser);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			LocalDate startDate = getDateArg(arguments[0]);
			LocalDate endDate = getDateArg(arguments[1]);
			
			List<IPeriod> periods = getPeriods(startDate, endDate);
			List<Event> events =
				periods.parallelStream().map(p -> Event.of(p)).collect(Collectors.toList());
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run(){
					// update calendar height
					updateCalendarHeight();
					scriptingHelper.scrollToNow();
				}
			});
			return gson.toJson(events);
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
	}
	
	public void addResource(String resource) {
		this.resources.add(resource);
	}
	
	public void removeResource(String resource){
		this.resources.remove(resource);
	}
	
	public void setResources(List<String> resources){
		this.resources.clear();
		this.resources.addAll(resources);
	}
	
	private List<IPeriod> getPeriods(LocalDate from, LocalDate to)
		throws IllegalStateException{
		
		for (String resource : resources) {
			LocalDate updateDate = LocalDate.from(from);
			do {
				TerminUtil.updateBoundaries(resource, new TimeTool(updateDate));
				updateDate = updateDate.plusDays(1);
			} while (updateDate.isBefore(to) || updateDate.isEqual(to));
		}
		
		ArrayList<IPeriod> ret = new ArrayList<IPeriod>();
		Query<Termin> query = new Query<Termin>(Termin.class, null, null, Termin.TABLENAME,
			new String[] {
				Termin.FLD_BEREICH, Termin.FLD_BEGINN, Termin.FLD_TAG, Termin.FLD_DAUER,
				Termin.FLD_TERMINSTATUS, Termin.FLD_TERMINTYP, Termin.FLD_LINKGROUP,
				Termin.FLD_PATIENT, Termin.FLD_GRUND, Termin.FLD_STATUSHIST
			});
		if (!resources.isEmpty()) {
			String[] resourceArray = resources.toArray(new String[resources.size()]);
			query.startGroup();
			for (int i = 0; i < resourceArray.length; i++) {
				if (i > 0) {
					query.or();
				}
				query.add(Termin.FLD_BEREICH, Query.EQUALS, resourceArray[i]);
			}
			query.endGroup();
			query.and();
			query.startGroup();
			if (from != null) {
				TimeTool time = new TimeTool(from);
				query.add(Termin.FLD_TAG, Query.GREATER_OR_EQUAL,
					time.toString(TimeTool.DATE_COMPACT));
			}
			if (to != null) {
				TimeTool time = new TimeTool(to);
				query.add(Termin.FLD_TAG, Query.LESS_OR_EQUAL,
					time.toString(TimeTool.DATE_COMPACT));
			}
			query.endGroup();
			ret.addAll((Collection<? extends IPeriod>) query.execute());
		}
		return ret;
	}
}
