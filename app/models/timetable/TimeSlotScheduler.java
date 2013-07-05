package models.timetable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.CustomerPreferences;
import models.DateTimeInterval;
import models.DateUtility;
import models.DateUtility.Day;
import models.TimeInterval;
import models.timetable.ScheduledTimetableEntryList.ScheduleStatus;
import models.timetable.TimetableEntry.TimetableEntryType;

import org.joda.time.DateTime;

import dto.CourseDTO;

/**
 * Scheduler class for time slots. Used to define optimal time of each timeslot.
 * 
 */
public class TimeSlotScheduler {

	public static final int DIFFICULTY_RELATION_PAGES_PER_X_HOURS = 10;

	private List<CourseDTO> courses;
	private CustomerPreferences customerPreferences;
	private double minSlotDuration = 0;
	private Color currentCourseColor = new Color(0, 0, 0);

	public TimeSlotScheduler(List<CourseDTO> courses, CustomerPreferences customerPreferences) {
		this.courses = courses;
		this.customerPreferences = customerPreferences;
		this.minSlotDuration = customerPreferences.getMinLearningSlotDuration();
	}

	/*
	 * This method computes and schedules all learning time slots of all courses
	 * by taking external calendar data into account
	 */
	public ScheduledTimetableEntryList scheduleTimeSlots(List<TimetableEntry> externalCalendarData) {
		ScheduledTimetableEntryList scheduledTimetableEntryList = new ScheduledTimetableEntryList();
		Collections.sort(courses, Collections.reverseOrder());

		scheduledTimetableEntryList.setScheduledTimeSlotList(addBlockedTimeSlots(externalCalendarData, getLatestCourseDeadline()));

		// iterate over courses, depending on priorities
		for (CourseDTO course : courses) {
			scheduledTimetableEntryList = computeCourseLearningSlots(scheduledTimetableEntryList, course);
		}

		return scheduledTimetableEntryList;
	}

