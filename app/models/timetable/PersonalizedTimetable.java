package models.timetable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import models.CustomerPreferences;
import models.DateUtility;
import models.TimeInterval;
import models.calendar.CalendarFeed;
import models.calendar.GoogleCalendarConnector;

import org.joda.time.LocalTime;

import dto.CourseDTO;

/**
 * Represents a timetable for a single user. The timetable is built based on the
 * user preferences.
 * 
 */
public class PersonalizedTimetable {
	private static final String CALENDER_SYNC_URL = "https://www.google.com/calendar/ical/schmi.kalle%40gmail.com/private-82d40d6ada06fa5825d8910c4949e64f/basic.ics";
	private TimeSlotScheduler timeSlotScheduler = null;

	public PersonalizedTimetable(List<CourseDTO> courses) {
		// CourseDTO agtCourse = new CourseDTO(null, "Algorithmic Game Theory",
		// "very tough lecture..", 0.9, 1.0,
		// new DateTime().plusWeeks(2), new CourseMaterialDTO(500, 40));
		// CourseDTO sebaCourse = new CourseDTO(null, "SEBA", "Web App", 1.0, 1.0,
		// new DateTime().plusWeeks(4),
		// new CourseMaterialDTO(120, 40));
		//
		// List<CourseDTO> courses = new ArrayList<CourseDTO>();
		// courses.add(agtCourse);
		// courses.add(sebaCourse);

		CustomerPreferences dummyCustomerPrefs = new CustomerPreferences(
				DateUtility.LearningDayTime.AFTERNOON, 1.0, 0.5, 0.25);
		List<TimeInterval> dummyCustomerRestTimeIntervals = new ArrayList<TimeInterval>();
		dummyCustomerRestTimeIntervals.add(new TimeInterval(new LocalTime(0, 0), new LocalTime(7, 0)));
		dummyCustomerRestTimeIntervals
				.add(new TimeInterval(new LocalTime(22, 0), new LocalTime(23, 59)));
		dummyCustomerPrefs.setTimeIntervalsOfRest(dummyCustomerRestTimeIntervals);
		List<DateUtility.Day> dummyRestDays = new ArrayList<DateUtility.Day>();
		dummyRestDays.add(DateUtility.Day.Saturday);
		dummyRestDays.add(DateUtility.Day.Sunday);
		dummyCustomerPrefs.setDaysOfRest(dummyRestDays);

		timeSlotScheduler = new TimeSlotScheduler(courses, dummyCustomerPrefs);
	}

	private List<TimetableEntry> importExternalCalendarData() {
		List<TimetableEntry> receivedEntries = null;
		GoogleCalendarConnector calConnector = null;
		try {
			calConnector = new GoogleCalendarConnector(new URL(CALENDER_SYNC_URL));
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

	public ScheduledTimetableEntryList scheduleTimeSlots() {
		List<TimetableEntry> receivedEntries = importExternalCalendarData();
		ScheduledTimetableEntryList allEntries = null;
		if (timeSlotScheduler != null) {
			allEntries = timeSlotScheduler.scheduleTimeSlots(receivedEntries);
		}
		return allEntries;
	}
}
