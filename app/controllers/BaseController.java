package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import dto.UserDTO;

/**
 * Base controller class. Passes the current user object as a parameter for a
 * view.
 * 
 */
@With(Secure.class)
public class BaseController extends Controller {

    @Before
    static void setConnectedUser() {
	if (Security.isConnected()) {
	    User user = User.find("byEmail", Security.connected()).first();
	    if (user == null) {
		// No such user, logging out
		session.remove(Security.SESSION_USERID);
	    } else {
		UserDTO userDto = new UserDTO(user);
		renderArgs.put("user", userDto);
	    }
	}
    }
}