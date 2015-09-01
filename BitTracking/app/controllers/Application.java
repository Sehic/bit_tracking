package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    static Form<User> newUser = new Form<User>(User.class);

    public Result login(){

        String  password=newUser.bindFromRequest().field("password").value();
        String  email=newUser.bindFromRequest().field("email").value();
        User u = User.findEmailAndPassword(email, password);

        if(u != null) {

            return ok(login.render("User registered successfuly!"));
        }
        return ok(login.render("There is no such user."));

    }

    public Result register(){
        return ok(register.render());
    }

}
