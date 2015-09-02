package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

public class Application extends Controller {

    static Form<User> newUser = new Form<User>(User.class);
    private static final Form<User> userForm =
            Form.form(User.class);

    public Result login(){
        return ok(login.render(""));
    }

    public Result check(){

        String  password=newUser.bindFromRequest().field("password").value();
        String  email=newUser.bindFromRequest().field("email").value();
        User u = User.findEmailAndPassword(email, password);

        if(u != null) {
            return ok(login.render("Loged in successfuly!"));
        }
        return ok(login.render("Wrong email or password!"));

    }

    public Result register(){
        return ok(register.render());
    }

    /**
     * This method get user input from registration form and if every input is valid
     * saves it to database.
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
        if(u == null) {
            u = new User(firstName, lastName, password, email);
            Logger.info(u.toString());
            if (u.checkName(firstName) && u.checkName(lastName)) {
                if (password.equals(repassword)) {
                    Ebean.save(u);
                    return redirect(routes.Application.login());
                } else {
                    flash("errorName", "Ne valja ti ime");
                    return ok(register.render());
                }
            } else {
                flash("errorName", "Ne valja ti ime");
                return (badRequest(register.render()));
                //return ok(register.render());
            }
        }else {
            flash("errorName", "Ne valja ti ime");
            return ok(register.render());
        }
    }



}
