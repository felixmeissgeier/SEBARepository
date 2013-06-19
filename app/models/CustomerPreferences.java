package models;

import java.util.List;
import java.util.TreeMap;

import models.DateUtility.Day;
import models.DateUtility.LearningDayTime;

public class CustomerPreferences {
	
	private LearningDayTime preferredLearningDayTime = LearningDayTime.AFTERNOON;
	private double maxLearningSlotDuration;
	private double minLearningSlotDuration = 0.5f;
	private double breakDuration = 0.25f;
	private List<Day> daysOfRest;
	private List<TimeInterval> timeIntervalsOfRest;
	
	public CustomerPreferences(){
		this.preferredLearningDayTime = LearningDayTime.AFTERNOON;
		this.maxLearningSlotDuration = 1.0f;
		this.minLearningSlotDuration = 0.5f;
		this.breakDuration = 0.25f;
	}
	
	public CustomerPreferences(LearningDayTime preferredLearningDayTime, double maxLearningSlotDuration, double minLearningSlotDuration, double breakDuration){
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
	
	
}
