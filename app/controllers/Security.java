package controllers;

import models.User;

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
	
    public static void signup() {
    	render("auth/signup.html");
    }
    
    public static void createUser(String name, String email, String password, String question, String answer, String captcha) {
    	
    	// TODO: validation!
    	// check for duplicates as well
    	
    	User user = new User();
    	user.name = name;
    	user.email = email;
    	user.password = password;
    	user.securityQuestion = question;
    	user.securityAnswer = answer;
    	user.save();

    	// Authenticating the user in the system
    	session.put("username", email);
    	
    	// Redirecting to the start page
    	redirect("Application.index");
    }
}
