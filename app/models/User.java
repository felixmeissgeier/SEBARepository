package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import controllers.subtypes.ServiceSubscriptionPeriod;

/**
 * DAO class for a user object.
 * 
 */
@Entity
public class User extends Model {

    // CSOFF: VisibilityModifierCheck
    public String name;
    // CSON: VisibilityModifierCheck

    @Email
    @Required
    @Unique
    public String email;
    public String userpic;

    @Required
    public String password;
    public String salt;

    public String question;
    public String answer;

    public boolean sharedTimetable;
    public boolean deadlineEmail;
    public boolean learnSlotEmail;
    
    public String privateCalendarURL;
    public CustomerPreferences preferences;

    public ServiceSubscriptionPeriod serviceSubscriptionPeriod;
    @As(lang = { "*" }, value = { "yyyy-MM-dd HH:mm" })
    public Date subscriptionExpires;
}
