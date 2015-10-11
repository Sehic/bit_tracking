package controllers;

import com.avaje.ebean.Ebean;
import helpers.*;
import models.Package;
import models.*;
import play.Logger;
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
        User u = p.users.get(0);
        for (int i = 0; i < u.packages.size(); i++) {
            if (u.packages.get(i).id == id) {
                u.packages.remove(i);
            }
        }
        u.update();
        p.users.clear();
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

            if (!User.checkName(pack.recipientName)) {
                flash("recipientNameError", "Your name should contains only letters.");
                return ok(userpanel.render(Package.findPackagesByUser(user), PostOffice.findOffice.findList()));
            }

            pack.recipientAddress = form.get("recipientAddress");
            pack.senderName = user.firstName + " " + user.lastName;
            pack.weight = Double.parseDouble(form.get("weight"));
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

            user.packages.add(pack);
            Shipment ship = new Shipment();
            ship.packageId = pack;
            ship.postOfficeId = PostOffice.findPostOfficeByName(form.get("initialPostOffice"));
            if (ship.postOfficeId == null) {
                flash("noOffice", "Please select one office!");
                return badRequest(userpanel.render(Package.findPackagesByUser(user), PostOffice.findOffice.findList()));
            }
            pack.save();
            user.update();
            ship.save();
            String subject = "Thank You For Choosing BitTracking For Your Delivery Services";
            String message = "Mr. " + user.lastName + ",<br>" +
                    "Your request for delivery services has been received.<br>" +
                    "It will be processed as soon as possible,<br>" +
                    "and you will be notified.<br><br>" +
                    "<i>BitTracking Team</i>";
            MailHelper.sendConfirmation(subject, message, user.email);

        } catch (PersistenceException | IllegalStateException | NumberFormatException e) {

            return badRequest(index.render(0,0));
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
        String price = form.get("price");
        String subject = "BitTracking Notification!";
        String message = "";

        Shipment ship = Shipment.shipmentFinder.where().eq("packageId", pack).findUnique();
        if (value.equals("approve") && destination != "default") {
            if (destination.equals("default")) {
                return redirect(routes.WorkerController.officeWorkerPanel());
            }
            if ("".equals(price)) {
                return redirect(routes.WorkerController.officeWorkerPanel());
            }
            pack.price = Double.parseDouble(price);
            pack.approved = true;
            pack.seen = false;
            pack.trackingNum = (UUID.randomUUID().toString());
            pack.destination = destination;
            message = "Mr. " + pack.users.get(0).lastName + ",<br>" +
                    "Your Latest Request For Delivery Service Has Been <strong>Approved</strong>.<br>" +
                    "You can get status information of delivery by any time using this tracking number:<br><br> " + pack.trackingNum + "<br><br>" +
                    "As soon as the package is delivered, you will be notified.<br><br>" +
                    "<i>BitTracking Team!</i>";
            MailHelper.sendConfirmation(subject, message, pack.users.get(0).email);
        } else if (value.equals("reject")) {
            pack.approved = false;
            pack.trackingNum = "rejected";
            pack.seen = false;
            pack.update();
            ship.delete();
            message = "Mr. " + pack.users.get(0).lastName + ",<br>" +
                    "Your Latest Request For Delivery Service Has Been <strong>Rejected</strong>.<br>" +
                    "For more information, contact us.<br><br>" +
                    "<i>BitTracking Team!</i>";
            MailHelper.sendConfirmation(subject, message, pack.users.get(0).email);
            return redirect(routes.WorkerController.officeWorkerPanel());
        } else {
            pack.approved = null;
            return redirect(routes.WorkerController.officeWorkerPanel());
        }
        pack.update();
        ship.packageId = pack;
        ship.postOfficeId = initial;
        ship.update();
        return redirect(routes.PostOfficeController.listRoutes(id));
    }
}
