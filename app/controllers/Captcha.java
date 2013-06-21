package controllers;

import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;

/**
 * Controller to handle captcha requests. Used to prevent automatic sign-up 
 * of new users. 
 *
 */
public class Captcha extends Controller {

	/**
	 * Generates a captcha image and renders it as a binary stream.
	 * 
	 * @param id Random id to associate the generated captcha image with 
	 */
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#2060a0");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }    
}