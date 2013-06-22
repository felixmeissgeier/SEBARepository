package models;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class DateUtility {
	public enum LearningDayTime{
		MORNING,
		AFTERNOON,
		EVENING,
		NIGHT
	}
	
	public enum Day{
		Monday,
		Tuesday,
		Wednesday,
		Thursday,
		Friday,
		Saturday,
		Sunday
	}
	
	public static Day getDayConstByOrdinal(int ordinalNumber){
		Day[] dayConsts = Day.values();
		for(int i=0;i<dayConsts.length;i++){
			if(dayConsts[i].ordinal()==ordinalNumber){
				return dayConsts[i];
			}
		}
		
		return null;
	}
	
	public static int getMinutesOfDuration(DateTime date1, DateTime date2){
		return new Period(date1,date2,PeriodType.forFields(new DurationFieldType[]{DurationFieldType.minutes()})).getMinutes();
	}
	
	public static int getDaysOfDuration(DateTime date1, DateTime date2){
		return new Period(date1,date2,PeriodType.forFields(new DurationFieldType[]{DurationFieldType.days()})).getDays();
	}
	
	//returns best starttime regarding to preferredDayTime within given interval
	//interval has to be at same day!!
	public static DateTime getBestDayTimeMatch(DateTime startDateTime, DateTime endDateTime, LearningDayTime preferredDayTime){
		DateTime prefDayTimeStart = startDateTime;
		DateTime prefDayTimeEnd = endDateTime;
		DateTime bestStartTime = startDateTime;
		
		if(preferredDayTime.equals(LearningDayTime.MORNING)){
			prefDayTimeStart = prefDayTimeStart.withHourOfDay(7).withMinuteOfHour(0);
			prefDayTimeEnd = prefDayTimeEnd.withHourOfDay(12).withMinuteOfHour(59);
		}
		if(preferredDayTime.equals(LearningDayTime.AFTERNOON)){
			prefDayTimeStart = prefDayTimeStart.withHourOfDay(13).withMinuteOfHour(0);
			prefDayTimeEnd = prefDayTimeEnd.withHourOfDay(17).withMinuteOfHour(59);
		}
		if(preferredDayTime.equals(LearningDayTime.EVENING)){
			prefDayTimeStart = prefDayTimeStart.withHourOfDay(18).withMinuteOfHour(0);
			prefDayTimeEnd = prefDayTimeEnd.withHourOfDay(21).withMinuteOfHour(59);
		}
		if(preferredDayTime.equals(LearningDayTime.NIGHT)){
			prefDayTimeStart = prefDayTimeStart.withHourOfDay(0).withMinuteOfHour(0);
			prefDayTimeEnd = prefDayTimeEnd.withHourOfDay(6).withMinuteOfHour(59);
		}
		
		if((startDateTime.isBefore(prefDayTimeStart) || startDateTime.isEqual(prefDayTimeStart)) && endDateTime.isAfter(prefDayTimeStart)){
			bestStartTime = bestStartTime.withHourOfDay(prefDayTimeStart.getHourOfDay()).withMinuteOfHour(prefDayTimeStart.getMinuteOfHour());
		}
		else if(startDateTime.isAfter(prefDayTimeStart) && startDateTime.isBefore(prefDayTimeEnd)){
			bestStartTime = bestStartTime.withHourOfDay(startDateTime.getHourOfDay()).withMinuteOfHour(startDateTime.getMinuteOfHour());
		}
		else{
			return null;
		}
		
		return bestStartTime;
	}
}
