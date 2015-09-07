package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    public Result login() {
        return ok(login.render(""));
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.login());
    }

    public Result register() {
        return ok(register.render());
    }
    public Result editProfile() {
        return ok(editprofile.render());
    }




}
