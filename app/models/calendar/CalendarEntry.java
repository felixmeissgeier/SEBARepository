package models.calendar;

import java.util.Date;

import org.simpleframework.xml.Element;

public class CalendarEntry {

	private String id;
	private Date lastModified;
	private String title;
	private Date startDate;
	private Date endDate;
	
	
	public String getID(){
		return id;
	}
	
	public void setID(String id){
		this.id = id;
	}
		
	public Date getLastModified(){
		return lastModified;
	}
	
	public void setLastModified(Date lastModified){
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
	
	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}
	
}
