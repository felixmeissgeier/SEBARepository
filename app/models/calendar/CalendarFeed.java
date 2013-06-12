package models.calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class CalendarFeed {
	
	private List<CalendarEntry> entryList = new ArrayList<CalendarEntry>();
	
	public CalendarFeed(Calendar iCalCalendar){
		fromICal(iCalCalendar);
	}
	
	public List<CalendarEntry> getEntryList(){
		return entryList;
	}
	
	private void fromICal(Calendar iCal){
		ComponentList eventComponents = iCal.getComponents(Component.VEVENT);
		for(int i = 0; i<eventComponents.size(); i++){
			VEvent event = (VEvent) eventComponents.get(i);
			entryList.add(convertToCalendarEntry(event));
		}
	}
	
	private CalendarEntry convertToCalendarEntry(VEvent iCalEvent){
		CalendarEntry calEntry = new CalendarEntry();
		
		if(iCalEvent.getSummary()!=null){
			calEntry.setTitle(iCalEvent.getSummary().getValue());
		}
		else{
			calEntry.setTitle("# N/A #");
		}
		calEntry.setStartDate(iCalEvent.getStartDate().getDate());
		calEntry.setEndDate(iCalEvent.getEndDate().getDate());
		calEntry.setLastModified(iCalEvent.getLastModified().getDate());
		calEntry.setID(iCalEvent.getUid().getValue());	
		
		return calEntry;
	}
	
}
