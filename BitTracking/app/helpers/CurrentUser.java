package helpers;

/**
 * Created by mladen.teofilovic on 08/09/15.
 */
import controllers.routes;
import models.User;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class CurrentUser extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        if(!ctx.session().containsKey("email"))
            return null;
        String email = ctx.session().get("email");
        User u = User.checkEmail(email);
        if (u != null)
            return u.email;
        return null;
    }

    /**
     * Redirects unauthorized users to the specified page
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.index());
    }

}
