package models;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * Helper class used to provide auxiliary routines for date/time support.
 * 
 */
public class DateUtility {

	/**
	 * Defines time of studying.
	 * 
	 */
	public enum LearningDayTime {
		MORNING, AFTERNOON, EVENING, NIGHT
	}

	/**
	 * Defines a day of the week. Used as preferred/unpreferred days of studying.
	 * 
	 */
	public enum Day {
		Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
	}

	/**
	 * Converts day index into the value of {@link Day} type.
	 * 
	 * @param ordinalNumber
	 *          Day index
	 * @return Day corresponding to the index
	 */
	public static Day getDayConstByOrdinal(int ordinalNumber) {
		Day[] dayConsts = Day.values();
		for (int i = 0; i < dayConsts.length; i++) {
			if (dayConsts[i].ordinal() == ordinalNumber) {
				return dayConsts[i];
			}
		}

		return null;
	}

	/**
	 * Returns the interval in minutes between two daytime objects.
	 */
	public static int getMinutesOfDuration(DateTime date1, DateTime date2) {
		return new Period(date1, date2, PeriodType.forFields(new DurationFieldType[] { DurationFieldType.minutes() })).getMinutes();
	}

	/**
	 * Returns the interval in days between two daytime objects.
	 */
	public static int getDaysOfDuration(DateTime date1, DateTime date2) {
		return new Period(date1, date2, PeriodType.forFields(new DurationFieldType[] { DurationFieldType.days() })).getDays();
	}

	/**
	 * Returns best start time within given interval by taking into account the
	 * preferredDayTime value. Interval has to be at the provided day.
	 * 
	 * @param startDateTime
	 * @param endDateTime
	 * @param preferredDayTime
	 * @return
	 */
	public static DateTime getBestDayTimeMatch(DateTime startDateTime, DateTime endDateTime, LearningDayTime preferredDayTime) {
		DateTime nearestStartTime = getNearestDayTimeMatch(startDateTime, endDateTime, preferredDayTime);
		DateTimeInterval prefDayTimeInterval = getDayTimeInterval(startDateTime, endDateTime, preferredDayTime);
		if(nearestStartTime.isBefore(prefDayTimeInterval.getStartDateTime()) || nearestStartTime.isAfter(prefDayTimeInterval.getEndDateTime())){
			return null;
		}

		return nearestStartTime;
	}
	
	public static DateTime getNearestDayTimeMatch(DateTime startDateTime, DateTime endDateTime, LearningDayTime preferredDayTime){
		DateTimeInterval prefDayTimeInterval = getDayTimeInterval(startDateTime, endDateTime, preferredDayTime);
		DateTime prefDayTimeStart = prefDayTimeInterval.getStartDateTime();
		DateTime prefDayTimeEnd = prefDayTimeInterval.getEndDateTime();
		DateTime bestStartTime = startDateTime;		

		if ((startDateTime.isBefore(prefDayTimeStart) || startDateTime.isEqual(prefDayTimeStart)) && endDateTime.isAfter(prefDayTimeStart)) {
			bestStartTime = bestStartTime.withHourOfDay(prefDayTimeStart.getHourOfDay()).withMinuteOfHour(prefDayTimeStart.getMinuteOfHour());
		} 
		else if (startDateTime.isAfter(prefDayTimeStart) && startDateTime.isBefore(prefDayTimeEnd)) {
			bestStartTime = bestStartTime.withHourOfDay(startDateTime.getHourOfDay()).withMinuteOfHour(startDateTime.getMinuteOfHour());
		} 
		else if(startDateTime.isAfter(prefDayTimeEnd) || startDateTime.isEqual(prefDayTimeEnd)){
			bestStartTime = startDateTime;
		}
		else if(endDateTime.isBefore(prefDayTimeStart) || endDateTime.isEqual(prefDayTimeStart)){
			bestStartTime = endDateTime;
		}
	
		return bestStartTime;
	}
	
	private static DateTimeInterval getDayTimeInterval(DateTime startDateTime, DateTime endDateTime, LearningDayTime preferredDayTime){
		DateTimeInterval dayTimeInterval = new DateTimeInterval();
		if (preferredDayTime.equals(LearningDayTime.MORNING)) {
			dayTimeInterval.setStartDateTime(startDateTime.withHourOfDay(7).withMinuteOfHour(0));
			dayTimeInterval.setEndDateTime(endDateTime.withHourOfDay(12).withMinuteOfHour(59));
		}
		if (preferredDayTime.equals(LearningDayTime.AFTERNOON)) {
			dayTimeInterval.setStartDateTime(startDateTime.withHourOfDay(13).withMinuteOfHour(0));
			dayTimeInterval.setEndDateTime(endDateTime.withHourOfDay(17).withMinuteOfHour(59));
		}
		if (preferredDayTime.equals(LearningDayTime.EVENING)) {
			dayTimeInterval.setStartDateTime(startDateTime.withHourOfDay(18).withMinuteOfHour(0));
			dayTimeInterval.setEndDateTime(endDateTime.withHourOfDay(21).withMinuteOfHour(59));
		}
		if (preferredDayTime.equals(LearningDayTime.NIGHT)) {
			dayTimeInterval.setStartDateTime(startDateTime.withHourOfDay(0).withMinuteOfHour(0));
			dayTimeInterval.setEndDateTime(endDateTime.withHourOfDay(6).withMinuteOfHour(59));
		}
		
		return dayTimeInterval;
	}
}
