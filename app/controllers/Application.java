package controllers;

import helper.UserHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Course;
import models.DateUtility;
import models.User;
import models.timetable.PersonalizedTimetable;
import models.timetable.ScheduledTimetableEntryList;
import play.data.binding.As;
import play.mvc.With;
import controllers.subtypes.ServiceSubscriptionPeriod;
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
	String currentUser = session.get(Security.SESSION_USERID);
	User user = User.find("byEmail", currentUser).first();
	UserDTO userDto = new UserDTO(user);
	renderArgs.put("userSettings", userDto);

	if (SETTINGS_IMPORT.equals(category)) {
	    render("Application/import.html");
	} else if (SETTINGS_PAYMENT.equals(category)) {
	    render("Application/payment.html");
	} else {
	    render("Application/personaldata.html");
	}
    }

    public static void savePersonalData() {

	String currentUserId = session.get(Security.SESSION_USERID);
	User currentUser = User.find("byEmail", currentUserId).first();

	String name = params.get("name");
	String email = params.get("email");
	boolean sharedTimetable = getBooleanValue(params.get("sharedTimetable"));
	boolean deadlineEmail = getBooleanValue(params.get("deadlineEmail"));
	boolean learnSlotEmail = getBooleanValue(params.get("learnSlotEmail"));

	validation.required(email);
	validation.email(email);

	// Checking the user with such an email address does not exist yet
	if (!validation.hasErrors()) {
	    if (!currentUser.email.equals(email)) {
		User existingUser = User.find("byEmail", email).first();
		if (existingUser != null) {
		    validation.addError("email", "validation.user.exists");
		}
	    }
	}

	if (validation.hasErrors()) {
	    // Showing the form again if the input was invalid
	    UserDTO userDto = new UserDTO(currentUser);
	    userDto.setName(name);
	    userDto.setEmail(email);
	    userDto.setSharedTimetable(sharedTimetable);
	    userDto.setDeadlineEmail(deadlineEmail);
	    userDto.setLearnSlotEmail(learnSlotEmail);
	    renderArgs.put("userSettings", userDto);

	    // Rendering settings page showing the errors
	    render("Application/personaldata.html");
	} else {
	    currentUser.name = name;
	    currentUser.email = email;
	    currentUser.sharedTimetable = sharedTimetable;
	    currentUser.deadlineEmail = deadlineEmail;
	    currentUser.learnSlotEmail = learnSlotEmail;
	    currentUser.save();
	    
	    // Authenticating the user in the system
	    session.put(Security.SESSION_USERID, email);

	    // Redirecting to the settings page
	    redirect("Application.settings", SETTINGS_PERSONAL_DATA);
	}
    }
    
    public static void updatePaymentStatus() {
	String paymentValue = params.get("payment");
	int payment;
	try {
	    payment = Integer.parseInt(paymentValue);
	}
	catch (NumberFormatException e) {
	    payment = 0;
	}
	
	if (payment > 0) {
	    String currentUserId = session.get(Security.SESSION_USERID);
	    User currentUser = User.find("byEmail", currentUserId).first();
	    
	    currentUser.serviceSubscriptionPeriod = UserHelper.monthsToSubscriptionPeriod(payment);
	    if (!currentUser.serviceSubscriptionPeriod.equals(ServiceSubscriptionPeriod.FREE)) {
		Date expirationDate = currentUser.subscriptionExpires;
		Calendar calendar = Calendar.getInstance();
		
		// Using current date if subscription is requested for
		// the first time or it has already expired. Otherwise,
		// extend it.
		if (expirationDate != null && expirationDate.after(calendar.getTime())) {
		    calendar.setTime(expirationDate);
		}
		calendar.add(Calendar.MONTH, payment);
		currentUser.subscriptionExpires = calendar.getTime();
		currentUser.save();
	    }
	}

	redirect("Application.settings", SETTINGS_PAYMENT);
    }

    public static void timetable() {
	List<Course> courses = Course.findAll();
	List<CourseDTO> coursesList = new ArrayList<CourseDTO>();

	for (Course course : courses) {
	    CourseDTO courseDTO = new CourseDTO(course);
	    coursesList.add(courseDTO);
	}
	PersonalizedTimetable timetable = new PersonalizedTimetable(coursesList);
	ScheduledTimetableEntryList entryList = timetable.scheduleTimeSlots();

	render("Application/timetable.html", entryList);
    }

    private static boolean getBooleanValue(String value) {
	if (value != null && !value.isEmpty()) {
	    return true;
	}

	return false;
    }

}