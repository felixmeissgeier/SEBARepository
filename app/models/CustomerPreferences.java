package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

import models.DateUtility.Day;
import models.DateUtility.LearningDayTime;

import org.joda.time.LocalTime;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Stores the studying preferences of the user. Used to calculate the study plan
 * an intervals for the user.
 * 
 */
@Embeddable
public class CustomerPreferences {

    private LearningDayTime preferredLearningDayTime = LearningDayTime.AFTERNOON;
    private double maxLearningSlotDuration;
    private double minLearningSlotDuration = 0.5f;
    private double breakDuration = 0.25f;

    @ElementCollection
    private List<Day> daysOfRest;

    @ElementCollection
    private List<TimeInterval> timeIntervalsOfRest;

    // @ElementCollection
    // private List<TimeInterval2> timeIntervalsOfRest2;

    public CustomerPreferences() {
	this.preferredLearningDayTime = LearningDayTime.AFTERNOON;
	this.maxLearningSlotDuration = 1.0f;
	this.minLearningSlotDuration = 0.5f;
	this.breakDuration = 0.25f;
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
	System.out.println("> setting day of rest: " + Arrays.toString(daysOfRest.toArray()));
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

	    int intervalsNumber = timeValues.size() / 4;
	    for (int i = 0; i < intervalsNumber; i++) {
		TimeInterval timeInterval = new TimeInterval(new LocalTime(timeValues.get(i * 4),
			timeValues.get(i * 4 + 1)), new LocalTime(timeValues.get(i * 4 + 2), timeValues.get(i * 4 + 3)));
		this.timeIntervalsOfRest.add(timeInterval);
	    }
	}
    }
}
