package controllers.crudtypes;

import play.mvc.With;
import controllers.CRUD;
import controllers.Secure;

/**
 * CRUD object to manage course materials entities.
 * 
 */
@With(Secure.class)
public class CourseMaterials extends CRUD {

}
