package controllers;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import models.User;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.libs.Codec;
import dto.UserDTO;

public class Security extends Secure.Security {
	
	public static final String SESSION_USERNAME = "username";
	
	static boolean authenticate(String email, String password) {
		if ("admin".equals(email) && "admin".equals(password)) {
			return true;
		}
		
		User user = User.find("byEmail", email).first();
		if (user != null) {
			String userSalt = user.salt;
			String userPasswordHash = user.password;
			String submittedPasswordHash = getHash(password, userSalt);
			
			if (userPasswordHash.equals(submittedPasswordHash)) {
				return true;
			}
		}
		
		return false;
	}
	
    public static void signup(String name, String email, String password, String question, String answer, String captchaId, String captcha) {

    	if (areAllEmpty(name, email, password, question, answer, captchaId, captcha)) {
    		// This is a new request to the page;
    		// Showing the new sign up page

    		captchaId = Codec.UUID();
        	render("auth/signup.html", captchaId);
    	}
    	else {
    		// The form with data has been submitted;
    		// Validating it
    		
	    	validation.required(email);
	    	validation.required(password);
	    	validation.required(captcha);
	    	validation.email(email);
	    	
	    	validation.equals(StringUtils.lowerCase(captcha), StringUtils.lowerCase((String)Cache.get(captchaId))).message("validation.captcha");	    	
	    	
	    	if (question != null && !question.isEmpty()) {
	    		validation.required(answer);
	    	}

	    	if (!validation.hasErrors()) {
	    		User existingUser = User.find("byEmail", email).first();
	    		if (existingUser != null) {
	    			validation.addError("email", "validation.user.exists");
	    		}
	    	}
	    	
	    	if (validation.hasErrors()) {
	    		UserDTO userDto = new UserDTO(name, email, password, question, answer);
	    		renderArgs.put("user", userDto);
	    		captchaId = Codec.UUID();
	        	render("auth/signup.html", captchaId);
	    	}
	    	else {
	    		String salt = "";
		    	User user = new User();
		    	user.name = name;
		    	user.email = email;
		    	user.question = question;
		    	user.answer = answer;
		    	user.salt = salt; 
		    	user.password = getHash(password, salt);
		    	user.save();
		
		    	// Authenticating the user in the system
		    	session.put(SESSION_USERNAME, email);
		    	
		    	// Redirecting to the start page
		    	redirect("Application.index");
	    	}
    	}
    }
    
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
    
    private static String getHash(String password, String salt) {
    	String saltedPassword = "" + password + salt;
    	return getSHA1(saltedPassword);
    }
    
    private static String getSHA1(String s) {
    	try {
    		MessageDigest md = MessageDigest.getInstance("SHA-1");
    		return byteArrayToHexString(md.digest(s.getBytes(Charset.forName("UTF-8"))));
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    	
    	throw new RuntimeException("Unable to calculate hash for the password");
    }
    
	public static String byteArrayToHexString(byte[] b) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString(b[i] + 0x100, 16).substring(1));
		}
		return result.toString();
	}
}
