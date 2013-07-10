package controllers.crudtypes;

import play.mvc.With;
import controllers.CRUD;
import controllers.Secure;

/**
 * CRUD object to manage course entities.
 * 
 */
@With(Secure.class)
public class Courses extends CRUD {

}
