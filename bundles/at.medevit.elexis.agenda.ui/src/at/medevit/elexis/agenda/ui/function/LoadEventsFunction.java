package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import at.medevit.elexis.agenda.ui.model.Event;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.data.TerminUtil;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.model.IPeriod;
import ch.elexis.data.Query;
import ch.elexis.data.Reminder;
import ch.rgw.tools.TimeTool;

public class LoadEventsFunction extends AbstractBrowserFunction {
	
	private static Logger logger = LoggerFactory.getLogger(LoadEventsFunction.class);
	
	private Gson gson;
	
	private Set<String> resources = new HashSet<String>();
	
	private ScriptingHelper scriptingHelper;
	
	private LoadingCache<TimeSpan, EventsJsonValue> cache;
	
	private long knownLastUpdate = 0;
	
	private TimeSpan currentTimeSpan;
	
	private HeartListener heartListener;
	
	private class EventsJsonValue {
		
		private Map<String, Event> eventsMap;
		
		private String jsonString;
		
		private TimeSpan timespan;

		public EventsJsonValue(TimeSpan key, List<Event> events) {
			this.timespan = key;
			this.eventsMap =
				events.parallelStream().collect(Collectors.toMap(e -> e.getId(), e -> e));
			jsonString = gson.toJson(eventsMap.values());
		}
		
		public String getJson(){
			return jsonString;
		}
		
		public boolean updateWith(List<IPeriod> changedPeriods) {
			boolean updated = false;
			for (IPeriod iPeriod : changedPeriods) {
				if (eventsMap.containsKey(iPeriod.getId())) {
					boolean deleted = ((Termin) iPeriod).isDeleted();
					if (deleted || !timespan.contains(iPeriod)) {
						// deleted or moved outside timespan
						eventsMap.remove(iPeriod.getId());
					} else {
						// updated still inside timespan
						Event event = Event.of(iPeriod);
						eventsMap.put(event.getId(), event);
					}
					updated = true;
				} else if (timespan.contains(iPeriod)) {
					// new or moved into timespan
					Event event = Event.of(iPeriod);
					eventsMap.put(event.getId(), event);
					updated = true;
				}
			}
			if (updated) {
				jsonString = gson.toJson(eventsMap.values());
			}
			return updated;
		}
	}
	
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
		
		public boolean contains(IPeriod iPeriod){
			LocalDate periodStartDate = iPeriod.getStartTime().toLocalDate();
			return (periodStartDate.isEqual(startDate) || periodStartDate.isAfter(startDate))
				&& (periodStartDate.isEqual(endDate) || periodStartDate.isBefore(endDate));
		}
		
		@Override
		public String toString(){
			return "Timespan [" + startDate + " - " + endDate + "]";
		}
	}
	
	public LoadEventsFunction(Browser browser, String name, ScriptingHelper scriptingHelper){
		super(browser, name);
		gson = new GsonBuilder().create();
		this.scriptingHelper = scriptingHelper;
		
		cache = CacheBuilder.newBuilder().maximumSize(7).build(new TimeSpanLoader());
		
		heartListener = new HeartListener() {
			@Override
			public void heartbeat(){
				long currentLastUpdate = Termin.getHighestLastUpdate(Termin.TABLENAME);
				if (knownLastUpdate != 0 && knownLastUpdate < currentLastUpdate) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run(){
							scriptingHelper.refetchEvents();
						}
					});
				}
			}
		};
		
		browser.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e){
				CoreHub.heart.removeListener(heartListener);
			}
		});
		
		CoreHub.heart.addListener(heartListener);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			try {
				currentTimeSpan =
					new TimeSpan(getDateArg(arguments[0]), getDateArg(arguments[1]));
				long currentLastUpdate = Termin.getHighestLastUpdate(Termin.TABLENAME);
				if (knownLastUpdate == 0) {
					knownLastUpdate = currentLastUpdate;
				} else if (knownLastUpdate < currentLastUpdate) {
					List<IPeriod> changedPeriods = getChangedPeriods();
					ConcurrentMap<TimeSpan, EventsJsonValue> cacheAsMap = cache.asMap();
					for (TimeSpan timeSpan : cacheAsMap.keySet()) {
						EventsJsonValue eventsJson = cache.get(timeSpan);
						if (eventsJson.updateWith(changedPeriods)) {
							logger.debug("Updated timespan " + timeSpan);
						} else {
							logger.debug("No update to timespan " + timeSpan);
						}
					}
					knownLastUpdate = currentLastUpdate;
				}
				EventsJsonValue eventsJson = cache.get(currentTimeSpan);
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
				return eventsJson.getJson();
			} catch (ExecutionException e) {
				throw new IllegalStateException("Error loading events", e);
			}
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
	}
	
	/**
	 * Get all changed {@link IPeriod} since the knownLastUpdate.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<IPeriod> getChangedPeriods(){
		Query<Termin> query = new Query<>(Termin.class, Termin.TABLENAME, true, null);
		query.clear(true);
		query.add(Reminder.FLD_LASTUPDATE, Query.GREATER, Long.toString(knownLastUpdate));
		return (List<IPeriod>) (List<?>) query.execute();
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
	
	private class TimeSpanLoader extends CacheLoader<TimeSpan, EventsJsonValue> {
		
		@Override
		public EventsJsonValue load(TimeSpan key) throws Exception{
			List<IPeriod> periods = getPeriods(key);
			return new EventsJsonValue(key,
				periods.parallelStream().map(p -> Event.of(p)).collect(Collectors.toList()));
		}
		
		private List<IPeriod> getPeriods(TimeSpan timespan) throws IllegalStateException{
			logger.debug("Loading timespan " + timespan);
			LocalDate from = timespan.startDate;
			LocalDate to = timespan.endDate;
			
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
					query.add(Termin.FLD_TAG, Query.LESS,
						time.toString(TimeTool.DATE_COMPACT));
				}
				query.endGroup();
				List<? extends IPeriod> iPeriods = (List<? extends IPeriod>) query.execute();
				ret.addAll(iPeriods);
			}
			logger.debug("Loading timespan " + timespan + " finished");
			return ret;
		}
	}
	
	public List<IPeriod> getCurrentPeriods(){
		TimeSpanLoader loader = new TimeSpanLoader();
		return loader.getPeriods(currentTimeSpan);
	}
}
