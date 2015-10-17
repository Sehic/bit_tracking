package controllers;

import helpers.Authenticators;
import helpers.MailHelper;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;
import views.html.validatephone;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Application extends Controller {
    /**
     * Method that opens up index page
     *
     * @return
     */
    public Result index() {
        Http.Cookie cookie = request().cookie("email");
        if (cookie != null) {
            session("email", cookie.value());
        }
        return ok(index.render());
    }

    /**
     * This method send notification to Registered user, if that user
     * has some newly approved or rejected packages.
     * @return
     */
    public Result indexAjax() {
        User user = SessionHelper.getCurrentUser(ctx());
        Integer approved = 0;
        Integer rejected = 0;
        if(user != null && user.typeOfUser == UserType.REGISTERED_USER) {
            List<Package> packages = Package.finder.where().eq("seen", false).eq("users", user).findList();
            for (int i = 0; i < packages.size(); i++) {
                if (packages.get(i).approved) {
                    approved++;
                } else if (!packages.get(i).approved) {
                    rejected++;
                }
            }
        }
        Integer result = approved + rejected;
        String notification = Integer.toString(result);

        if(result != 0){
            return ok(notification);
        }

        return ok();
    }

    /**
     * Method that opens up login page
     *
     * @return
     */
    public Result login() {
        User u = SessionHelper.getCurrentUser(ctx());
        if(u!=null){
            return redirect(routes.Application.index());
        }
        Form<User> newUser = new Form<User>(User.class);
        return ok(login.render("", newUser));
    }

    /**
     * Method that logs out user and remove it from session
     *
     * @return
     */
    public Result logout() {
        session().clear();
        return redirect(routes.Application.login());
    }

    /**
     * Method that opens up register page
     *
     * @return
     */
    public Result register() {
        User u = SessionHelper.getCurrentUser(ctx());
        if(u!=null){
            return redirect(routes.Application.index());
        }
        Form<User> newUser = new Form<User>(User.class);
        List<Country> countryList = Country.findCountry.findList();
        return ok(register.render(newUser, countryList));
    }

    /**
     * Method that opens up admin panel
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPanel() {

        return ok(adminindex.render(User.find.findList()));

    }

    /**
     * Method that opens up maps panel
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminMaps() {

        if (Location.findLocation.findList().size() > 0)
            return ok(adminmaps.render(Location.findLocation.findList(), PostOffice.findOffice.findList()));
        return redirect(routes.Application.adminPanel());

    }

    /**
     * Method that opens up tables of users, post offices and packages
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminTables() {

        return ok(admintables.render(User.find.findList(), PostOffice.findOffice.findList(), Package.finder.findList()));
    }

    /**
     * Method that opens up list of office workers
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result officeWorkersList() {

        return ok(officeworkerlist.render(User.findOfficeWorkers()));
    }

    /**
     * Method that opens up list of post offices
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPostOffice() {

        List<PostOffice> list = PostOffice.findOffice.findList();
        return ok(adminpostoffice.render(list));
    }

    /**
     * Method that opens up form for adding new post office
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addPostOffice() {
        List<Country> countries = Country.findCountry.findList();
        return ok(adminpostofficeadd.render(countries));
    }

    /**
     * Method that opens up form for registering office worker
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result registerWorker() {
        Form<User> userForm =
                Form.form(User.class);
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        return ok(adminworkeradd.render(postOffices, userForm));
    }

    public Result trackPackage() {
        return ok(trackpackage.render());
    }

    public Result checkTrackingNumber() {

        DynamicForm form = Form.form().bindFromRequest();
        String trackingNumber = form.data().get("trackingNumber");
        Package p = Package.findPackageByTrackingNumber(trackingNumber);
        return ok(p.toString());
    }
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deliveryWorkersList() {

        return ok(deliveryworkerslist.render(User.find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).findList()));
    }

    public Result userPanel() {
        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null) {
            return redirect(routes.Application.index());
        }
        List<Package> packagesForRender = Package.findPackagesByUser(user);
        List<Package> packages = Package.findPackagesByUser(user);
        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).seen != null) {
                packages.get(i).seen = null;
                packages.get(i).update();
            }
        }
        return ok(userpanel.render(packagesForRender, PostOffice.findOffice.findList()));
    }

}
