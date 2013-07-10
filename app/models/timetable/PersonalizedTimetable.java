package models.timetable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import models.User;
import models.calendar.CalendarFeed;
import models.calendar.GoogleCalendarConnector;
import dto.CourseDTO;

/**
 * Represents a timetable for a single user. The timetable is built based on the
 * user preferences.
 * 
 */
public class PersonalizedTimetable {
	private TimeSlotScheduler timeSlotScheduler = null;
	private User user;

	public PersonalizedTimetable(User user, List<CourseDTO> courses) {
		this.user = user;
		timeSlotScheduler = new TimeSlotScheduler(courses, user.preferences);
	}

	/**
	 * Imports data from external calendar. As calendar synchronisation link a
	 * property of the user object is used.
	 * 
	 * @return List of {@link TimetableEntry} extracted from the external
	 *         calendar
	 */
	private List<TimetableEntry> importExternalCalendarData() {
		List<TimetableEntry> receivedEntries = null;
		GoogleCalendarConnector calConnector = null;
		try {
			calConnector = new GoogleCalendarConnector(new URL(user.privateCalendarURL));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		calConnector.receiveCalendarFeed();
		CalendarFeed feed = calConnector.getCalendarFeed();

		if (feed != null) {
			receivedEntries = feed.getTimetableEntryList();
		}

		return receivedEntries;
	}

	/**
	 * Applies data from external calendar to the current list of entries.
	 * Builds the merged list of entries and returns back.
	 * 
	 * @return Whole list of scheduled entries
	 */
	public ScheduledTimetableEntryList scheduleTimeSlots() {
		List<TimetableEntry> receivedEntries = importExternalCalendarData();
		ScheduledTimetableEntryList allEntries = null;
		if (timeSlotScheduler != null) {
			allEntries = timeSlotScheduler.scheduleTimeSlots(receivedEntries);
		}
		return allEntries;
	}
}
