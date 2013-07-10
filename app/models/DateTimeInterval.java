package models;

import org.joda.time.DateTime;

/**
 * Represents a datetime interval defined by two datetime objects.
 * 
 */
public class DateTimeInterval implements Comparable<DateTimeInterval> {

	private static final double MINUTES_IN_HOUR = 60.0;

	private DateTime dateTime1 = null;
	private DateTime dateTime2 = null;

	public DateTimeInterval(DateTimeInterval interval) {
		this.dateTime1 = interval.getStartDateTime();
		this.dateTime2 = interval.getEndDateTime();
	}

	public DateTimeInterval() {
		dateTime1 = new DateTime();
		dateTime2 = new DateTime();
	}

	public DateTimeInterval(DateTime dateTime1, DateTime dateTime2) {
		this.dateTime1 = dateTime1;
		this.dateTime2 = dateTime2;
	}

	public DateTimeInterval(long dateTime1, long dateTime2) {
		this.dateTime1 = new DateTime(dateTime1);
		this.dateTime2 = new DateTime(dateTime2);
	}

	public DateTime getStartDateTime() {
		return dateTime1;
	}

	public DateTime getEndDateTime() {
		return dateTime2;
	}

	public void setStartDateTime(DateTime startDateTime) {
		this.dateTime1 = startDateTime;
	}

	public void setEndDateTime(DateTime endDateTime) {
		this.dateTime2 = endDateTime;
	}

	public double getDurationInHours() {
		return DateUtility.getMinutesOfDuration(dateTime1, dateTime2) / MINUTES_IN_HOUR;
	}

	@Override
	public int compareTo(DateTimeInterval o) {
		if (o.dateTime1.isAfter(this.dateTime1)) {
			return -1;
		} else if (o.dateTime1.isEqual(this.dateTime1)) {
			return 0;
		} else if (o.dateTime1.isBefore(this.dateTime1)) {
			return 1;
		}

		return 0;
	}
}
