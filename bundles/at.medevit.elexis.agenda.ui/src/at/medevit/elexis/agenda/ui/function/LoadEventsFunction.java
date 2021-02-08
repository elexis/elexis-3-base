package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.browser.Browser;
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
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class LoadEventsFunction extends AbstractBrowserFunction {
	
	private static Logger logger = LoggerFactory.getLogger(LoadEventsFunction.class);
	
	private Gson gson;
	
	private Set<String> resources = new HashSet<String>();
	
	protected ScriptingHelper scriptingHelper;
	
	private LoadingCache<TimeSpan, EventsJsonValue> cache;
	
	protected long knownLastUpdate = 0;
	
	private TimeSpan currentTimeSpan;
	
	private Timer timer;
	
	private class EventsJsonValue {
		
		private Map<String, Event> eventsMap;
		
		private String jsonString;
		
		private TimeSpan timespan;
		
		public EventsJsonValue(TimeSpan key, List<Event> events){
			this.timespan = key;
			this.eventsMap =
				events.parallelStream().collect(Collectors.toMap(e -> e.getId(), e -> e));
			jsonString = gson.toJson(eventsMap.values());
		}
		
		public String getJson(){
			return jsonString;
		}
		
		public boolean updateWith(List<IPeriod> changedPeriods, IContact userContact){
			boolean updated = false;
			for (IPeriod iPeriod : changedPeriods) {
				if (eventsMap.containsKey(iPeriod.getId())) {
					boolean deleted = ((IAppointment) iPeriod).isDeleted();
					if (deleted || !timespan.contains(iPeriod)) {
						// deleted or moved outside timespan
						eventsMap.remove(iPeriod.getId());
					} else {
						// updated still inside timespan
						Event event = Event.of(iPeriod, userContact);
						eventsMap.put(event.getId(), event);
					}
					updated = true;
				} else if (timespan.contains(iPeriod)) {
					// new or moved into timespan
					Event event = Event.of(iPeriod, userContact);
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
		private IContact userContact;
		
		public TimeSpan(LocalDate startDate, LocalDate endDate, IContact userContact){
			this.startDate = startDate;
			this.endDate = endDate;
			this.userContact = userContact;
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
	
	public LoadEventsFunction(Browser browser, String name, ScriptingHelper scriptingHelper,
		UISynchronize uiSynchronize){
		super(browser, name);
		gson = new GsonBuilder().create();
		this.scriptingHelper = scriptingHelper;
		
		cache = CacheBuilder.newBuilder().maximumSize(7).build(new TimeSpanLoader());
		
		timer = new Timer("Agenda check for updates", true);
		timer.schedule(new CheckForUpdatesTimerTask(this, uiSynchronize), 10000);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			try {
				IContact userContact = ContextServiceHolder.get().getActiveUser()
					.map(IUser::getAssignedContact).orElse(null);
				currentTimeSpan =
					new TimeSpan(getDateArg(arguments[0]), getDateArg(arguments[1]), userContact);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.BASE + "agenda/loadtimespan",
					new LoadEventTimeSpan(currentTimeSpan.startDate, currentTimeSpan.endDate));
				long currentLastUpdate =
					CoreModelServiceHolder.get().getHighestLastUpdate(IAppointment.class);
				if (knownLastUpdate == 0) {
					knownLastUpdate = currentLastUpdate;
				} else if (knownLastUpdate < currentLastUpdate) {
					List<IPeriod> changedPeriods = getChangedPeriods();
					ConcurrentMap<TimeSpan, EventsJsonValue> cacheAsMap = cache.asMap();
					for (TimeSpan timeSpan : cacheAsMap.keySet()) {
						EventsJsonValue eventsJson = cache.get(timeSpan);
						if (eventsJson.updateWith(changedPeriods, userContact)) {
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
		IQuery<IAppointment> query =
			CoreModelServiceHolder.get().getQuery(IAppointment.class, true, true);
		query.and("lastupdate", COMPARATOR.GREATER, Long.valueOf(knownLastUpdate));
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
			return new EventsJsonValue(key, periods.parallelStream()
				.map(p -> Event.of(p, key.userContact)).collect(Collectors.toList()));
		}
		
		@SuppressWarnings("unchecked")
		private List<IPeriod> getPeriods(TimeSpan timespan) throws IllegalStateException{
			logger.debug("Loading timespan " + timespan);
			LocalDate from = timespan.startDate;
			LocalDate to = timespan.endDate;
			
			for (String resource : resources) {
				LocalDate updateDate = LocalDate.from(from);
				do {
					AppointmentServiceHolder.get().updateBoundaries(resource, updateDate);
					updateDate = updateDate.plusDays(1);
				} while (updateDate.isBefore(to) || updateDate.isEqual(to));
			}
			
			IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
			if (!resources.isEmpty()) {
				String[] resourceArray = resources.toArray(new String[resources.size()]);
				query.startGroup();
				for (int i = 0; i < resourceArray.length; i++) {
					query.or(ModelPackage.Literals.IAPPOINTMENT__SCHEDULE, COMPARATOR.EQUALS,
						resourceArray[i]);
					
				}
				query.andJoinGroups();
				query.startGroup();
				if (from != null) {
					query.and("tag", COMPARATOR.GREATER_OR_EQUAL, from);
				}
				if (to != null) {
					query.and("tag", COMPARATOR.LESS, to);
				}
				query.andJoinGroups();
				return (List<IPeriod>) (List<?>) query.execute();
			}
			logger.debug("Loading timespan " + timespan + " finished");
			return Collections.emptyList();
		}
	}
	
	public List<IPeriod> getCurrentPeriods(){
		TimeSpanLoader loader = new TimeSpanLoader();
		return loader.getPeriods(currentTimeSpan);
	}
	
	/**
	 * Invalidate all cache entries. This should be done prior to changing the user.
	 */
	public void invalidateCache(){
		cache.invalidateAll();
	}
}
