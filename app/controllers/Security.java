package controllers;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import models.User;

import org.apache.commons.lang.StringUtils;

import controllers.subtypes.ServiceSubscriptionPeriod;

import play.cache.Cache;
import play.libs.Codec;
import dto.UserDTO;

/**
 * Controller to provide support of security aspects of the application.
 * Performs authentication and creation of users.
 * 
 */
public class Security extends Secure.Security {

    /**
     * Session key to identify an authenticated user. Used to keep the used
     * logged in right after account creation.
     */
    public static final String SESSION_USERID = "username";

    /**
     * Authenticates a user.
     * 
     * @param email
     *            Email of the user used as unique identifier
     * @param password
     *            User password from the authentication request
     * @return true if the password matches the user object, false otherwise
     */
    static boolean authenticate(String email, String password) {

	if (email == null || email.isEmpty()) {
	    return false;
	}

	User user = User.find("byEmail", email).first();
	if (user != null) {
	    // Getting the salt of user password and calculating the
	    // password hash based on it
	    String userSalt = user.salt;
	    String userPasswordHash = user.password;
	    String submittedPasswordHash = getHash(password, userSalt);

	    if (userPasswordHash.equals(submittedPasswordHash)) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Handles requests to the sign-up page. Renders the sign-up form if
     * requested with empty parameters or validates the data and creates a new
     * user of the sign-up form was submitted.
     * 
     * @param name
     *            Human readable name of the user
     * @param email
     *            Email of the user; used as user identifier
     * @param password
     *            User password
     * @param question
     *            Security question
     * @param answer
     *            Answer to the security question
     * @param captchaId
     *            Id of the captcha
     * @param captcha
     *            Submitted captcha value
     */
    public static void signup(String name, String email, String password, String question, String answer,
	    String captchaId, String captcha) {

	// Showing the page with an empty form if this is a new requiest
	if (areAllEmpty(name, email, password, question, answer, captchaId, captcha)) {
	    captchaId = Codec.UUID();
	    render("auth/signup.html", captchaId);
	} else {
	    // Validating the data, if the sign-up page was submitted

	    validation.required(email);
	    validation.required(password);
	    validation.required(captcha);
	    validation.email(email);

	    if (question != null && !question.isEmpty()) {
		validation.required(answer);
	    }

	    validation.equals(StringUtils.lowerCase(captcha), StringUtils.lowerCase((String) Cache.get(captchaId)))
		    .message("validation.captcha");

	    // Checking the user with such an email address does not exist yet
	    if (!validation.hasErrors()) {
		User existingUser = User.find("byEmail", email).first();
		if (existingUser != null) {
		    validation.addError("email", "validation.user.exists");
		}
	    }

	    if (validation.hasErrors()) {
		// Showing the form again if the input was invalid
		UserDTO userDto = new UserDTO(name, email, password, question, answer, "", false, false, false);
		renderArgs.put("user", userDto);
		captchaId = Codec.UUID();
		render("auth/signup.html", captchaId);
	    } else {
		// Creating a new user account
		String defaultUserpic = "default.jpg";
		String salt = generateSalt();
		User user = new User();
		user.name = name;
		user.email = email;
		user.question = question;
		user.answer = answer;
		user.salt = salt;
		user.password = getHash(password, salt);
		user.userpic = defaultUserpic;
		user.serviceSubscriptionPeriod = ServiceSubscriptionPeriod.FREE;
		user.subscriptionExpires = null;
		user.save();

		// Authenticating the user in the system
		session.put(SESSION_USERID, email);

		// Redirecting to the start page
		redirect("Application.index");
	    }
	}
    }

    /**
     * Checks whether all the input parameters are empty.
     * 
     * @param values
     *            Values to check
     * @return true of all the values are null or empty; false otherwise
     */
    private static boolean areAllEmpty(String... values) {
	if (values == null) {
	    return true;
	}

	for (int i = 0; i < values.length; i++) {
	    if (values[i] != null && !values[i].isEmpty()) {
		return false;
	    }
	}

	return true;
    }

    /**
     * Calculates hash of the user password.
     * 
     * @param password
     *            Password to calculate the hash
     * @param salt
     *            Salt value
     * @return Password hash
     */
    private static String getHash(String password, String salt) {
	String saltedPassword = "" + password + salt;
	return getSHA1(saltedPassword);
    }

    /**
     * Calculates SHA-1 hash value of a string.
     * 
     * @param s
     *            String to calculate the hash
     * @return Hash value
     */
    private static String getSHA1(String s) {
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA-1");
	    return byteArrayToHexString(md.digest(s.getBytes(Charset.forName("UTF-8"))));
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}

	throw new RuntimeException("Unable to calculate hash for the password");
    }

    /**
     * Converts byte array to a hex string to ensure readability.
     * 
     * @param b
     *            Byte array
     * @return Hex string representing the array
     */
    private static String byteArrayToHexString(byte[] b) {
	StringBuilder result = new StringBuilder();

	// CSOFF: MagicNumber
	for (int i = 0; i < b.length; i++) {
	    result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
	}
	// CSON: MagicNumber

	return result.toString();
    }

    /**
     * Generates a (pseudo)random salt value for a user.
     * 
     * @return Salt value
     */
    private static String generateSalt() {
	// Using salt of 6 bytes length
	// CSOFF: MagicNumber
	int saltSize = 6;
	// CSON: MagicNumber

	byte[] b = new byte[saltSize];
	new Random().nextBytes(b);
	return byteArrayToHexString(b);
    }
}
