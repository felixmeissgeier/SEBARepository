package dto;

import helper.UserHelper;

import java.util.Date;

import models.CustomerPreferences;
import models.User;

import org.apache.commons.lang.ArrayUtils;

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

    public UserDTO(String name, String email, String password, String question,
	    String answer, String userpic, boolean sharedTimetable,
	    boolean deadlineEmail, boolean learnSlotEmail) {
	this.name = name;
	this.email = email;
	this.question = question;
	this.answer = answer;
	this.userpic = userpic;
	this.sharedTimetable = sharedTimetable;
	this.deadlineEmail = deadlineEmail;
	this.learnSlotEmail = learnSlotEmail;
	
	System.out.println("!!!!!!!!!! EMPTY CONSTRUCTOR");
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
	this.preferences = user.preferences;
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
}
