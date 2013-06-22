package controllers.crudtypes;

import controllers.CRUD;
import controllers.Secure;
import play.mvc.With;

/**
 * CRUD object to manage user entities.
 *
 */
@With(Secure.class)
public class Users extends CRUD {

}
