package controllers;

import java.util.List;

import models.User;
import models.timetable.PersonalizedTimetable;
import models.timetable.TimetableEntry;
import play.mvc.Controller;
import play.mvc.With;
import dto.UserDTO;

@With(Secure.class)
public class Application extends Controller {
	
	public static final String SETTINGS_PERSONAL_DATA = "personalData";
	public static final String SETTINGS_IMPORT = "import";
	public static final String SETTINGS_PAYMENT = "payment";

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
    		String currentUser = session.get(Security.SESSION_USERNAME);
    		User user = User.find("byEmail", currentUser).first();
    		UserDTO userDto = new UserDTO(user);
    		renderArgs.put("user", userDto);
        	render("Application/personaldata.html");
    	}
    }
    
    public static void savePersonalData() {
    	redirect("Application.settings", SETTINGS_PERSONAL_DATA);
    }

    public static void timetable() {
    	PersonalizedTimetable timetable = new PersonalizedTimetable();
    	List<TimetableEntry> entryList = timetable.scheduleTimeSlots();
        
    	render("Application/timetable.html", entryList);
    }
}