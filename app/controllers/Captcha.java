package controllers;

import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;

public class Captcha extends Controller {

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#2060a0");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }    
}