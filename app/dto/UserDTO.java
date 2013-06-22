package dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.subtypes.ServiceSubscriptionPeriod;
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
    private String subscriptionPeriod;
    private String subscriptionExpires;
    private String pfree;
    private String p1;
    private String p3;
    private String p6;
    private String p12;

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
	
	this.subscriptionPeriod = getSubscriptionPeriod(user.serviceSubscriptionPeriod);
	this.subscriptionExpires = getSubscriptionExpirationDate(user.subscriptionExpires);
    }

    private String getSubscriptionExpirationDate(Date expirationDate) {
	
	if (expirationDate == null || expirationDate.before(new Date())) {
	    return "forever";
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	return sdf.format(expirationDate);
    }

    private String getSubscriptionPeriod(ServiceSubscriptionPeriod serviceSubscriptionPeriod) {
	switch (serviceSubscriptionPeriod) {
	case FREE:
	    pfree = "checked";
	    return "Free";
	case ONE_MONTH:
	    p1 = "checked";
	    return "1 month";
	case THREE_MONTHS:
	    p3 = "checked";
	    return "3 months";
	case SIX_MONTHS:
	    p6 = "checked";
	    return "6 months";
	case TWELWE_MONTHS:
	    p12 = "checked";
	    return "12 months";
	default:
	    return "";
	}
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
    
    public String getPfree() {
	return pfree;
    }
    
    public String getP1() {
	return p1;
    }
    
    public String getP3() {
	return p3;
    }
    
    public String getP6() {
	return p6;
    }
    
    public String getP12() {
	return p12;
    }
}