	/**
	 * Computes learning time slots for each course
	 */
	private ScheduledTimetableEntryList computeCourseLearningSlots(ScheduledTimetableEntryList currentTimetableEntryList, CourseDTO course) {
		List<TimetableEntry> currentTimetable = currentTimetableEntryList.getScheduledTimeSlotList();
		currentCourseColor = new Color((int) (Math.random() * 250), (int) (Math.random() * 250), (int) (Math.random() * 250));

		// compute timeslots where scheduling is possible
		List<DateTimeInterval> freeTimeSlots = computeFreeTimeSlots(currentTimetable, course.getDeadline());
		Collections.sort(freeTimeSlots);
		Collections.sort(currentTimetable);

		// get and compute some needed values
		//double accumulatedFreeTimeHours = getAccumulatedFreeTimeHours(freeTimeSlots);
		//int coursePeriodNumberOfDays = DateUtility.getDaysOfDuration(new DateTime(), course.getDeadline());
		//double averageFreeTimeHoursPerDay = accumulatedFreeTimeHours / coursePeriodNumberOfDays;
		double hoursToSchedule = 0;
		if (course.getCourseMaterial().isConsiderExpectedHours()) {
			hoursToSchedule = course.getCourseMaterial().getWorkloadHoursExpected();
		} else {
			hoursToSchedule = (course.getDifficulty() / DIFFICULTY_RELATION_PAGES_PER_X_HOURS) * course.getCourseMaterial().getScriptPagesToDo();
		}
		//double averageNeededHoursPerDay = hoursToSchedule / coursePeriodNumberOfDays;

		DateTime currentDateTime = new DateTime().plusDays(1);
		List<TimetableEntry> scheduledWorkLoad = new ArrayList<TimetableEntry>();

		// first iteration
		// iterate over all freetime slots and prioritize daytime
		// procedure: find free time slot within daytime which is long enough
		// and
		// store learning slot there (only one each day)
		for (int i = 0; i < freeTimeSlots.size(); i++) {
			DateTimeInterval currentInterval = new DateTimeInterval(freeTimeSlots.get(i));
			boolean tryNextIntervalSameDay = false;
			// if intervals jump, like multiple days slot
			if (currentDateTime.getDayOfYear() < currentInterval.getStartDateTime().getDayOfYear()) {
				currentDateTime = currentInterval.getStartDateTime();
			}
			// current interval matches with current considered date
			if (currentDateTime.getDayOfYear() == currentInterval.getStartDateTime().getDayOfYear()) {

				// retrieve best time to start learn time slot, depending on
				// daytime
				DateTime bestStartTime = DateUtility.getBestDayTimeMatch(currentInterval.getStartDateTime(), currentInterval.getEndDateTime(), customerPreferences.getPreferredLearningDayTime());
				if (bestStartTime != null) {
					int learningSlotDurationMinutes = 0;
					currentInterval.setStartDateTime(bestStartTime);
					double intervalHours = currentInterval.getDurationInHours();
					if (customerPreferences.getMaxLearningSlotDuration() < hoursToSchedule && intervalHours >= customerPreferences.getMaxLearningSlotDuration()) {
						learningSlotDurationMinutes = (int) (customerPreferences.getMaxLearningSlotDuration() * 60);
					} else if (customerPreferences.getMaxLearningSlotDuration() >= hoursToSchedule) {
						learningSlotDurationMinutes = (int) (hoursToSchedule * 60);
					}
					if (learningSlotDurationMinutes != 0) {
						// add new timetable entry (learn time slot) to
						// timetable entry list
						hoursToSchedule -= learningSlotDurationMinutes / 60.0;
						scheduledWorkLoad.add(new TimetableEntry(course.getTitle(), "Learning Slot", currentInterval.getStartDateTime(), currentInterval.getStartDateTime().plusMinutes(learningSlotDurationMinutes), TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor));
					} else {
						// if free timeslot is too small
						tryNextIntervalSameDay = true;
					}
				}
				if (!tryNextIntervalSameDay) {
					currentDateTime = currentDateTime.plusDays(1);
				}
			}
		}
		Collections.sort(scheduledWorkLoad);

		// second iteration
		// if more hours to schedule than
		// go through all days and look for days without
		// specified learning slot
		if (hoursToSchedule > 0) {
			currentDateTime = new DateTime().plusDays(1);
			int timetableEntryIterator = 0;
			int freeTimeSlotIterator = 0;
			while (currentDateTime.isBefore(course.getDeadline()) && hoursToSchedule > 0) {
				//increase currentDateTime every while-iteration and if there is already scheduled workload from
				//the previous scheduling, then iterate timetableEntryDay as long it at least equals currentDateTime
				//in this case it will do nothing but again increase currentDateTime and timetableEntryDay until
				//timetableEntryDay is located in the future (after currentDateTime), then there is no scheduled 
				//workload at this day --> use it for new scheduling
				
				
				// if timetableEntry day is before currentDateTime day, then
				// switch to next entry
				int timetableEntryDay = course.getDeadline().getDayOfYear();
				if(scheduledWorkLoad.size()>0){
					while ((timetableEntryIterator < scheduledWorkLoad.size()-1) && scheduledWorkLoad.get(timetableEntryIterator).getStartDateTime().getDayOfYear() < currentDateTime.getDayOfYear()) {
						timetableEntryIterator++;
					}
					timetableEntryDay = scheduledWorkLoad.get(timetableEntryIterator).getStartDateTime().getDayOfYear();
				}				

				// next timetableentry (already stored learning slot) is located
				// in the future
				// thus, this day hasn't scheduled slot
				if (timetableEntryDay > currentDateTime.getDayOfYear()) {

					// go to next free time slot blocks
					while (freeTimeSlots.get(freeTimeSlotIterator).getStartDateTime().getDayOfYear() <= currentDateTime.getDayOfYear() && freeTimeSlotIterator < (freeTimeSlots.size() - 1)) {
						DateTimeInterval interval = freeTimeSlots.get(freeTimeSlotIterator);
						if (interval.getStartDateTime().getDayOfYear() == currentDateTime.getDayOfYear()) {
							double intervalHours = interval.getDurationInHours();
							int learningSlotDurationMinutes = 0;
							if (customerPreferences.getMaxLearningSlotDuration() < hoursToSchedule && intervalHours >= customerPreferences.getMaxLearningSlotDuration()) {
								learningSlotDurationMinutes = (int) (customerPreferences.getMaxLearningSlotDuration() * 60);
							} else if (customerPreferences.getMaxLearningSlotDuration() >= hoursToSchedule) {
								learningSlotDurationMinutes = (int) (hoursToSchedule * 60);
							}
							if (learningSlotDurationMinutes != 0) {
								hoursToSchedule -= learningSlotDurationMinutes / 60.0;
								scheduledWorkLoad.add(new TimetableEntry(course.getTitle(), "Learning Slot", interval.getStartDateTime(), interval.getStartDateTime().plusMinutes(learningSlotDurationMinutes), TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor));
								break;
							}
						}
						freeTimeSlotIterator++;
					}

				} else if (timetableEntryDay == currentDateTime.getDayOfYear()) {
					if (timetableEntryIterator < scheduledWorkLoad.size() - 1) {
						timetableEntryIterator++;
					}
				}

				currentDateTime = currentDateTime.plusDays(1);
			}
		}

		// applying previous additions to time table
		// as precondition of the next step
		for (TimetableEntry entry : scheduledWorkLoad) {
			currentTimetable.add(entry);
		}

		// third iteration
		// if more hours to schedule than
		// apply previous additions to timetable and recompute free time slots
		if (hoursToSchedule > 0) {
			freeTimeSlots = computeFreeTimeSlots(currentTimetable, course.getDeadline());
			Collections.sort(freeTimeSlots);
			int day = 0;
			for (DateTimeInterval interval : freeTimeSlots) {
				if (interval.getStartDateTime().getDayOfYear() > day) {
					day = interval.getStartDateTime().getDayOfYear();
				}
				if (hoursToSchedule > 0 && interval.getDurationInHours() >= minSlotDuration && day == interval.getStartDateTime().getDayOfYear()) {
					int duration = 0;
					if (hoursToSchedule < minSlotDuration) {
						duration = (int) (hoursToSchedule * 60);
					} else {
						duration = (int) (minSlotDuration * 60);
					}
					currentTimetable.add(new TimetableEntry(course.getTitle(), "Learning Slot", interval.getStartDateTime(), interval.getStartDateTime().plusMinutes(duration), TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor));
					hoursToSchedule -= duration / 60.0;
					day++;
				}
				if (hoursToSchedule == 0) {
					break;
				}
			}
		}
		ScheduledTimetableEntryList.ScheduleStatus status;
		if(hoursToSchedule>0){
			status = ScheduleStatus.ERROR_REACHED_DEADLINE;
		}
		else{
			status = ScheduleStatus.SUCCESS;
		}
		currentTimetableEntryList.setScheduledTimeSlotList(currentTimetable);
		currentTimetableEntryList.addCourseScheduleStatus(course.getTitle(), status);
		return currentTimetableEntryList;
	}

