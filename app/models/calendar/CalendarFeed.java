package models.calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.timetable.TimetableEntry;
import models.timetable.TimetableEntry.TimetableEntryType;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;

import org.joda.time.DateTime;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class CalendarFeed {
	
	private List<CalendarEntry> entryList = new ArrayList<CalendarEntry>();
	
	public CalendarFeed(Calendar iCalCalendar){
		fromICal(iCalCalendar);
	}
	
	public List<CalendarEntry> getCalendarEntryList(){
		return entryList;
	}
	
	public List<TimetableEntry> getTimetableEntryList(){
		List<TimetableEntry> timetableEntryList = new ArrayList<TimetableEntry>();
		for(CalendarEntry currentEntry : entryList){
			timetableEntryList.add(convertToTimetableEntry(currentEntry));
		}
		return timetableEntryList;
	}
	
	private void fromICal(Calendar iCal){
		ComponentList eventComponents = iCal.getComponents(Component.VEVENT);
		for(int i = 0; i<eventComponents.size(); i++){
			VEvent event = (VEvent) eventComponents.get(i);
			entryList.add(convertToCalendarEntry(event));
		}
	}
	
	private TimetableEntry convertToTimetableEntry(CalendarEntry calEntry){
		TimetableEntry timetableEntry = new TimetableEntry();
		
		if(calEntry.getTitle()!=null){
			timetableEntry.setTitle(calEntry.getTitle());
		}
		else{
			timetableEntry.setTitle("# N/A #");
		}
		timetableEntry.setStartDateTime(calEntry.getStartDate());
		timetableEntry.setEndDateTime(calEntry.getEndDate());
		timetableEntry.setEntryType(TimetableEntryType.EXTERNAL);
		timetableEntry.setDescription("- no description -");
		
		return timetableEntry;
	}
	
	private CalendarEntry convertToCalendarEntry(VEvent iCalEvent){
		CalendarEntry calEntry = new CalendarEntry();
		
		if(iCalEvent.getSummary()!=null){
			calEntry.setTitle(iCalEvent.getSummary().getValue());
		}
		else{
			calEntry.setTitle("# N/A #");
		}
		calEntry.setStartDate(new DateTime(iCalEvent.getStartDate().getDate()));
		calEntry.setEndDate(new DateTime(iCalEvent.getEndDate().getDate()));
		calEntry.setLastModified(new DateTime(iCalEvent.getLastModified().getDate()));
		calEntry.setID(iCalEvent.getUid().getValue());	
		
		return calEntry;
	}
	
}
