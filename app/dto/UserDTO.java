package dto;

import helper.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.mapping.Collection;
import org.joda.time.LocalTime;

import edu.emory.mathcs.backport.java.util.Collections;

import models.CustomerPreferences;
import models.DateUtility;
import models.DateUtility.Day;
import models.TimeInterval;
import models.User;

/**
 * DTO for a user object used to transfer the data between controllers and
 * views.
 * 
 */
public class UserDTO {

    private String name;
    private String email;
    private String question;
    private String answer;
    private String userpic;
    private boolean sharedTimetable;
    private boolean deadlineEmail;
    private boolean learnSlotEmail;
    private boolean paidUser;
    private String subscriptionPeriod;
    private String subscriptionExpires;
    private int subscriptionMonths;
    private String privateCalendarURL;
    private CustomerPreferences preferences;

    public UserDTO(String name, String email, String password, String question, String answer, String userpic,
	    boolean sharedTimetable, boolean deadlineEmail, boolean learnSlotEmail) {
	this.name = name;
	this.email = email;
	this.question = question;
	this.answer = answer;
	this.userpic = userpic;
	this.sharedTimetable = sharedTimetable;
	this.deadlineEmail = deadlineEmail;
	this.learnSlotEmail = learnSlotEmail;
    }

    public UserDTO(User user) {
	this.name = user.name;
	this.email = user.email;
	this.question = user.question;
	this.answer = user.answer;
	this.userpic = user.userpic;
	this.sharedTimetable = user.sharedTimetable;
	this.deadlineEmail = user.deadlineEmail;
	this.learnSlotEmail = user.learnSlotEmail;

	this.subscriptionPeriod = UserHelper.subscriptionPeriodToText(user.serviceSubscriptionPeriod);
	this.subscriptionMonths = UserHelper.subscriptionPeriodToMonths(user.serviceSubscriptionPeriod);
	this.subscriptionExpires = UserHelper.expirationDateToString(user.subscriptionExpires);
	this.paidUser = (user.subscriptionExpires != null && user.subscriptionExpires.after(new Date()));

	this.privateCalendarURL = user.privateCalendarURL;

	CustomerPreferences preferences = new CustomerPreferences(user.preferences.getPreferredLearningDayTime(),
		user.preferences.getMaxLearningSlotDuration(), user.preferences.getMinLearningSlotDuration(),
		user.preferences.getBreakDuration());
	preferences.setDaysOfRest(new ArrayList<DateUtility.Day>());
	preferences.getDaysOfRest().addAll(user.preferences.getDaysOfRest());
	preferences.setTimeIntervalsOfRest(new ArrayList<TimeInterval>());
	preferences.getTimeIntervalsOfRest().addAll(user.preferences.getTimeIntervalsOfRest());
	this.preferences = preferences;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getQuestion() {
	return question;
    }

    public void setQuestion(String question) {
	this.question = question;
    }

    public String getAnswer() {
	return answer;
    }

    public void setAnswer(String answer) {
	this.answer = answer;
    }

    public String getUserpic() {
	return userpic;
    }

    public void setUserpic(String userpic) {
	this.userpic = userpic;
    }

    public boolean isSharedTimetable() {
	return sharedTimetable;
    }

    public void setSharedTimetable(boolean sharedTimetable) {
	this.sharedTimetable = sharedTimetable;
    }

    public boolean isDeadlineEmail() {
	return deadlineEmail;
    }

    public void setDeadlineEmail(boolean deadlineEmail) {
	this.deadlineEmail = deadlineEmail;
    }

    public boolean isLearnSlotEmail() {
	return learnSlotEmail;
    }

    public void setLearnSlotEmail(boolean learnSlotEmail) {
	this.learnSlotEmail = learnSlotEmail;
    }

    public String getSubscriptionExpires() {
	return subscriptionExpires;
    }

    public void setSubscriptionExpires(String subscriptionExpires) {
	this.subscriptionExpires = subscriptionExpires;
    }

    public String getSubscriptionPeriod() {
	return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(String subscriptionPeriod) {
	this.subscriptionPeriod = subscriptionPeriod;
    }

    public int getSubscriptionMonths() {
	return subscriptionMonths;
    }

    public boolean isPaidUser() {
	return paidUser;
    }

    public String getPrivateCalendarURL() {
	return privateCalendarURL;
    }

    public CustomerPreferences getPreferences() {
	return preferences;
    }

    public void setPrivateCalendarURL(String privateCalendarURL) {
	this.privateCalendarURL = privateCalendarURL;
    }

    public void setDaysOfRest(String[] daysOfRestValue) {
	if (daysOfRestValue != null) {
	    if (preferences.getDaysOfRest() == null) {
		preferences.setDaysOfRest(new ArrayList<DateUtility.Day>());
	    }
	    
	    preferences.getDaysOfRest().clear();

	    for (int i = 0; i < daysOfRestValue.length; i++) {
		if ("monday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Monday);
		} else if ("tuesday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Tuesday);
		} else if ("wednesday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Wednesday);
		} else if ("thursday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Thursday);
		} else if ("friday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Friday);
		} else if ("saturday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Saturday);
		} else if ("sunday".equals(daysOfRestValue[i])) {
		    preferences.getDaysOfRest().add(Day.Sunday);
		}
	    }
	}

    }

    public void addTimeIntervalOfRest(String startTimeValue, String endTimeValue) {
	
	int startHour = getHour(startTimeValue);
	int startMinute = getMinutes(startTimeValue);
	int endHour = getHour(endTimeValue);
	int endMinute = getMinutes(endTimeValue);

	if (preferences.getTimeIntervalsOfRest() == null) {
	    preferences.setTimeIntervalsOfRest(new ArrayList<TimeInterval>());
	}
	
	preferences.getTimeIntervalsOfRest().add(
		new TimeInterval(
			new LocalTime(startHour, startMinute), 
			new LocalTime(endHour, endMinute)));
	
	Collections.sort(preferences.getTimeIntervalsOfRest());
	Collections.reverse(preferences.getTimeIntervalsOfRest());
    }

    private int getHour(String time) {
	try {
	    if (time != null) {
		int i = time.indexOf(":");
		if (i >= 0) {
		    return Integer.parseInt(time.substring(0, i));
		}
	    }
	} catch (NumberFormatException e) {
	    // Ignore
	}

	return 0;
    }

    private int getMinutes(String time) {
	try {
	    if (time != null) {
		int i = time.indexOf(":");
		if (i >= 0) {
		    return Integer.parseInt(time.substring(i + 1));
		}
	    }
	} catch (NumberFormatException e) {
	    // Ignore
	}

	return 0;
    }

    public String getStartTime1() {
	if (preferences != null) {
	    List<TimeInterval> li = preferences.getTimeIntervalsOfRest();
	    if (li != null && li.size() >= 1) {
		return String.format("%d:%d", li.get(0).getTime1().getHourOfDay(), li.get(0).getTime1()
			.getMinuteOfHour());
	    }
	}
	return "";
    }

    public String getEndTime1() {
	if (preferences != null) {
	    List<TimeInterval> li = preferences.getTimeIntervalsOfRest();
	    if (li != null && li.size() >= 1) {
		return String.format("%d:%d", li.get(0).getTime2().getHourOfDay(), li.get(0).getTime2()
			.getMinuteOfHour());
	    }
	}
	return "";
    }

    public String getStartTime2() {
	if (preferences != null) {
	    List<TimeInterval> li = preferences.getTimeIntervalsOfRest();
	    if (li != null && li.size() >= 2) {
		return String.format("%d:%d", li.get(1).getTime1().getHourOfDay(), li.get(1).getTime1()
			.getMinuteOfHour());
	    }
	}
	return "";
    }

    public String getEndTime2() {
	if (preferences != null) {
	    List<TimeInterval> li = preferences.getTimeIntervalsOfRest();
	    if (li != null && li.size() >= 2) {
		return String.format("%d:%d", li.get(1).getTime2().getHourOfDay(), li.get(1).getTime2()
			.getMinuteOfHour());
	    }
	}
	return "";
    }

    public List<String> getDaysOfRestIndexes() {
	List<String> daysOfRest = new ArrayList<String>();

	if (preferences != null && preferences.getDaysOfRest() != null) {
	    for (Day d : preferences.getDaysOfRest()) {
		switch (d) {
		case Monday:
		    daysOfRest.add("monday");
		    break;
		case Tuesday:
		    daysOfRest.add("tuesday");
		    break;
		case Wednesday:
		    daysOfRest.add("wednesday");
		    break;
		case Thursday:
		    daysOfRest.add("thursday");
		    break;
		case Friday:
		    daysOfRest.add("friday");
		    break;
		case Saturday:
		    daysOfRest.add("saturday");
		    break;
		case Sunday:
		    daysOfRest.add("sunday");
		    break;
		}
	    }
	}

	return daysOfRest;
    }
}
