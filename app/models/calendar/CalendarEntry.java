package models.calendar;

import org.joda.time.DateTime;
import org.simpleframework.xml.Element;

public class CalendarEntry {

	private String id;
	private DateTime lastModified;
	private String title;
	private DateTime startDate;
	private DateTime endDate;
	
	
	public String getID(){
		return id;
	}
	
	public void setID(String id){
		this.id = id;
	}
		
	public DateTime getLastModified(){
		return lastModified;
	}
	
	public void setLastModified(DateTime lastModified){
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public DateTime getStartDate(){
		return startDate;
	}
	
	public void setStartDate(DateTime startDate){
		this.startDate = startDate;
	}
	
	public DateTime getEndDate(){
		return endDate;
	}
	
	public void setEndDate(DateTime endDate){
		this.endDate = endDate;
	}
	
}