	/**
	 * Returns the duration of all free time slots (as a sum of the subdurations).
	 */
	private double getAccumulatedFreeTimeHours(List<DateTimeInterval> freeTimeSlots) {
		double accumulatedHours = 0;
		for (DateTimeInterval currentInterval : freeTimeSlots) {
			accumulatedHours += DateUtility.getMinutesOfDuration(currentInterval.getStartDateTime(), currentInterval.getEndDateTime()) / 60.0;
		}
		return accumulatedHours;
	}

	/**
	 * Determine and add time slots, where no scheduling of learning time slots
	 * are allowed these are defined by the customer preferences "days of rest"
	 * and "rest time intervals".
	 */
	private List<TimetableEntry> addBlockedTimeSlots(List<TimetableEntry> timetableEntries, DateTime deadline) {
		List<TimetableEntry> extendedTimetableEntries = timetableEntries;
		Collections.sort(extendedTimetableEntries);
		DateTime date = new DateTime();
		List<TimeInterval> restTimeIntervals = customerPreferences.getTimeIntervalsOfRest();
		List<Day> restDays = customerPreferences.getDaysOfRest();

		if (restTimeIntervals != null) {
			Collections.sort(restTimeIntervals);
			// for each day till deadline add block-entries into timetable
			while (date.isBefore(deadline) && date.getDayOfYear() != deadline.getDayOfYear()) {
				date = date.plusDays(1);
				if (restDays != null) {
					if (restDays.contains(DateUtility.getDayConstByOrdinal(date.getDayOfWeek() - 1))) {
						DateTime startDateTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 0, 0);
						DateTime endDateTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 23, 59);
						TimetableEntry blockEntry = new TimetableEntry("Day Of Rest", "", startDateTime, endDateTime, TimetableEntryType.BLOCKED, null);
						extendedTimetableEntries.add(blockEntry);
						continue;
					}
				}

				for (TimeInterval timeInterval : restTimeIntervals) {
					DateTime startDateTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), timeInterval.getTime1().getHourOfDay(), timeInterval.getTime1().getMinuteOfHour());
					DateTime endDateTime = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), timeInterval.getTime2().getHourOfDay(), timeInterval.getTime2().getMinuteOfHour());
					TimetableEntry blockEntry = new TimetableEntry("Free Time", "", startDateTime, endDateTime, TimetableEntryType.BLOCKED, null);
					extendedTimetableEntries.add(blockEntry);
				}
			}
		}

		return extendedTimetableEntries;
	}

	/**
	 * Computes time slots where scheduling of learn time slots is allowed.
	 */
	private List<DateTimeInterval> computeFreeTimeSlots(List<TimetableEntry> currentCalendarData, DateTime deadline) {
		List<DateTimeInterval> freeTimeSlots = new ArrayList<DateTimeInterval>();
		if (currentCalendarData != null) {
			Collections.sort(currentCalendarData);
			DateTime potentialTimeSlotBegin = new DateTime();

			// look through all timetable-entries and modify potential
			// timeslotbegin-position
			// on the fly
			// this procedure obtains the free time slots
			for (TimetableEntry entry : currentCalendarData) {
				if (entry.getStartDateTime().isAfter(potentialTimeSlotBegin) && entry.getStartDateTime().isBefore(deadline) && entry.getEndDateTime().isBefore(deadline)) {

					// if free timeslot is spanned multiple days, than separate
					// into
					// multiple outcome datasets
					if (entry.getStartDateTime().getDayOfYear() != potentialTimeSlotBegin.getDayOfYear()) {
						// get timeslot of current day
						DateTime timeSlotBeginFirst = potentialTimeSlotBegin;
						DateTime timeSlotEndFirst = potentialTimeSlotBegin.withHourOfDay(23).withMinuteOfHour(59);
						if (DateUtility.getMinutesOfDuration(timeSlotBeginFirst, timeSlotEndFirst) / 60.0 >= minSlotDuration) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginFirst, timeSlotEndFirst));
						}

						// get timeslot of day when next calendar entry starts
						DateTime timeSlotBeginLast = entry.getStartDateTime().withHourOfDay(0).withMinuteOfHour(0);
						DateTime timeSlotEndLast = entry.getStartDateTime();
						if (DateUtility.getMinutesOfDuration(timeSlotBeginLast, timeSlotEndLast) / 60.0 >= minSlotDuration) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginLast, timeSlotEndLast));
						}
						// get all-day-timeslots of days between
						for (int i = potentialTimeSlotBegin.getDayOfYear() + 1; i < entry.getStartDateTime().getDayOfYear(); i++) {
							DateTime timeSlotBeginBetween = (new DateTime()).withDayOfYear(i).withHourOfDay(0).withMinuteOfHour(0);
							DateTime timeSlotEndBetween = (new DateTime()).withDayOfYear(i).withHourOfDay(23).withMinuteOfHour(59);
							if (DateUtility.getMinutesOfDuration(timeSlotBeginBetween, timeSlotEndBetween) / 60.0 >= minSlotDuration) {
								freeTimeSlots.add(new DateTimeInterval(timeSlotBeginBetween, timeSlotEndBetween));
							}
						}
					} else {
						// get timeslot of current day
						DateTime timeSlotBegin = potentialTimeSlotBegin;
						DateTime timeSlotEnd = entry.getStartDateTime();
						if (DateUtility.getMinutesOfDuration(timeSlotBegin, timeSlotEnd) / 60.0 >= minSlotDuration) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBegin, timeSlotEnd));
						}
					}
				}

				// set new potentialTimeSlotBegin
				potentialTimeSlotBegin = entry.getEndDateTime();
			}

		}
		return freeTimeSlots;
	}

	/**
	 * Returns the latest deadline datetime of all courses.
	 */
	private DateTime getLatestCourseDeadline() {
		DateTime latestDeadline = new DateTime();

		for (CourseDTO course : courses) {
			if (course.getDeadline().isAfter(latestDeadline)) {
				latestDeadline = course.getDeadline();
			}
		}

		return latestDeadline;
	}
}
