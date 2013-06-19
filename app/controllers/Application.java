package controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import models.calendar.CalendarEntry;
import models.calendar.CalendarFeed;
import models.calendar.GoogleCalendarConnector;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
        render();
    }
    
    
    public static void courses() {
    	render("Application/courses.html");
    }
    
    public static void settings(String category) {
    	if(category==null){
    		category = "personalData";
    	}
    	render("Application/settings.html",category);
    }

    public static void timetable() {
    	GoogleCalendarConnector calConnector = null;
    	String errorString = "";
    	try {
			calConnector = new GoogleCalendarConnector(
					new URL("https://www.google.com/calendar/ical/schmi.kalle%40gmail.com/private-82d40d6ada06fa5825d8910c4949e64f/basic.ics"));
		} catch (MalformedURLException e) {
			errorString += e.toString();
		}
		
    	CalendarFeed feed = calConnector.receiveCalendarFeed();
 	
    	List<CalendarEntry> entryList = null;
    	if(feed!=null){
    		entryList = (List<CalendarEntry>) feed.getEntryList();
    	}   	
        
    	render("Application/timetable.html", entryList, errorString);
    }
}