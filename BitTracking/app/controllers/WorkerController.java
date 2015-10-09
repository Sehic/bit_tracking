package controllers;

import helpers.Authenticators;
import helpers.HashHelper;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.adminworkeradd;
import views.html.deliveryworkerpanel;
import views.html.officeworkerpanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mladen13 on 9.10.2015.
 */
public class WorkerController extends Controller {

    private static final Form<User> newUser = new Form<User>(User.class);

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addWorker() {

        Form<User> boundForm = newUser.bindFromRequest();
        String firstName = boundForm.field("firstName").value();
        String lastName = boundForm.field("lastName").value();
        String password = boundForm.field("password").value();
        String repassword = boundForm.field("repassword").value();
        String email = boundForm.field("email").value();
        //Getting post office name
        String postOffice = boundForm.bindFromRequest().field("postOffice").value();
        String userType = boundForm.bindFromRequest().field("userType").value();
        //Proceeding value and creating post office with it
        PostOffice wantedPostOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();

        User u = User.checkEmail(email);
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        if (u != null) {
            flash("errorEmail", "E-mail address already exists!");
            return ok(adminworkeradd.render(postOffices, boundForm));
        }
        u = new User(firstName, lastName, password, email, wantedPostOffice);


        if (!u.checkName(u.firstName)) {
            flash("errorName", "Your name should have only letters.");
            return badRequest(adminworkeradd.render(postOffices, boundForm));
        }

        if (!u.checkName(u.lastName)) {
            flash("errorLastName", "Your last name should have only letters.");
            return badRequest(adminworkeradd.render(postOffices, boundForm));
        }

        if (!u.checkPassword(u.password)) {
            flash("errorPassword", "Couldn't accept password. Your password should contain at least 6 characters and one number");
            return badRequest(adminworkeradd.render(postOffices, boundForm));
        }

        if (!u.password.equals(repassword)) {
            flash("errorTwoPasswords", "You entered different passwords");
            return badRequest(adminworkeradd.render(postOffices, boundForm));
        }

        String newPassword = HashHelper.getEncriptedPasswordMD5(password);

        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

        u = new User(firstName, lastName, newPassword, email, wantedPostOffice);
        if (userType.equals("1")) {
            u.typeOfUser = UserType.OFFICE_WORKER;
        } else {
            u.typeOfUser = UserType.DELIVERY_WORKER;
        }
        u.save();

        return redirect(routes.Application.adminTables());

    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result officeWorkerPanel() {

        User u1 = SessionHelper.getCurrentUser(ctx());

        PostOffice userOffice = u1.postOffice;
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("postOfficeId", userOffice).findList();
        List<models.Package> packages = new ArrayList<>();
        for (int i = 0; i < shipments.size(); i++) {
            packages.add(shipments.get(i).packageId);
        }

        List<Package> packagesWaiting = Package.findPackagesWaitingForApproval();
        List<Package> packagesForOfficeWorker= PackageStatusController.packagesForOfficeWorkerWaitingForApproval(userOffice, packagesWaiting);

        List<PostOffice> offices = PostOffice.findOffice.findList();

        return ok(officeworkerpanel.render(packages, u1.postOffice, packagesForOfficeWorker, offices));
    }

    public Result deliveryWorkerPanel() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }

        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();
        List<Package> packages = new ArrayList<>();
        for (int i = 0; i < shipments.size(); i++) {

            packages.add(shipments.get(i).packageId);
        }

        return ok(deliveryworkerpanel.render(packages));
    }

}