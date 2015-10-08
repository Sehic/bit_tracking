package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.PackageType;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.Package;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import javax.persistence.PersistenceException;
import java.util.*;

/**
 * Created by USER on 9.9.2015.
 */
public class PackageController extends Controller {

    private static final Form<Package> newPackage = new Form<Package>(Package.class);

    /**
     * Method that shows up list of all packages in post office
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPackage() {

        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that opens up add package form (packageadd.scala.html)
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result addPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        List<PostOffice> offices = PostOffice.findOffice.findList();
        if (offices == null || offices.size() == 0) {
            return ok(adminpostofficeadd.render());
        }

        List<Location> locations = Location.findLocation.findList();

        return ok(packageadd.render(offices, locations, newPackage, u1));
    }

    /**
     * Method that saves package to database using (packageadd.scala.html) form
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result savePackage() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        Form<models.Package> boundForm = newPackage.bindFromRequest();

        String officeAddress = boundForm.field("officePost").value();
        PostOffice office = PostOffice.findPostOfficeByAddress(officeAddress);
        List<Location> locations = Location.findLocation.findList();
        if (office == null) {
            flash("wrongInitialOffice", "Please choose one office!");
            return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
        }
        Package pack = new Package();

        List<PostOffice> offices = PostOffice.findOffice.findList();

        try {
            pack = boundForm.get();

            PostOffice officeByName = PostOffice.findPostOfficeByName(pack.destination);
            if (officeByName == null) {
                flash("wrongFinalOffice", "Please choose one office!");
                return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
            }

            pack.trackingNum = (UUID.randomUUID().toString());
            pack.approved = true;
            pack.save();

        } catch (IllegalStateException | NumberFormatException e) {

            flash("wrongFormatBoth", "Please fill this form with correct values!");

            return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
        }

        Shipment ship = new Shipment();
        ship.packageId = pack;
        ship.postOfficeId = office;
        ship.save();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user.typeOfUser == UserType.ADMIN)
            return redirect(routes.PackageController.adminPackage());
        else
            return redirect(routes.PostOfficeController.listRoutes(pack.id));
    }

    /**
     * Method that deletes package from database
     *
     * @param id - tracking number
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result deletePackage(Long id) {

        Package p = Package.findPackageById(id);

        for (int i = 0; i < p.shipmentPackages.size(); i++) {
            p.shipmentPackages.get(i).delete();
        }

        p.delete();
        return redirect(routes.PackageController.adminPackage());

    }

    @Security.Authenticated(Authenticators.RegisteredUserFilter.class)
    public Result userSavePackage() {
        User user = SessionHelper.getCurrentUser(ctx());
        DynamicForm form = Form.form().bindFromRequest();
        List<Package> packages = Package.finder.where().eq("seen", false).eq("users", user).findList();
        Package pack = new Package();
        try {
            pack.recipientName = form.get("recipientName");
            pack.recipientAddress = form.get("recipientAddress");
            pack.senderName = user.firstName + " " + user.lastName;
            pack.weight = Double.parseDouble(form.get("weight"));
            pack.price = Double.parseDouble(form.get("price"));
            if(pack.weight <= 0 || pack.price <= 0){
                return badRequest(userpanel.render(Package.findPackagesByUser(user), PostOffice.findOffice.findList()));
            }
            pack.seen = true;
            String type = form.get("packageType");
            pack.packageType = null;
            switch (type) {
                case "1":
                    pack.packageType = PackageType.BOX;
                    break;
                case "2":
                    pack.packageType = PackageType.ENVELOPE;
                    break;
                case "3":
                    pack.packageType = PackageType.FLYER;
                    break;
                case "4":
                    pack.packageType = PackageType.TUBE;
                    break;
            }
            pack.save();
            user.packages.add(pack);
            user.update();
        } catch (PersistenceException | IllegalStateException | NumberFormatException e) {

            return badRequest(index.render(packages));
        }
        return ok(userpanel.render(Package.findPackagesByUser(user), PostOffice.findOffice.findList()));
    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result approveReject(Long id) {
        DynamicForm form = Form.form().bindFromRequest();
        Package pack = Package.findPackageById(id);
        String value = form.get("approveReject");
        PostOffice initial = PostOffice.findPostOfficeByName(form.get("initialPostOffice"));
        String destination = form.get("destinationPostOffice");
        if (value.equals("approve") && initial != null && destination != "default") {
            pack.approved = true;
            pack.trackingNum = (UUID.randomUUID().toString());
            pack.destination = destination;
        } else if (value.equals("reject")) {
            pack.approved = false;
            pack.trackingNum = "rejected";
            pack.seen = false;
            pack.update();
            return redirect(routes.UserController.officeWorkerPanel());
        } else {
            pack.approved = null;
            return redirect(routes.UserController.officeWorkerPanel());
        }
        pack.update();
        Shipment ship = new Shipment();
        ship.packageId = pack;
        ship.postOfficeId = initial;
        ship.save();
        return redirect(routes.PostOfficeController.listRoutes(id));
    }
}
