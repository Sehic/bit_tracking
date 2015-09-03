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

    static Form<User> newUser = new Form<User>(User.class);
    private static final Form<User> userForm =
            Form.form(User.class);

    public Result login() {
        return ok(login.render(""));
    }

    public Result check() {

        String password = newUser.bindFromRequest().field("password").value();
        String email = newUser.bindFromRequest().field("email").value();
        String newPassword = getEncriptedPasswordMD5(password);

        User u = User.findEmailAndPassword(email, newPassword);

        if (u != null) {
            return ok(login.render("Logged in successfuly!"));
        }
        return ok(login.render("Wrong email or password!"));

    }

    public Result register() {
        return ok(register.render());
    }

    /**
     * This method get user input from registration form and if every input is valid
     * saves it to database.
     *
     * @return redirect user to subpage login if everything is ok, otherwise ?????
     */
    public Result registrationCheck() {
        Form<User> boundForm = userForm.bindFromRequest();
        String firstName = boundForm.bindFromRequest().field("firstName").value();
        String lastName = boundForm.bindFromRequest().field("lastName").value();
        String password = boundForm.bindFromRequest().field("password").value();
        String repassword = boundForm.bindFromRequest().field("repassword").value();
        String email = boundForm.bindFromRequest().field("email").value();
        User u = User.checkEmail(email);
        if (u == null) {
            u = new User(firstName, lastName, password, email);
            Logger.info(u.toString());
            if (firstName.length() == 0 || lastName.length() == 0 || password.length() == 0 || repassword.length() == 0 || email.length() == 0) {
                flash("errorEmptyName", "Please fill all fields!");
            }

            if (u.checkName(firstName) && u.checkName(lastName)) {

                if (u.checkPassword(password) && password.equals(repassword)) {
                    String newPassword = getEncriptedPasswordMD5(password);
                    u = new User(firstName, lastName, newPassword, email);

                    Ebean.save(u);
                    return redirect(routes.Application.login());
                } else {
                    flash("errorPassword", "Couldn't accept password. Your password should contain at least 6 characters, one number, and one sign, or you entered different passwords");
                    return ok(register.render());
                }
            } else {
                flash("errorName", "Your name or last name should have only letters.");
                return (badRequest(register.render()));
                //return ok(register.render());
            }
        } else {
            flash("errorEmail", "E-mail address already exists!");
            return ok(register.render());
        }
    }

    public static String getEncriptedPasswordMD5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes(), 0, password.length());
            String result = new BigInteger(1, md5.digest()).toString(16);
            md5.reset();
            return result;
        } catch (NoSuchAlgorithmException e) {
            // TODO add to logger
        }
        return "INVALID PASSWORD";
    }


}
