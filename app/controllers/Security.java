package controllers;

import models.User;
import dto.UserDTO;

public class Security extends Secure.Security {
	
	static boolean authenticate(String email, String password) {
		if ("admin".equals(email) && "admin".equals(password)) {
			return true;
		}
		
		User user = User.find("byEmail", email).first();
		if (user != null) {
			// TODO: PZ: use hashes + salt!
			if (user.password.equals(password)) {
				return true;
			}
		}
		
		return false;
	}
	
    public static void signup(String name, String email, String password, String question, String answer, String captcha) {

    	if (areAllEmpty(name, email, password, question, answer, captcha)) {
    		// This is a new request to the page;
    		// Showing the new sign up page
        	render("auth/signup.html");
    	}
    	else {
    		// Some data was submitted;
    		// Validating it
    		
	    	// TODO: validation!
	    	// check for duplicates as well
	    	
	    	validation.required(email);
	    	validation.required(password);
	    	validation.required(captcha);
	    	validation.email(email);
	    	
	    	if (captcha != null) {
	    		if (!captcha.equals("123")) {
	    			// TODO: PZ: move consts to resources
	    			validation.addError("captcha", "validation.captcha");
	    		}
	    	}
	    	
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
	        	render("auth/signup.html");
	    	}
	    	else {
		    	User user = new User();
		    	user.name = name;
		    	user.email = email;
		    	user.question = question;
		    	user.answer = answer;
		    	user.password = password;
		    	user.save();
		
		    	// Authenticating the user in the system
		    	session.put("username", email);
		    	
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
}
