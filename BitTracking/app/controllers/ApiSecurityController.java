
package controllers;

import helpers.ApiSecurity;
import models.ApiToken;
import play.mvc.Controller;

/**
 * Created by USER on 28.10.2015.
 */
public class ApiSecurityController extends Controller implements ApiSecurity {

    @Override
    public boolean isAuthorized(String token) {
        ApiToken apiToken = ApiToken.findApiToken(token);
        if (apiToken == null) {
            return false;
        }
        return true;
    }
}
