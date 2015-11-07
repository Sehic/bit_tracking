package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.JSONHelper;
import helpers.MailHelper;
import models.User;
import models.UserType;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.register;

import java.util.UUID;

/**
 * Created by Mladen13 on 25.10.2015.
 */
public class ApiUserController extends Controller {

    /**
     * Method that is part of content negotiation and it is used for registering new user
     * @return - ok if everything goes fine, badRequest if user already exists in database
     */
    public static Result registration(){

        JsonNode json = request().body().asJson();
        String firstName = json.findPath("firstName").textValue();
        String lastName = json.findPath("lastName").textValue();
        String email = json.findPath("email").textValue();
        String password = json.findPath("password").textValue();
        User u = new User();
        u.email = email;
        User user = User.checkEmail(u.email);
        //If user is not found, that means we can proceed creating new user
        if (user != null) {
            return badRequest();
        }
        u.firstName = firstName;
        u.lastName = lastName;
        u.password = password;
        u.firstName = u.firstName.substring(0, 1).toUpperCase() + u.firstName.substring(1);
        u.lastName = u.lastName.substring(0, 1).toUpperCase() + u.lastName.substring(1);
        u.token = UUID.randomUUID().toString();
        u.typeOfUser = UserType.REGISTERED_USER;
        MailHelper.sendVerificationMail(u.token, u.lastName, u.email);
        u.save();
        return ok();
    }

    /**
     * Method that is used for signing in registered user
     * @return
     */
    public static Result login(){

        JsonNode json = request().body().asJson();
        String email = json.findPath("email").textValue();
        String password = json.findPath("password").textValue();

        User u = User.findEmailAndPassword(email, password);
        if(u==null){
            return badRequest();
        }

        return ok(JSONHelper.jsonUser(u));
    }

    /**
     * Method that is used for getting packages from user with email that we get as json
     * @return - list of user packages
     */
    public Result getUserPackagesList(){

        JsonNode json = request().body().asJson();
        String email = json.findPath("email").textValue();
        User u = User.findUserByEmail(email);
        if(u == null){
           return badRequest();
        }
        return ok(JSONHelper.jsonPackagesList(u.packages));
    }

}
