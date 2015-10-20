package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.JsonHelper;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by mladen.teofilovic on 20/10/15.
 */
public class JsonController extends Controller {


    public static Result registration(){

        JsonNode json = request().body().asJson();
        String firstName = json.findPath("firstName").textValue();
        String lastName = json.findPath("lastName").textValue();
        String email = json.findPath("email").textValue();
        String password = json.findPath("password").textValue();
        User u = new User();
        u.firstName = firstName;
        u.lastName = lastName;
        u.email = email;
        u.password = password;
        u.save();
        return ok();
    }

    public static Result login(){

        JsonNode json = request().body().asJson();
        String email = json.findPath("email").textValue();
        String password = json.findPath("password").textValue();

        User u = User.findUserByEmail(email);
        if(u==null){
            return badRequest();
        }
        return ok(JsonHelper.jsonUser(u));
    }

}
