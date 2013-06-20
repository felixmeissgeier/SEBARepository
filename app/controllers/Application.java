package controllers;

import java.util.List;

import models.timetable.PersonalizedTimetable;
import models.timetable.TimetableEntry;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {
	
	private static final String SETTINGS_PERSONAL_DATA = "personalData";
	private static final String SETTINGS_IMPORT = "import";
	private static final String SETTINGS_PAYMENT = "payment";

    public static void index() {
        render();
    }
    
    public static void courses() {
    	render("Application/courses.html");
    }
    
    public static void settings(String category) {
    	if (SETTINGS_IMPORT.equals(category)) {
        	render("Application/import.html");
    	}
    	else if (SETTINGS_PAYMENT.equals(category)) {
        	render("Application/payment.html");
    	}
    	else {
        	render("Application/personaldata.html");
    	}
    }

    public static void timetable() {
    	PersonalizedTimetable timetable = new PersonalizedTimetable();
    	List<TimetableEntry> entryList = timetable.scheduleTimeSlots();
        
    	render("Application/timetable.html", entryList);
    }
}