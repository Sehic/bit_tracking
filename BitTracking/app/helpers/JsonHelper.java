package helpers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.libs.Json;

/**
 * Created by mladen.teofilovic on 20/10/15.
 */
public class JsonHelper {

    public static ObjectNode jsonUser(User u){

        ObjectNode user = Json.newObject();
        user.put("id", u.id);
        user.put("firstName", u.firstName);
        user.put("lastName", u.lastName);
        user.put("password", u.password);
        user.put("email", u.email);
        return user;

    }
}
