package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

import models.DateUtility.Day;
import models.DateUtility.LearningDayTime;

import org.joda.time.LocalTime;

/**
 * Stores the studying preferences of the user. Used to calculate the study plan
 * an intervals for the user.
 * 
 */
@Embeddable
public class CustomerPreferences {

	private static final float DEFAULT_BREAK_DURATION = 0.25f;
	private static final float DEFAULT_MAX_LEARNING_SLOT_DURATION = 1.0f;
	private static final float DEFAULT_MIN_LEARNING_SLOT_DURATION = 0.5f;
	
	private LearningDayTime preferredLearningDayTime;
	private double maxLearningSlotDuration;
	private double minLearningSlotDuration;
	private double breakDuration;

	@ElementCollection
	private List<Day> daysOfRest;

	@ElementCollection
	private List<TimeInterval> timeIntervalsOfRest;

	public CustomerPreferences() {
		this.preferredLearningDayTime = LearningDayTime.AFTERNOON;
		this.maxLearningSlotDuration = DEFAULT_MAX_LEARNING_SLOT_DURATION;
		this.minLearningSlotDuration = DEFAULT_MIN_LEARNING_SLOT_DURATION;
		this.breakDuration = DEFAULT_BREAK_DURATION;
	}

	public CustomerPreferences(LearningDayTime preferredLearningDayTime, double maxLearningSlotDuration,
			double minLearningSlotDuration, double breakDuration) {
		this.preferredLearningDayTime = preferredLearningDayTime;
		this.maxLearningSlotDuration = maxLearningSlotDuration;
		this.minLearningSlotDuration = minLearningSlotDuration;
		this.breakDuration = breakDuration;
	}

	public LearningDayTime getPreferredLearningDayTime() {
		return preferredLearningDayTime;
	}

	public void setPreferredLearningDayTime(LearningDayTime preferredLearningDayTime) {
		this.preferredLearningDayTime = preferredLearningDayTime;
	}

	public double getMaxLearningSlotDuration() {
		return maxLearningSlotDuration;
	}

	public void setMaxLearningSlotDuration(double maxLearningSlotDuration) {
		this.maxLearningSlotDuration = maxLearningSlotDuration;
	}

	public double getMinLearningSlotDuration() {
		return minLearningSlotDuration;
	}

	public void setMinLearningSlotDuration(double minLearningSlotDuration) {
		this.minLearningSlotDuration = minLearningSlotDuration;
	}

	public double getBreakDuration() {
		return breakDuration;
	}

	public void setBreakDuration(double breakDuration) {
		this.breakDuration = breakDuration;
	}

	public List<Day> getDaysOfRest() {
		return daysOfRest;
	}

	public void setDaysOfRest(List<Day> daysOfRest) {
		this.daysOfRest = daysOfRest;
	}

	public List<TimeInterval> getTimeIntervalsOfRest() {
		return timeIntervalsOfRest;
	}

	public void setTimeIntervalsOfRest(List<TimeInterval> timeIntervalsOfRest) {
		this.timeIntervalsOfRest = timeIntervalsOfRest;
	}

	public void setIntervalsOfRest(List<Integer> timeValues) {
		if (timeValues != null) {
			if (this.timeIntervalsOfRest == null) {
				this.timeIntervalsOfRest = new ArrayList<TimeInterval>();
			}

			// CSOFF: MagicNumber
			int intervalsNumber = timeValues.size() / 4;
			for (int i = 0; i < intervalsNumber; i++) {
				TimeInterval timeInterval = new TimeInterval(new LocalTime(timeValues.get(i * 4),
						timeValues.get(i * 4 + 1)), new LocalTime(timeValues.get(i * 4 + 2),
						timeValues.get(i * 4 + 3)));
				this.timeIntervalsOfRest.add(timeInterval);
			}
			// CSON: MagicNumber
		}
	}
}
