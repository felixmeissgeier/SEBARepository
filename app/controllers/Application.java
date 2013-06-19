package controllers;

import java.util.List;

import models.timetable.PersonalizedTimetable;
import models.timetable.TimetableEntry;
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
    	PersonalizedTimetable timetable = new PersonalizedTimetable();
    	List<TimetableEntry> entryList = timetable.scheduleTimeSlots();
        
    	render("Application/timetable.html", entryList);
    }
}