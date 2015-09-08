package helpers;

import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import models.User;




/**
 * Created by mladen.teofilovic on 08/09/15.
 */

public class SessionHelper extends Security.Authenticator {

    public static User getCurrentUser(Context ctx){
        String email = ctx.session().get("email");
        if(email == null)
            return null;
        return User.checkEmail(email);
    }

    @Override
    public Result onUnauthorized(Context ctx){
        return redirect(routes.Application.index());
    }

}
