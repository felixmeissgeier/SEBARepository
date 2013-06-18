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
	
}
