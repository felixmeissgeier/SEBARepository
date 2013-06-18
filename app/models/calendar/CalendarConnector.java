package models.calendar;

import java.io.IOException;
import java.util.List;

import models.timetable.TimetableEntry;

public interface CalendarConnector {
		
	public void receiveCalendarFeed();
	public CalendarFeed getCalendarFeed();
	
}
