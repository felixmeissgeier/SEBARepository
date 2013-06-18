package models;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class TimeInterval implements Comparable<TimeInterval>{
	
	private LocalTime time1 = null;
	private LocalTime time2 = null;
	
	public TimeInterval(){
		setTime1(new LocalTime());
		setTime2(new LocalTime());
	}
	
	public TimeInterval(LocalTime time1, LocalTime time2){
		this.time1 = time1;
		this.time2 = time2;
	}

	public LocalTime getTime1() {
		return time1;
	}

	public void setTime1(LocalTime time1) {
		this.time1 = time1;
	}

	public LocalTime getTime2() {
		return time2;
	}

	public void setTime2(LocalTime time2) {
		this.time2 = time2;
	}

	@Override
	public int compareTo(TimeInterval o) {
		if(o.getTime1().isAfter(this.time1)){
			return -1;
		}
		else if(o.time1.isEqual(this.time1)){
			return 0;
		}
		else if(o.time1.isBefore(this.time1)){
			return 1;
		}
		return 0;
	}
	
	
	
}
