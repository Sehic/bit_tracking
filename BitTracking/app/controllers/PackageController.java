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
     * @return - ok and adminpackage.html render
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPackage() {

        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that opens up add package form (packageadd.scala.html)
     *
     * @return - ok and renders packageadd.html
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result addPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        List<PostOffice> offices = PostOffice.findOffice.findList();
        List<Country> countries = Country.findCountry.findList();
        if (offices == null || offices.size() == 0) {
            return ok(adminpostofficeadd.render(countries));
        }

        List<Location> locations = Location.findLocation.findList();

        return ok(packageadd.render(offices, locations, newPackage, u1));
    }

    /**
     * Method that saves package to database using (packageadd.scala.html) form
     *
     * @return - badRequest if something goes wrong, redirect to office worker panel otherwise
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result savePackage() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        Form<models.Package> boundForm = newPackage.bindFromRequest();

        String officeAddress = boundForm.field("officePost").value();
        String routeForShipment = boundForm.field("routeOffices").value();
        PostOffice office = PostOffice.findPostOfficeByAddress(officeAddress);
        List<Location> locations = Location.findLocation.findList();
        //Form input validation, because initial and destination post offices are required
        if (office == null) {
            ApplicationLog newLog = new ApplicationLog("Error saving package " + u1.email + ". Initial Post Office not chosen.");
            newLog.save();
            flash("wrongInitialOffice", "Please choose one office!");
            return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
        }
        Package pack = new Package();

        List<PostOffice> offices = PostOffice.findOffice.findList();

        try {
            pack = boundForm.get();

            PostOffice officeByName = PostOffice.findPostOfficeByName(pack.destination);
            if (officeByName == null) {
                ApplicationLog newLog = new ApplicationLog("Error saving package " + u1.email + ". Final Post Office not chosen.");
                newLog.save();
                flash("wrongFinalOffice", "Please choose one office!");
                return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
            }

            pack.trackingNum = (UUID.randomUUID().toString());
            pack.approved = true;
            pack.isVerified = true;
            pack.isTaken = false;
            double distance = DijkstraHelper.getDistance(office.place.toString(), officeByName.place.toString());
            double price = PriceHelper.calculatePrice(pack.weight, distance);
            pack.price = price;
            pack.save();

        } catch (IllegalStateException | NumberFormatException e) {

            flash("wrongFormatBoth", "Please fill this form with correct values!");
            ApplicationLog newLog = new ApplicationLog("Error saving package " + u1.email + ". Form not filled with correct values.");
            newLog.save();
            return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
        }
        //Create only one shipment if route option is disabled else, whole route is created.
        if (routeForShipment == null) {
            Shipment ship = new Shipment();
            ship.packageId = pack;
            ship.postOfficeId = office;
            ship.save();
        } else {
            List<PostOffice> officesFromRoute = RouteController.officesFromAutoRoute(routeForShipment);

            for (int i = 0; i < officesFromRoute.size(); i++) {
                Shipment ship = new Shipment();
                ship.packageId = pack;
                ship.postOfficeId = officesFromRoute.get(i);
                if (i == 0) {
                    ship.status = StatusHelper.READY_FOR_SHIPPING;
                } else {
                    ship.status = StatusHelper.ON_ROUTE;
                }
                ship.save();
            }
        }

        User user = SessionHelper.getCurrentUser(ctx());
        if (user.typeOfUser == UserType.ADMIN) {
            return redirect(routes.PackageController.adminPackage());
        }
            return redirect(routes.WorkerController.officeWorkerPanel());
    }

    /**
     * Method that deletes package from database
     *
     * @param id - tracking number
     * @return - redirect to admin package list
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result deletePackage(Long id) {

        Package p = Package.findPackageById(id);
        //Deleting shipments from this package
        for (int i = 0; i < p.shipmentPackages.size(); i++) {
            p.shipmentPackages.get(i).delete();
        }
        //Deleting package relation with users
        for (int i = 0; i < p.users.size(); i++) {
            User u = p.users.get(i);
            Logger.info(u.email);
            if (u != null) {
                for (int j = 0; j < u.packages.size(); j++) {
                    if (u.packages.get(j).id == id) {
                        u.packages.remove(j);
                    }
                }
                u.update();
            }
        }
        p.delete();
        return redirect(routes.PackageController.adminPackage());

    }

    /**
     * This method is used for creating package request by Registered User
     * @return - redirect to user panel
     */
    @Security.Authenticated(Authenticators.RegisteredUserFilter.class)
    public Result userSavePackage() {
        //Getting form data
        User user = SessionHelper.getCurrentUser(ctx());
        DynamicForm form = Form.form().bindFromRequest();
        List<Package> packages = Package.finder.where().eq("seen", false).eq("users", user).findList();
        Package pack = new Package();
        try {
            pack.recipientName = form.get("recipientName");

            if (!User.checkName(pack.recipientName)) {
                flash("recipientNameError", "Your name should contains only letters.");
                return redirect(routes.Application.userPanel());
            }

            pack.recipientAddress = form.get("recipientAddress");
            pack.senderName = user.firstName + " " + user.lastName;
            pack.weight = Double.parseDouble(form.get("weight"));
            Double distance = Double.parseDouble(form.get("distanceBetween"));
            pack.seen = true;
            String type = form.get("packageType");
            String recipientCountry = form.get("recipientCountry");
            pack.recipientCountry = recipientCountry;
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
            //Creating first shipment
            Shipment ship = new Shipment();
            ship.packageId = pack;
            ship.postOfficeId = PostOffice.findPostOfficeByAddress(form.get("initialPostOffice"));
            if (ship.postOfficeId == null) {
                flash("noOffice", "Please select one office!");
                return redirect(routes.Application.userPanel());
            }
            //Calculating price for package request
            pack.price = PriceHelper.calculatePrice(pack.weight, distance);
            pack.save();
            user.update();
            ship.save();
            //Sending mail to user that request has been received
            MailHelper.requestReceivedNotification(user.lastName, user.email);
        } catch (PersistenceException | IllegalStateException | NumberFormatException e) {

            return badRequest(index.render());
        }
        List<Country> countries = Country.findCountry.findList();
        return ok(userpanel.render(Package.findPackagesByUser(user), PostOffice.findOffice.findList(), countries));
    }

    /**
     * This method is used for approving or rejecting package request that comes from registered user
     * If package request is valid, office worker will approve it
     * @param id - package id
     * @return - redirect to office worker panel
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result approveReject(Long id) {
        //Getting form values
        DynamicForm form = Form.form().bindFromRequest();
        Package pack = Package.findPackageById(id);
        PostOffice initial = PostOffice.findPostOfficeByName(form.get("initialPostOffice"));

        String value = form.get("approveReject");
        String destination = form.get("destinationPostOffice");
        String price = form.get("price");
        String subject = "BitTracking Notification!";
        String message = "";

        Shipment ship = Shipment.shipmentFinder.where().eq("packageId", pack).findUnique();
        Calendar c = Calendar.getInstance();

        Date date = c.getTime();
        //Approving package request
        if ("approve".equals(value) && !"default".equals(destination)) {
            if ("default".equals(destination)) {
                return redirect(routes.WorkerController.officeWorkerPanel());
            }
            if ("".equals(price)) {
                return redirect(routes.WorkerController.officeWorkerPanel());
            }

            pack.price = Double.parseDouble(price);
            pack.packageRejectedTimestamp = date;
            pack.approved = true;
            pack.seen = false;
            pack.isTaken = false;
            pack.trackingNum = (UUID.randomUUID().toString());
            pack.destination = destination;
            pack.packagePinCode = Package.getPinCode();
            /**
             * Due to limitations caused by trial version of Twilio, we can send only 5 SMS messages per day.
             * That's why we use MailHelper in this testing period.
             */
            /*if (pack.users.size() > 0 && pack.users.get(0).phoneNumber != null && pack.users.get(0).numberValidated) {
                SmsHelper.sendSms("Package Pin Code:" + pack.packagePinCode, pack.users.get(0).phoneNumber);
            }*/
            if (pack.users.size() > 0) {
                MailHelper.approvedRequestNotification(pack.users.get(0).lastName, pack.trackingNum, pack.users.get(0).email, pack.packagePinCode);
            }
        //Rejecting package request
        } else if ("reject".equals(value)) {
            pack.approved = false;
            pack.trackingNum = "rejected";
            pack.seen = false;
            pack.packageRejectedTimestamp = date;
            pack.update();
            ship.delete();
            MailHelper.rejectedRequestNotification(pack.users.get(0).lastName, pack.users.get(0).email);
            return redirect(routes.WorkerController.officeWorkerPanel());
        } else {
            pack.approved = null;
            return redirect(routes.WorkerController.officeWorkerPanel());
        }
        pack.update();
        ship.packageId = pack;
        ship.postOfficeId = initial;
        ship.update();

        return redirect(routes.WorkerController.officeWorkerPanel());
    }

    /**
     * This method is used for taking packages for transport as delivery worker
     * @return - redirect to delivery worker/courier panel
     */
    @Security.Authenticated(Authenticators.AdminDeliveryWorkerFilter.class)
    public Result takePackages() {
        User user = SessionHelper.getCurrentUser(ctx());
        DynamicForm form = Form.form().bindFromRequest();
        List<Package> packages = Package.findApprovedPackages();
        //Getting values from checkboxes
        Package newPack = new Package();
        for (int i = 0; i < packages.size(); i++) {
            String pack = form.field("" + packages.get(i).id).value();
            if (pack != null) {
                newPack = Package.findPackageById(Long.parseLong(pack));
                newPack.isTaken = true;
                user.packages.add(newPack);
                newPack.users.add(user);
                newPack.update();
            }
        }
        user.update();
        if (user.isCourier) {
            return redirect(routes.WorkerController.deliveryCourierPanel());
        }
        return redirect(routes.WorkerController.deliveryWorkerPanel());
    }

    /**
     * This method is used to open package info view which is used to verify package pin code.
     * @param id - package id
     * @return - ok and opens up package info view
     */
    public Result packageInfo(Long id) {
        Package pack = Package.findPackageById(id);
        return ok(packageinfo.render(pack));
    }

    /**
     * This method is used for checking existance of package pin code
     * @param id - package id
     * @return - redirect to office worker panel if ok, otherwise redirect to same page
     */
    public Result checkPackageCode(Long id){
        DynamicForm form = Form.form().bindFromRequest();
        String code = form.get("packagePinCode");
        Package pack = Package.findPackageById(id);
        Long pinCode =0L;
        try{
             pinCode = Long.parseLong(code);

        }catch(NumberFormatException e) {
            return badRequest(packageinfo.render(pack));
        }
        //If inserted pin code is equal to package pin code, package is verified
        if(!pinCode.equals(pack.packagePinCode)){
            ApplicationLog newLog = new ApplicationLog("Error checking Package Code: " + pinCode + ". Invalid PinCode.");
            newLog.save();
            flash("incorrectCode","Incorrect Package Code. Please enter valid code.");
            return redirect(routes.PackageController.packageInfo(pack.id));
        }
        pack.isVerified = true;
        pack.update();
        return redirect(routes.WorkerController.officeWorkerPanel());
    }
}
