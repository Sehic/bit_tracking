import com.cloudinary.Cloudinary;
import models.ImagePath;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import views.html.notfound;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;

/**
 * Created by Mladen13 on 22.9.2015.
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        ImagePath.cloudinary = new Cloudinary("cloudinary://"+ Play.application().configuration().getString("cloudinary.string"));
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<Result>pure(notFound(notfound.render()));
    }

    @Override
    public F.Promise<Result> onBadRequest(RequestHeader request, String error) {
        return F.Promise.<Result>pure(badRequest(notfound.render()));
    }

}
