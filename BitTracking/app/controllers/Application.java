package controllers;

import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


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
     * Method that opens up login page
     *
     * @return
     */
    public Result login() {
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
        Form<User> newUser = new Form<User>(User.class);
        return ok(register.render(newUser));
    }

    /**
     * Method that opens up admin panel
     *
     * @return
     */
    public Result adminPanel() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        return ok(adminindex.render(User.find.findList()));
    }

    /**
     * Method that opens up maps panel
     *
     * @return
     */
    public Result adminMaps() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        if (Location.findLocation.findList().size() > 0)
            return ok(adminmaps.render(Location.findLocation.findList()));
        return redirect(routes.Application.adminPanel());

    }

    /**
     * Method that opens up tables of users, post offices and packages
     *
     * @return
     */
    public Result adminTables() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        return ok(admintables.render(User.find.findList(), PostOffice.findOffice.findList(), Package.finder.findList()));
    }

    /**
     * Method that opens up list of office workers
     *
     * @return
     */
    public Result officeWorkersList() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        return ok(officeworkerlist.render(User.findOfficeWorkers()));
    }

    /**
     * Method that opens up list of post offices
     *
     * @return
     */
    public Result adminPostOffice() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        List<PostOffice> list = PostOffice.findOffice.findList();
        return ok(adminpostoffice.render(list));
    }

    /**
     * Method that opens up form for adding new post office
     *
     * @return
     */
    public Result addPostOffice() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }


        return ok(postofficeadd.render());
    }

    /**
     * Method that opens up form for registering office worker
     *
     * @return
     */
    public Result registerWorker() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        return ok(officeworkeradd.render(postOffices));
    }

    public Result trackPackage(){
        return ok(trackpackage.render());
    }

    public Result checkTrackingNumber(){
        DynamicForm form = Form.form().bindFromRequest();
        System.out.println(form.data().toString());
        String trackingNumber = form.data().get("trackingNumber");
        Package p = Package.findPackageByTrackingNumber(trackingNumber);
        return ok(p.toString());
    }

    public Result deliveryWorkersList() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        return ok(deliveryworkerslist.render(User.find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).findList()));
    }

    public Result deliveryWorkerPanel(){
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }

        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
        List<Package> packages = new ArrayList<>();
        for (int i=0; i<shipments.size();i++){
            if(shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING){
                packages.add(shipments.get(i).packageId);
            }
        }

        packages.remove(0);

        return ok(deliveryworkerpanel.render(packages));
    }

}
