import play.GlobalSettings;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;

/**
 * Created by Mladen13 on 22.9.2015.
 */
public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<Result>pure(notFound(notfound.render()));
    }

    @Override
    public F.Promise<Result> onBadRequest(RequestHeader request, String error) {
        return F.Promise.<Result>pure(badRequest(notfound.render()));
    }

}
