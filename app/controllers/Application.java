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
import play.mvc.Controller;
import play.mvc.With;
import models.timetable.PersonalizedTimetable;
import models.timetable.TimetableEntry;

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
    	PersonalizedTimetable timetable = new PersonalizedTimetable();
    	List<TimetableEntry> entryList = timetable.scheduleTimeSlots();
        
    	render("Application/timetable.html", entryList);
    }
}