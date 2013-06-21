package models.timetable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import dto.CourseDTO;

import java.util.Collections;

import models.CustomerPreferences;
import models.DateTimeInterval;
import models.DateUtility;
import models.DateUtility.Day;
import models.TimeInterval;
import models.timetable.TimetableEntry.TimetableEntryType;

public class TimeSlotScheduler {
	private List<CourseDTO> courses;
	private CustomerPreferences customerPreferences;
	private double minSlotDuration = 0;
	
	public TimeSlotScheduler(List<CourseDTO> courses, CustomerPreferences customerPreferences){
		this.courses = courses;
		this.customerPreferences = customerPreferences;
		this.minSlotDuration = customerPreferences.getMinLearningSlotDuration();
	}
	
	public List<TimetableEntry> scheduleTimeSlots(List<TimetableEntry> externalCalendarData){
		List<TimetableEntry> scheduledTimeSlotList = null;
		TreeMap<String, CourseDTO> priorizedCourseMap = getCoursePriorityMap(courses);		
		
		scheduledTimeSlotList = addBlockedTimeSlots(externalCalendarData, getLatestCourseDeadline());
		
		//iterate over courses, depending on priorities
		for(CourseDTO course : priorizedCourseMap.values()){
			scheduledTimeSlotList = computeCourseLearningSlots(scheduledTimeSlotList, course);
		}		
		
		return scheduledTimeSlotList;
	}
	
	private List<TimetableEntry> computeCourseLearningSlots(List<TimetableEntry> currentTimetable, CourseDTO course){
		List<DateTimeInterval> freeTimeSlots = computeFreeTimeSlots(currentTimetable, course.getDeadline());
		for(DateTimeInterval interval : freeTimeSlots){
			TimetableEntry entry = new TimetableEntry("Pot.Scheduling", "", interval.getStartDateTime(), interval.getEndDateTime(), TimetableEntryType.BLOCKED);
			currentTimetable.add(entry);
		}
		return currentTimetable;
	}
	
	private TreeMap<String,CourseDTO> getCoursePriorityMap(List<CourseDTO> courses){
		TreeMap<String, CourseDTO> courseMap = new TreeMap<String, CourseDTO>();
		for(CourseDTO course : courses){
			courseMap.put(String.valueOf(course.getPriority()), course);
		}
		
		return courseMap;
	}
	
	private List<TimetableEntry> addBlockedTimeSlots(List<TimetableEntry> timetableEntries, DateTime deadline){
		List<TimetableEntry> extendedTimetableEntries = timetableEntries;
		Collections.sort(extendedTimetableEntries);
		DateTime date = new DateTime();
		List<TimeInterval> restTimeIntervals = customerPreferences.getTimeIntervalsOfRest();
		List<Day> restDays = customerPreferences.getDaysOfRest();		
		
		if(restTimeIntervals!=null){
			Collections.sort(restTimeIntervals);
			//for each day till deadline add block-entries into timetable
			while(date.isBefore(deadline)){
				date = date.plusDays(1);
				if(restDays!=null){
					if(restDays.contains(DateUtility.getDayConstByOrdinal(date.getDayOfWeek()-1))){
						DateTime startDateTime = new DateTime(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth(),0,0);
						DateTime endDateTime = new DateTime(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth(),23,59);
						TimetableEntry blockEntry = new TimetableEntry("All Day Block","",startDateTime,endDateTime,TimetableEntryType.BLOCKED);
						extendedTimetableEntries.add(blockEntry);
						continue;
					}
				}
				
				for(TimeInterval timeInterval : restTimeIntervals){
					DateTime startDateTime = new DateTime(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth(),timeInterval.getTime1().getHourOfDay(),timeInterval.getTime1().getMinuteOfHour());
					DateTime endDateTime = new DateTime(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth(),timeInterval.getTime2().getHourOfDay(),timeInterval.getTime2().getMinuteOfHour());
					TimetableEntry blockEntry = new TimetableEntry("Free Time Slot","",startDateTime,endDateTime,TimetableEntryType.BLOCKED);
					extendedTimetableEntries.add(blockEntry);	
				}				
			}
		}
		
		return extendedTimetableEntries;
	}
	
	private List<DateTimeInterval> computeFreeTimeSlots(List<TimetableEntry> currentCalendarData, DateTime deadline){
		List<DateTimeInterval> freeTimeSlots = new ArrayList<DateTimeInterval>();
		if(currentCalendarData!=null){			
			Collections.sort(currentCalendarData);
			DateTime potentialTimeSlotBegin = new DateTime();
			
			//look through all timetable-entries and modify potential timeslotbegin-position
			//on the fly
			//this procedure obtains the free time slots
			for(TimetableEntry entry : currentCalendarData){
				if(entry.getStartDateTime().isAfter(potentialTimeSlotBegin)){
					
					//if free timeslot is spanned multiple days, than separate into 
					//multiple outcome datasets
					if(entry.getStartDateTime().getDayOfYear()!=potentialTimeSlotBegin.getDayOfYear()){
						//get timeslot of current day
						DateTime timeSlotBeginFirst = potentialTimeSlotBegin;
						DateTime timeSlotEndFirst = potentialTimeSlotBegin.withHourOfDay(23).withMinuteOfHour(59);
						if(DateUtility.getMinutesOfDuration(timeSlotBeginFirst,timeSlotEndFirst)/60.0>=minSlotDuration){
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginFirst, timeSlotEndFirst));
						}
						
						//get timeslot of day when next calendar entry starts
						DateTime timeSlotBeginLast = entry.getStartDateTime().withHourOfDay(0).withMinuteOfHour(0);
						DateTime timeSlotEndLast = entry.getStartDateTime();
						if(DateUtility.getMinutesOfDuration(timeSlotBeginLast,timeSlotEndLast)/60.0>=minSlotDuration){
							freeTimeSlots.add(new DateTimeInterval(timeSlotBeginLast, timeSlotEndLast));
						}
						//get all-day-timeslots of days between
						for(int i=potentialTimeSlotBegin.getDayOfYear()+1;i<entry.getStartDateTime().getDayOfYear();i++){
							DateTime timeSlotBeginBetween = (new DateTime()).withDayOfYear(i).withHourOfDay(0).withMinuteOfHour(0);
							DateTime timeSlotEndBetween = (new DateTime()).withDayOfYear(i).withHourOfDay(23).withMinuteOfHour(59);
							if(DateUtility.getMinutesOfDuration(timeSlotBeginBetween,timeSlotEndBetween)/60.0>=minSlotDuration){
								freeTimeSlots.add(new DateTimeInterval(timeSlotBeginBetween, timeSlotEndBetween));
							}
						}
					}
					else{
						//get timeslot of current day
						DateTime timeSlotBegin = potentialTimeSlotBegin;
						DateTime timeSlotEnd = entry.getStartDateTime();
						if(DateUtility.getMinutesOfDuration(timeSlotBegin,timeSlotEnd)/60.0>=minSlotDuration){
							freeTimeSlots.add(new DateTimeInterval(timeSlotBegin, timeSlotEnd));
						}
					}
				}
				
				//set new potentialTimeSlotBegin
				potentialTimeSlotBegin = entry.getEndDateTime();
			}
			
		}
		return freeTimeSlots;
	}
	
	private DateTime getLatestCourseDeadline(){
		DateTime latestDeadline = new DateTime();
		
		for(CourseDTO course : courses){
			if(course.getDeadline().isAfter(latestDeadline)){
				latestDeadline = course.getDeadline();
			}
		}
		
		return latestDeadline;
	}
	
}
