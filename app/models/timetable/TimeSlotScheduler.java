package models.timetable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

	private static final double MINUTES_IN_HOUR_FLOAT = 60.0;
	private static final int MINUTES_IN_HOUR = 60;
	public static final int DIFFICULTY_RELATION_PAGES_PER_X_HOURS = 10;
	public static final double MAX_DIFFICULT_LEARN_HOURS_PER_DAY = 4.0;
	public static final double MAX_LEARN_HOURS_PER_DAY = 8.0;
	public static final double COURSE_IS_DIFFICULT_THRESHOLD = 1.0;

	private List<CourseDTO> courses;
	private CustomerPreferences customerPreferences;
	private double neededHoursMinSlotAndBreak = 0;
	private double neededHoursMaxSlotAndBreak = 0;
	private double neededHoursBreak = 0;
	private double neededHoursMaxSlot = 0;
	private double neededHoursMinSlot = 0;
	private Color currentCourseColor = new Color(0, 0, 0);

	// map<dayOfYear, accumulatedHours>
	private HashMap<Integer, Double> dayHoursOfDifficultCourses = new HashMap<Integer, Double>();
	private HashMap<Integer, Double> dayHoursOfEasyCourses = new HashMap<Integer, Double>();

	public TimeSlotScheduler(List<CourseDTO> courses, CustomerPreferences customerPreferences) {
		this.courses = courses;
		this.customerPreferences = customerPreferences;
		this.neededHoursMinSlotAndBreak = customerPreferences.getMinLearningSlotDuration()
				+ customerPreferences.getBreakDuration();
		this.neededHoursMaxSlotAndBreak = customerPreferences.getMaxLearningSlotDuration()
				+ customerPreferences.getBreakDuration();
		this.neededHoursBreak = customerPreferences.getBreakDuration();
		this.neededHoursMaxSlot = customerPreferences.getMaxLearningSlotDuration();
		this.neededHoursMinSlot = customerPreferences.getMinLearningSlotDuration();
	}

	/*
	 * This method computes and schedules all learning time slots of all courses
	 * by taking external calendar data into account
	 */
	public ScheduledTimetableEntryList scheduleTimeSlots(List<TimetableEntry> externalCalendarData) {
		ScheduledTimetableEntryList scheduledTimetableEntryList = new ScheduledTimetableEntryList();
		Collections.sort(courses, Collections.reverseOrder());

		scheduledTimetableEntryList.setScheduledTimeSlotList(addBlockedTimeSlots(externalCalendarData,
				getLatestCourseDeadline()));

		// iterate over courses, depending on priorities
		for (CourseDTO course : courses) {
			scheduledTimetableEntryList = computeCourseLearningSlots(scheduledTimetableEntryList, course);
		}

		return scheduledTimetableEntryList;
	}

	/**
	 * Computes learning time slots for each course
	 */
	private ScheduledTimetableEntryList computeCourseLearningSlots(
			ScheduledTimetableEntryList currentTimetableEntryList, CourseDTO course) {
		boolean isDifficultCourse;

		if (course.getDifficulty() > COURSE_IS_DIFFICULT_THRESHOLD) {
			isDifficultCourse = true;
		} else {
			isDifficultCourse = false;
		}
		List<TimetableEntry> currentTimetable = currentTimetableEntryList.getScheduledTimeSlotList();
		ScheduledTimetableEntryList.ScheduleStatus status = ScheduledTimetableEntryList.ScheduleStatus.SUCCESS;
		currentCourseColor = course.getColor();
		if (course.getDeadline().isBefore((new DateTime().plusDays(1)))) {
			status = ScheduledTimetableEntryList.ScheduleStatus.ERROR_DEADLINE_IN_PAST;
		}

		// Compute timeslots where scheduling is possible
		List<DateTimeInterval> freeTimeSlots = computeFreeTimeSlots(currentTimetable, course.getDeadline());
		if (freeTimeSlots.size() > 0
				&& status != ScheduledTimetableEntryList.ScheduleStatus.ERROR_DEADLINE_IN_PAST) {
			Collections.sort(freeTimeSlots);
			Collections.sort(currentTimetable);

			// Get and compute some needed values
			double hoursToSchedule = 0;
			if (course.getCourseMaterial().isConsiderExpectedHours()) {
				hoursToSchedule = (int) (course.getCourseMaterial().getWorkloadHoursExpected());
			} else {
				hoursToSchedule = (int) ((course.getDifficulty() / DIFFICULTY_RELATION_PAGES_PER_X_HOURS) * course
						.getCourseMaterial().getScriptPagesToDo());
			}

			DateTime currentDateTime = new DateTime().plusDays(1);
			List<TimetableEntry> scheduledWorkLoad = new ArrayList<TimetableEntry>();

			// first iteration
			// iterate over all freetime slots and prioritize daytime
			// procedure: find free time slot within daytime which is long
			// enough
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
					DateTime bestStartTime = DateUtility.getBestDayTimeMatch(
							currentInterval.getStartDateTime(), currentInterval.getEndDateTime(),
							customerPreferences.getPreferredLearningDayTime());
					if (bestStartTime != null) {

						// compute best matching slot duration
						int learningSlotDurationMinutes = 0;
						currentInterval.setStartDateTime(bestStartTime);
						double intervalHours = currentInterval.getDurationInHours();
						if (neededHoursMaxSlotAndBreak < hoursToSchedule
								&& intervalHours >= neededHoursMaxSlotAndBreak) {
							learningSlotDurationMinutes = (int) (neededHoursMaxSlot * MINUTES_IN_HOUR);
						} else if (neededHoursMaxSlotAndBreak >= hoursToSchedule
								&& intervalHours >= (hoursToSchedule + neededHoursBreak)) {
							learningSlotDurationMinutes = (int) (hoursToSchedule * MINUTES_IN_HOUR);
						}

						if (learningSlotDurationMinutes != 0) {

							// check if it's allowed to schedule timeslot at
							// this day considering maximal workload per day
							double hoursUntilThreshold = hoursUntilWorkloadDayThresholdExceedance(
									isDifficultCourse, currentInterval.getStartDateTime().getDayOfYear());
							if (hoursUntilThreshold >= neededHoursMinSlot) {
								if (hoursUntilThreshold < (learningSlotDurationMinutes / MINUTES_IN_HOUR_FLOAT)) {
									learningSlotDurationMinutes = (int) (hoursUntilThreshold * MINUTES_IN_HOUR_FLOAT);
								}

								// add new timetable entry (learn time slot) to
								// timetable entry list
								hoursToSchedule -= learningSlotDurationMinutes / MINUTES_IN_HOUR_FLOAT;
								increaseScheduledDayHours(isDifficultCourse, currentInterval
										.getStartDateTime().getDayOfYear(), learningSlotDurationMinutes
										/ MINUTES_IN_HOUR_FLOAT);
								TimetableEntry learnSlotEntry = new TimetableEntry(course.getTitle(),
										"Learning Slot", currentInterval.getStartDateTime(), currentInterval
												.getStartDateTime().plusMinutes(learningSlotDurationMinutes),
										TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor);
								scheduledWorkLoad.add(learnSlotEntry);
								scheduledWorkLoad.add(new TimetableEntry("Break", "", learnSlotEntry
										.getEndDateTime(), learnSlotEntry.getEndDateTime().plusMinutes(
										(int) (neededHoursBreak * MINUTES_IN_HOUR_FLOAT)),
										TimetableEntryType.SCHEDULED_BREAKSLOT, null));
							}
						} else {
							// if free timeslot is too small
							tryNextIntervalSameDay = true;
						}
					} else {
						tryNextIntervalSameDay = true;
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
					// increase currentDateTime every while-iteration and if
					// there is already scheduled workload from
					// the previous scheduling, then iterate timetableEntryDay
					// as long it at least equals currentDateTime
					// in this case it will do nothing but again increase
					// currentDateTime and timetableEntryDay until
					// timetableEntryDay is located in the future (after
					// currentDateTime), then there is no scheduled
					// workload at this day --> use it for new scheduling

					// if timetableEntry day is before currentDateTime day, then
					// switch to next entry
					int timetableEntryDay = course.getDeadline().getDayOfYear();
					if (scheduledWorkLoad.size() > 0) {
						while ((timetableEntryIterator < scheduledWorkLoad.size() - 1)
								&& scheduledWorkLoad.get(timetableEntryIterator).getStartDateTime()
										.getDayOfYear() < currentDateTime.getDayOfYear()) {
							timetableEntryIterator++;
						}
						timetableEntryDay = scheduledWorkLoad.get(timetableEntryIterator).getStartDateTime()
								.getDayOfYear();
					}

					// next timetableentry (already stored learning slot) is
					// located
					// in the future
					// thus, this day hasn't scheduled slot
					if (timetableEntryDay > currentDateTime.getDayOfYear()) {

						// go to next free time slot blocks
						while (freeTimeSlots.get(freeTimeSlotIterator).getStartDateTime().getDayOfYear() <= currentDateTime
								.getDayOfYear() && freeTimeSlotIterator < (freeTimeSlots.size() - 1)) {
							DateTimeInterval interval = freeTimeSlots.get(freeTimeSlotIterator);
							if (interval.getStartDateTime().getDayOfYear() == currentDateTime.getDayOfYear()) {

								// get nearest start timestamp --> timestamp
								// within interval which is nearest to the
								// daytime interval
								DateTime nearestStartTimestamp = DateUtility.getNearestDayTimeMatch(
										interval.getStartDateTime(), interval.getEndDateTime(),
										customerPreferences.getPreferredLearningDayTime());

								// check if duration between
								// nearestStartTimestamp and interval-end is
								// more than/equal to neededHoursMaxSlotAndBreak
								// in this case define interval as
								// nearestStartTimestamp+neededHoursMaxSlotAndBreak
								// otherwise calculate new starttimestamp using
								// interval-end as origin
								if (DateUtility.getMinutesOfDuration(nearestStartTimestamp,
										interval.getEndDateTime()) < (neededHoursMaxSlotAndBreak * MINUTES_IN_HOUR)) {

									// check if interval is large enough to
									// store maxSlotAndBreak
									if (!interval
											.getEndDateTime()
											.minusMinutes(
													(int) (neededHoursMaxSlotAndBreak * MINUTES_IN_HOUR_FLOAT))
											.isBefore(interval.getStartDateTime())) {
										interval.setStartDateTime(interval.getEndDateTime().minusMinutes(
												(int) (neededHoursMaxSlotAndBreak * MINUTES_IN_HOUR_FLOAT)));
									}
								} else {
									interval.setStartDateTime(nearestStartTimestamp);
									interval.setEndDateTime(nearestStartTimestamp
											.plusMinutes((int) (neededHoursMaxSlotAndBreak * MINUTES_IN_HOUR_FLOAT)));
								}

								// compute intervalHours and bound interval if
								// hoursToSchedule is less than intervalHours
								double intervalHours = interval.getDurationInHours();
								int learningSlotDurationMinutes = 0;
								if (neededHoursMaxSlotAndBreak < hoursToSchedule
										&& intervalHours >= neededHoursMaxSlotAndBreak) {
									learningSlotDurationMinutes = (int) (neededHoursMaxSlot * MINUTES_IN_HOUR);
								} else if (neededHoursMaxSlotAndBreak >= hoursToSchedule
										&& intervalHours >= (hoursToSchedule + neededHoursBreak)) {
									learningSlotDurationMinutes = (int) (hoursToSchedule * MINUTES_IN_HOUR);
								}
								if (learningSlotDurationMinutes != 0) {

									// check if it's allowed to schedule
									// timeslot at this day considering maximal
									// workload per day
									double hoursUntilThreshold = hoursUntilWorkloadDayThresholdExceedance(
											isDifficultCourse, interval.getStartDateTime().getDayOfYear());
									if (hoursUntilThreshold >= neededHoursMinSlot) {
										if (hoursUntilThreshold < (learningSlotDurationMinutes / MINUTES_IN_HOUR_FLOAT)) {
											learningSlotDurationMinutes = (int) (hoursUntilThreshold * MINUTES_IN_HOUR_FLOAT);
										}
										hoursToSchedule -= learningSlotDurationMinutes
												/ MINUTES_IN_HOUR_FLOAT;
										increaseScheduledDayHours(isDifficultCourse, interval
												.getStartDateTime().getDayOfYear(),
												learningSlotDurationMinutes / MINUTES_IN_HOUR_FLOAT);
										TimetableEntry learnSlotEntry = new TimetableEntry(course.getTitle(),
												"Learning Slot", interval.getStartDateTime(), interval
														.getStartDateTime().plusMinutes(
																learningSlotDurationMinutes),
												TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor);
										scheduledWorkLoad.add(learnSlotEntry);
										scheduledWorkLoad.add(new TimetableEntry("Break", "", learnSlotEntry
												.getEndDateTime(),
												learnSlotEntry.getEndDateTime().plusMinutes(
														(int) (neededHoursBreak * MINUTES_IN_HOUR_FLOAT)),
												TimetableEntryType.SCHEDULED_BREAKSLOT, null));
										break;
									}
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
			// apply previous additions to timetable and recompute free time
			// slots
			// TODO: maybe not they fully correct approach to bluntly iterate
			// simply over free time slots -> better considering best
			// time slots (daytime) each day..
			while (hoursToSchedule > 0 && freeTimeSlots.size() != 0
					&& status != ScheduleStatus.ERROR_MAX_WORKLOAD_HOURS_REACHED) {
				freeTimeSlots = computeFreeTimeSlots(currentTimetable, course.getDeadline());
				Collections.sort(freeTimeSlots);
				int day = 0;
				for (DateTimeInterval interval : freeTimeSlots) {
					if (interval.getStartDateTime().getDayOfYear() > day) {
						day = interval.getStartDateTime().getDayOfYear();
					}
					if (hoursToSchedule > 0 && interval.getDurationInHours() >= neededHoursMinSlotAndBreak
							&& day == interval.getStartDateTime().getDayOfYear()) {
						// get nearest start timestamp --> timestamp within
						// interval which is nearest to the daytime interval
						DateTime nearestStartTimestamp = DateUtility.getNearestDayTimeMatch(
								interval.getStartDateTime(), interval.getEndDateTime(),
								customerPreferences.getPreferredLearningDayTime());

						// docu see iteration 2
						if (DateUtility
								.getMinutesOfDuration(nearestStartTimestamp, interval.getEndDateTime()) < (neededHoursMinSlotAndBreak * MINUTES_IN_HOUR)) {
							interval.setStartDateTime(interval.getEndDateTime().minusMinutes(
									(int) (neededHoursMinSlotAndBreak * MINUTES_IN_HOUR_FLOAT)));
						} else {
							interval.setStartDateTime(nearestStartTimestamp);
							interval.setEndDateTime(nearestStartTimestamp
									.plusMinutes((int) (neededHoursMinSlotAndBreak * MINUTES_IN_HOUR_FLOAT)));
						}

						int duration = 0;
						if ((hoursToSchedule + neededHoursBreak) <= neededHoursMinSlotAndBreak) {
							duration = (int) (hoursToSchedule * MINUTES_IN_HOUR);
						} else {
							duration = (int) (neededHoursMinSlot * MINUTES_IN_HOUR);
						}

						// check if it's allowed to schedule timeslot at this
						// day considering maximal workload per day
						double hoursUntilThreshold = hoursUntilWorkloadDayThresholdExceedance(
								isDifficultCourse, interval.getStartDateTime().getDayOfYear());
						if (hoursUntilThreshold >= neededHoursMinSlot) {
							TimetableEntry learnSlotEntry = new TimetableEntry(course.getTitle(),
									"Learning Slot", interval.getStartDateTime(), interval.getStartDateTime()
											.plusMinutes(duration),
									TimetableEntryType.SCHEDULED_LEARNINGSLOT, currentCourseColor);
							currentTimetable.add(learnSlotEntry);
							currentTimetable.add(new TimetableEntry("Break", "", learnSlotEntry
									.getEndDateTime(), learnSlotEntry.getEndDateTime().plusMinutes(
									(int) (neededHoursBreak * MINUTES_IN_HOUR_FLOAT)),
									TimetableEntryType.SCHEDULED_BREAKSLOT, null));
							hoursToSchedule -= duration / MINUTES_IN_HOUR_FLOAT;
							increaseScheduledDayHours(isDifficultCourse, interval.getStartDateTime()
									.getDayOfYear(), duration / MINUTES_IN_HOUR_FLOAT);
						}

						if (day >= course.getDeadline().getDayOfYear()) {
							status = ScheduleStatus.ERROR_MAX_WORKLOAD_HOURS_REACHED;
							break;
						}

						day++;
					}
					if (hoursToSchedule == 0) {
						break;
					}
				}
			}

			if (hoursToSchedule > 0 && status == ScheduleStatus.SUCCESS) {
				status = ScheduleStatus.ERROR_REACHED_DEADLINE;
			}
		} else {
			if (status != ScheduleStatus.ERROR_DEADLINE_IN_PAST) {
				status = ScheduleStatus.ERROR_NO_FREETIME_SLOTS;
			}
		}
		currentTimetableEntryList.setScheduledTimeSlotList(currentTimetable);
		currentTimetableEntryList.addCourseScheduleStatus(course.getTitle(), status);
		return currentTimetableEntryList;
	}

	private void increaseScheduledDayHours(boolean checkDifficultCourses, int dayOfYear, double scheduledHours) {
		if (checkDifficultCourses) {
			if (dayHoursOfDifficultCourses.containsKey(dayOfYear)) {
				dayHoursOfDifficultCourses.put(dayOfYear, dayHoursOfDifficultCourses.get(dayOfYear)
						+ scheduledHours);
			} else {
				dayHoursOfDifficultCourses.put(dayOfYear, scheduledHours);
			}
		} else {
			if (dayHoursOfEasyCourses.containsKey(dayOfYear)) {
				dayHoursOfEasyCourses.put(dayOfYear, dayHoursOfEasyCourses.get(dayOfYear) + scheduledHours);
			} else {
				dayHoursOfEasyCourses.put(dayOfYear, scheduledHours);
			}
		}
	}

	private double hoursUntilWorkloadDayThresholdExceedance(boolean checkDifficultCourses, int dayOfYear) {
		if (checkDifficultCourses) {
			if (dayHoursOfDifficultCourses.containsKey(dayOfYear)) {
				return MAX_DIFFICULT_LEARN_HOURS_PER_DAY - dayHoursOfDifficultCourses.get(dayOfYear);
			} else {
				return MAX_DIFFICULT_LEARN_HOURS_PER_DAY;
			}
		} else {
			if (dayHoursOfEasyCourses.containsKey(dayOfYear)) {
				return MAX_LEARN_HOURS_PER_DAY - MAX_DIFFICULT_LEARN_HOURS_PER_DAY
						- dayHoursOfEasyCourses.get(dayOfYear);
			} else {
				return MAX_LEARN_HOURS_PER_DAY - MAX_DIFFICULT_LEARN_HOURS_PER_DAY;
			}
		}
	}

	/**
	 * Returns the duration of all free time slots (as a sum of the
	 * subdurations).
	 */
	private double getAccumulatedFreeTimeHours(List<DateTimeInterval> freeTimeSlots) {
		double accumulatedHours = 0;
		for (DateTimeInterval currentInterval : freeTimeSlots) {
			accumulatedHours += DateUtility.getMinutesOfDuration(currentInterval.getStartDateTime(),
					currentInterval.getEndDateTime()) / MINUTES_IN_HOUR_FLOAT;
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
		if (extendedTimetableEntries == null) {
			extendedTimetableEntries = new ArrayList<TimetableEntry>();
		}
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
						DateTime startDateTime = new DateTime(date.getYear(), date.getMonthOfYear(),
								date.getDayOfMonth(), 0, 0);
						DateTime endDateTime = new DateTime(date.getYear(), date.getMonthOfYear(),
								date.getDayOfMonth(), 23, 59);
						TimetableEntry blockEntry = new TimetableEntry("Day Of Rest", "", startDateTime,
								endDateTime, TimetableEntryType.BLOCKED, null);
						extendedTimetableEntries.add(blockEntry);
						continue;
					}
				}

				for (TimeInterval timeInterval : restTimeIntervals) {
					DateTime startDateTime = new DateTime(date.getYear(), date.getMonthOfYear(),
							date.getDayOfMonth(), timeInterval.getTime1().getHourOfDay(), timeInterval
									.getTime1().getMinuteOfHour());
					DateTime endDateTime = new DateTime(date.getYear(), date.getMonthOfYear(),
							date.getDayOfMonth(), timeInterval.getTime2().getHourOfDay(), timeInterval
									.getTime2().getMinuteOfHour());
					TimetableEntry blockEntry = new TimetableEntry("Free Time", "", startDateTime,
							endDateTime, TimetableEntryType.BLOCKED, null);
					extendedTimetableEntries.add(blockEntry);
				}
			}
		}

		return extendedTimetableEntries;
	}

	/**
	 * Computes time slots where scheduling of learn time slots is allowed.
	 */
	private List<DateTimeInterval> computeFreeTimeSlots(List<TimetableEntry> currentCalendarData,
			DateTime deadline) {
		List<DateTimeInterval> freeTimeSlots = new ArrayList<DateTimeInterval>();
		if (currentCalendarData != null) {
			Collections.sort(currentCalendarData);
			DateTime startTimestamp = DateTime.now().withHourOfDay(23).withMinuteOfHour(59)
					.withSecondOfMinute(0).withMillisOfSecond(0);
			DateTime potentialTimeSlotBegin = startTimestamp;

			// look through all timetable-entries and modify potential
			// timeslotbegin-position
			// on the fly
			// this procedure obtains the free time slots
			for (TimetableEntry entry : currentCalendarData) {
				if (entry.getStartDateTime().isAfter(potentialTimeSlotBegin)
						&& entry.getStartDateTime().isBefore(deadline)
						&& entry.getEndDateTime().isBefore(deadline)) {

					// if free timeslot is spanned multiple days, than separate
					// into
					// multiple outcome datasets
					if (entry.getStartDateTime().getDayOfYear() != potentialTimeSlotBegin.getDayOfYear()) {

						// get timeslot of current day
						DateTime timeSlotBeginFirst = potentialTimeSlotBegin;
						DateTime timeSlotEndFirst = potentialTimeSlotBegin.withHourOfDay(23)
								.withMinuteOfHour(59);
						if (DateUtility.getMinutesOfDuration(timeSlotBeginFirst, timeSlotEndFirst)
								/ MINUTES_IN_HOUR_FLOAT >= neededHoursMinSlotAndBreak) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginFirst, timeSlotEndFirst));
						}

						// get timeslot of day when next calendar entry starts
						DateTime timeSlotBeginLast = entry.getStartDateTime().withHourOfDay(0)
								.withMinuteOfHour(0);
						DateTime timeSlotEndLast = entry.getStartDateTime();
						if (DateUtility.getMinutesOfDuration(timeSlotBeginLast, timeSlotEndLast)
								/ MINUTES_IN_HOUR_FLOAT >= neededHoursMinSlotAndBreak) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginLast, timeSlotEndLast));
						}

						// get all-day-timeslots of days between
						for (int i = potentialTimeSlotBegin.getDayOfYear() + 1; i < entry.getStartDateTime()
								.getDayOfYear(); i++) {
							DateTime timeSlotBeginBetween = (new DateTime()).withDayOfYear(i)
									.withHourOfDay(0).withMinuteOfHour(0);
							DateTime timeSlotEndBetween = (new DateTime()).withDayOfYear(i).withHourOfDay(23)
									.withMinuteOfHour(59);
							if (DateUtility.getMinutesOfDuration(timeSlotBeginBetween, timeSlotEndBetween)
									/ MINUTES_IN_HOUR_FLOAT >= neededHoursMinSlotAndBreak) {
								freeTimeSlots.add(new DateTimeInterval(timeSlotBeginBetween,
										timeSlotEndBetween));
							}
						}
					} else {

						// get timeslot of current day
						DateTime timeSlotBegin = potentialTimeSlotBegin;
						DateTime timeSlotEnd = entry.getStartDateTime();
						if (DateUtility.getMinutesOfDuration(timeSlotBegin, timeSlotEnd)
								/ MINUTES_IN_HOUR_FLOAT >= neededHoursMinSlotAndBreak) {
							freeTimeSlots.add(new DateTimeInterval(timeSlotBegin, timeSlotEnd));
						}
					}
				}
				if (entry.getEndDateTime().isAfter(potentialTimeSlotBegin)) {
					// set new potentialTimeSlotBegin
					potentialTimeSlotBegin = entry.getEndDateTime();
				}
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
