package models.calendar;

import java.io.IOException;

import net.fortuna.ical4j.data.ParserException;

/**
 * Interface of an external calendar to sync the timetable.
 * 
 */
public interface CalendarConnector {

	void receiveCalendarFeed() throws IOException, ParserException;

	CalendarFeed getCalendarFeed();

}
