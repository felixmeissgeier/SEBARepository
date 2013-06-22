package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Course;
import models.User;
import models.timetable.PersonalizedTimetable;
import models.timetable.TimetableEntry;
import play.mvc.With;
import dto.CourseDTO;
import dto.UserDTO;

/**
 * Main controller of the application. Handles requests to the all content
 * pages.
 * 
 */
@With(Secure.class)
public class Application extends BaseController {

    public static final String SETTINGS_PERSONAL_DATA = "personalData";
    public static final String SETTINGS_IMPORT = "import";
    public static final String SETTINGS_PAYMENT = "payment";

    public static void index() {
	redirect("Application.timetable");
    }

    public static void courses() {
	List<Course> courses = Course.findAll();
	List<CourseDTO> coursesList = new ArrayList<CourseDTO>();
	for (Course course : courses) {
	    CourseDTO courseDTO = new CourseDTO(course);
	    coursesList.add(courseDTO);
	}

	render("Application/courses.html", coursesList);
    }

    public static void course(Long id) {
	Course course = Course.findById(id);
	CourseDTO courseDTO = new CourseDTO(course);
	renderArgs.put("course", courseDTO);
	render("Application/course-details.html");
    }

    public static void settings(String category) {
	if (SETTINGS_IMPORT.equals(category)) {
	    render("Application/import.html");
	} else if (SETTINGS_PAYMENT.equals(category)) {
	    render("Application/payment.html");
	} else {
	    String currentUser = session.get(Security.SESSION_USERID);
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
	List<Course> courses = Course.findAll();
	List<CourseDTO> coursesList = new ArrayList<CourseDTO>();

	for (Course course : courses) {
	    CourseDTO courseDTO = new CourseDTO(course);
	    coursesList.add(courseDTO);
	}
	PersonalizedTimetable timetable = new PersonalizedTimetable(coursesList);
	List<TimetableEntry> entryList = timetable.scheduleTimeSlots();

	render("Application/timetable.html", entryList);
    }
}