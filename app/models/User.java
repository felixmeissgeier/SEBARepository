package models;

import javax.persistence.Entity;

import models.subtypes.UserEmailPeriod;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	public String name;
	
	@Email
	@Required
	public String email;
	
	@Required
	public String password;
	public String salt;
	
	public String question;
	public String answer;
	
	public boolean sharedTimetable;
	
	public boolean deadlineEmail;
	public boolean learnSlotEmail;
	public UserEmailPeriod emailPeriod;
	
}

