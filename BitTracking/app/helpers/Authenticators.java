package helpers;

import models.User;
import models.UserType;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;

/**
 * Created by Mladen13 on 20.9.2015.
 */
public class Authenticators {
    public static class AdminFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.ADMIN)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    public static class AdminOfficeWorkerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.ADMIN || u.typeOfUser == UserType.OFFICE_WORKER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    public static class AdminDeliveryWorkerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.ADMIN || u.typeOfUser == UserType.DELIVERY_WORKER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    public static class OfficeWorkerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.OFFICE_WORKER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    public static class DeliveryWorkerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.DELIVERY_WORKER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    public static class RegisteredUserFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            User u = User.checkEmail(email);
            if (u != null && u.typeOfUser == UserType.REGISTERED_USER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }


}
