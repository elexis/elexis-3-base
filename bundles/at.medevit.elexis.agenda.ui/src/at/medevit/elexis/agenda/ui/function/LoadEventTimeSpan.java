package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoadEventTimeSpan {
	private LocalDate from;
	private LocalDate to;
	
	public LoadEventTimeSpan(LocalDate from, LocalDate to){
		this.from = from;
		this.to = to;
	}
	
	public boolean isWeek(){
		return ChronoUnit.DAYS.between(from, to) == 7;
	}
	
	public boolean isDay(){
		return ChronoUnit.DAYS.between(from, to) == 1;
	}
	
	public LocalDate getFrom(){
		return from;
	}
}
