package controllers;

import play.*;
import play.mvc.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import models.*;
import models.calendar.CalendarEntry;
import models.calendar.CalendarFeed;
import models.calendar.GoogleCalendarConnector;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    
    

    public static void test() {
    	GoogleCalendarConnector calConnector = null;
    	String errorString = "";
    	try {
			calConnector = new GoogleCalendarConnector(new URL("https://www.google.com/calendar/ical/felixmeissgeier%40gmail.com/private-0117a3fd278c58a3b44f04ca66702054/basic.ics"));
		} catch (MalformedURLException e) {
			errorString += e.toString();
		}
		
    	CalendarFeed feed = calConnector.receiveCalendarFeed();
 	
    	List<CalendarEntry> entryList = null;
    	if(feed!=null){
    		entryList = (List<CalendarEntry>) feed.getEntryList();
    	}   	
        
    	render("Application/test.html", entryList, errorString);
    }
}