package models.calendar;

/**
 * Interface of an external calendar to sync the timetable.
 *
 */
public interface CalendarConnector {

    void receiveCalendarFeed();

    CalendarFeed getCalendarFeed();

}
