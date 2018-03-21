package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
	
	private LoadingCache<TimeSpan, List<Event>> cache;
	
	private long knownLastUpdate = 0;
	
	private class TimeSpan {
		private LocalDate startDate;
		private LocalDate endDate;
		
		public TimeSpan(LocalDate startDate, LocalDate endDate){
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
			result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj){
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TimeSpan other = (TimeSpan) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (endDate == null) {
				if (other.endDate != null)
					return false;
			} else if (!endDate.equals(other.endDate))
				return false;
			if (startDate == null) {
				if (other.startDate != null)
					return false;
			} else if (!startDate.equals(other.startDate))
				return false;
			return true;
		}
		
		private LoadEventsFunction getOuterType(){
			return LoadEventsFunction.this;
		}
	}
	
	public LoadEventsFunction(Browser browser, String name){
		super(browser, name);
		gson = new GsonBuilder().create();
		scriptingHelper = new ScriptingHelper(browser);
		
		cache = CacheBuilder.newBuilder().maximumSize(7).build(new TimeSpanLoader());
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			try {
				TimeSpan timeSpan =
					new TimeSpan(getDateArg(arguments[0]), getDateArg(arguments[1]));
				long currentLastUpdate = Termin.getHighestLastUpdate(Termin.TABLENAME);
				if (knownLastUpdate < currentLastUpdate) {
					cache.invalidateAll();
					knownLastUpdate = currentLastUpdate;
				}
				List<Event> events = cache.get(timeSpan);
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run(){
						if (!isDisposed()) {
							// update calendar height
							updateCalendarHeight();
							scriptingHelper.scrollToNow();
						}
					}
				});
				return gson.toJson(events);
			} catch (ExecutionException e) {
				throw new IllegalStateException("Error loading events", e);
			}
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
	}
	
	public void addResource(String resource){
		this.resources.add(resource);
	}
	
	public void removeResource(String resource){
		this.resources.remove(resource);
	}
	
	public void setResources(List<String> resources){
		this.resources.clear();
		this.resources.addAll(resources);
		cache.invalidateAll();
	}
	
	private class TimeSpanLoader extends CacheLoader<TimeSpan, List<Event>> {
		
		@Override
		public List<Event> load(TimeSpan key) throws Exception{
			System.out.println("LOADING ...");
			List<IPeriod> periods = getPeriods(key.startDate, key.endDate);
			return periods.parallelStream().map(p -> Event.of(p)).collect(Collectors.toList());
		}
		
		private List<IPeriod> getPeriods(LocalDate from, LocalDate to) throws IllegalStateException{
			
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
}
