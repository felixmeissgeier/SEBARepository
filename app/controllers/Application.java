package controllers;

import helper.UserHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Course;
import models.CourseMaterial;
import models.DateUtility;
import models.TimeInterval;
import models.User;
import models.timetable.PersonalizedTimetable;
import models.timetable.ScheduledTimetableEntryList;
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
	public static final String SETTINGS_PAYMENT = "payment";
	private static PersonalizedTimetable timetable;
	private static ScheduledTimetableEntryList entryList;

	public Application() {
		// List<Course> courses = Course.findAll();
		// List<CourseDTO> coursesList = new ArrayList<CourseDTO>();
		//
		// for (Course course : courses) {
		// CourseDTO courseDTO = new CourseDTO(course);
		// coursesList.add(courseDTO);
		// }
		//
		// timetable = new PersonalizedTimetable(coursesList);
		// entryList = timetable.scheduleTimeSlots();

	}

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
		renderArgs.put("courseID", id);
		render("Application/course-details.html");
	}

	public static void settings(String category) {
		User user = getConnectedUser();
		UserDTO userDto = new UserDTO(user);
		renderArgs.put("userSettings", userDto);

		if (SETTINGS_PAYMENT.equals(category)) {
			render("Application/payment.html");
		} else {
			render("Application/personaldata.html");
		}
	}

	public static void savePersonalData() {
		User currentUser = getConnectedUser();
		String name = params.get("name");
		String email = params.get("email");
		boolean sharedTimetable = getBooleanValue(params.get("sharedTimetable"));
		boolean deadlineEmail = getBooleanValue(params.get("deadlineEmail"));
		boolean learnSlotEmail = getBooleanValue(params.get("learnSlotEmail"));

		String calendarURL = params.get("calendarURL");
		String breakDurationValue = params.get("breakDuration");
		String minSlotDurationValue = params.get("minSlotDuration");
		String maxSlotDurationValue = params.get("maxSlotDuration");

		String[] daysOfRestValue = params.getAll("daysOfRest");
		String startTime1Value = params.get("starttime1");
		String endTime1Value = params.get("endtime1");
		String startTime2Value = params.get("starttime2");
		String endTime2Value = params.get("endtime2");

		validation.required(email);
		validation.email(email);

		float breakDuration = 0;
		float minSlotDuration = 0;
		float maxSlotDuration = 0;

		try {
			breakDuration = Float.parseFloat(breakDurationValue);
		} catch (NumberFormatException e) {
			validation.addError("breakDuration", "validation.settings.incorrect.float.value");
		}

		try {
			minSlotDuration = Float.parseFloat(minSlotDurationValue);
		} catch (NumberFormatException e) {
			validation.addError("minSlotDuration", "validation.settings.incorrect.float.value");
		}

		try {
			maxSlotDuration = Float.parseFloat(maxSlotDurationValue);
		} catch (NumberFormatException e) {
			validation.addError("maxSlotDuration", "validation.settings.incorrect.float.value");
		}

		// Checking the user with such an email address does not exist yet
		if (!validation.hasErrors()) {
			if (!currentUser.email.equals(email)) {
				User existingUser = User.find("byEmail", email).first();
				if (existingUser != null) {
					validation.addError("email", "validation.user.exists");
				}
			}
		}

		UserDTO userDto = new UserDTO(currentUser);
		userDto.setName(name);
		userDto.setEmail(email);
		userDto.setSharedTimetable(sharedTimetable);
		userDto.setDeadlineEmail(deadlineEmail);
		userDto.setLearnSlotEmail(learnSlotEmail);

		userDto.setPrivateCalendarURL(calendarURL);
		userDto.getPreferences().setBreakDuration(breakDuration);
		userDto.getPreferences().setMinLearningSlotDuration(minSlotDuration);
		userDto.getPreferences().setMaxLearningSlotDuration(maxSlotDuration);
		userDto.setDaysOfRest(daysOfRestValue);
		if (userDto.getPreferences().getTimeIntervalsOfRest() != null) {
			userDto.getPreferences().getTimeIntervalsOfRest().clear();
		}
		userDto.addTimeIntervalOfRest(startTime1Value, endTime1Value);
		userDto.addTimeIntervalOfRest(startTime2Value, endTime2Value);

		if (validation.hasErrors()) {
			// Showing the form again if the input was invalid
			renderArgs.put("userSettings", userDto);

			// Rendering settings page showing the errors
			render("Application/personaldata.html");
		} else {
			currentUser.name = name;
			currentUser.email = email;
			currentUser.sharedTimetable = sharedTimetable;
			currentUser.deadlineEmail = deadlineEmail;
			currentUser.learnSlotEmail = learnSlotEmail;

			currentUser.privateCalendarURL = userDto.getPrivateCalendarURL();
			currentUser.preferences.setMaxLearningSlotDuration(maxSlotDuration);
			currentUser.preferences.setMinLearningSlotDuration(minSlotDuration);
			currentUser.preferences.setBreakDuration(breakDuration);

			if (currentUser.preferences.getTimeIntervalsOfRest() != null) {
				currentUser.preferences.getTimeIntervalsOfRest().clear();
			} else {
				currentUser.preferences.setTimeIntervalsOfRest(new ArrayList<TimeInterval>());
			}

			if (currentUser.preferences.getDaysOfRest() != null) {
				currentUser.preferences.getDaysOfRest().clear();
			} else {
				currentUser.preferences.setDaysOfRest(new ArrayList<DateUtility.Day>());
			}

			currentUser.preferences.getTimeIntervalsOfRest().addAll(userDto.getPreferences().getTimeIntervalsOfRest());
			currentUser.preferences.getDaysOfRest().addAll(userDto.getPreferences().getDaysOfRest());

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
		} catch (NumberFormatException e) {
			payment = 0;
		}

		if (payment > 0) {
			User currentUser = getConnectedUser();

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
		User user = getConnectedUser();
		PersonalizedTimetable timetable = new PersonalizedTimetable(user, coursesList);
		ScheduledTimetableEntryList entryList = timetable.scheduleTimeSlots();

		render("Application/timetable.html", entryList);
	}

	public static void statistics() {
		List<Course> courses = Course.findAll();
		List<CourseDTO> coursesList = new ArrayList<CourseDTO>();

		for (Course course : courses) {
			CourseDTO courseDTO = new CourseDTO(course);
			coursesList.add(courseDTO);
		}
		User user = getConnectedUser();
		PersonalizedTimetable timetable = new PersonalizedTimetable(user, coursesList);
		ScheduledTimetableEntryList entryList = timetable.scheduleTimeSlots();
		render("Application/statistics.html", entryList, coursesList);
	}

	private static boolean getBooleanValue(String value) {
		if (value != null && !value.isEmpty()) {
			return true;
		}

		return false;
	}

	public static void saveCourseData() {
		Long id = Long.parseLong(params.get("id"));
		Course course = Course.findById(id);
		String dl = params.get("deadline");
		SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			course.deadline = sdfToDate.parse(dl);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		course.title = params.get("title");
		course.remarks = params.get("remarks");
		course.color = Integer.parseInt(params.get("color").replace("#", ""), 16);
		course.difficulty = Double.parseDouble(params.get("difficulty"));
		course.priority = Double.parseDouble(params.get("priority"));
		CourseMaterial cm = new CourseMaterial();

		if (params.get("considerExpectedHours") == null) {
			cm.considerExpectedHours = false;
			cm.scriptPagesDone = Integer.parseInt(params.get("scriptPagesDone"));
			cm.scriptPagesToDo = Integer.parseInt(params.get("scriptPagesToDo"));
		} else {
			cm.considerExpectedHours = true;
			cm.workloadHoursExpected = Double.parseDouble(params.get("workloadHoursExpected"));
		}
		cm.save();
		course.courseMaterial = cm;
		course.save();
		redirect("Application.courses", params.get("id"));
	}
}