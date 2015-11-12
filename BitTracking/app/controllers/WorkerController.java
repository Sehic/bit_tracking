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
import views.html.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mladen13 on 9.10.2015.
 */
public class WorkerController extends Controller {

    private static final Form<User> newUser = new Form<User>(User.class);

    /**
     * Method that is used for adding delivery or office worker (with admin that is signed in to page)
     *
     * @return - badRequest if form is not valid, otherwise redirect to admin tables
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addWorker() {
        //Getting values from form
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
        //Getting driving office field, that is required for delivery worker
        String drivingOffice = boundForm.field("drivingOffice").value();
        //Checking if delivery worker is courier
        String isCourier = boundForm.field("isCourier").value();
        User u = User.checkEmail(email);
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        //Backend field validation
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
        //Capitalizing first letters before inserting to database
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

        u = new User(firstName, lastName, newPassword, email, wantedPostOffice);
        if (userType.equals("1")) {
            u.typeOfUser = UserType.OFFICE_WORKER;
        } else {
            u.typeOfUser = UserType.DELIVERY_WORKER;
        }
        u.validated = true;
        //Setting delivery worker driving offices
        if (!"".equals(drivingOffice)) {
            if (u.typeOfUser == UserType.DELIVERY_WORKER) {
                u.drivingOffice = drivingOffice;
            }
        }
        if (isCourier != null) {
            if (u.typeOfUser == UserType.DELIVERY_WORKER) {
                u.isCourier = true;
            }
        }
        u.save();

        return redirect(routes.Application.adminTables());

    }

    /**
     * Method that opens up office worker panel
     *
     * @return ok and render office worker panel
     */
    @Security.Authenticated(Authenticators.OfficeWorkerFilter.class)
    public Result officeWorkerPanel() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        //Getting office from signed in office worker
        PostOffice userOffice = u1.postOffice;
        //Getting shipments that are connected with worker office
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("postOfficeId", userOffice).findList();
        List<models.Package> packages = new ArrayList<>();
        //Adding packages from shipment that are connected with user office to list
        for (int i = 0; i < shipments.size(); i++) {
            packages.add(shipments.get(i).packageId);
        }

        List<Package> packagesWaiting = Package.findPackagesWaitingForApproval();
        //Getting list of packages waiting for approval
        List<Package> packagesForOfficeWorker = PackageStatusController.packagesForOfficeWorkerWaitingForApproval(userOffice, packagesWaiting);

        List<PostOffice> offices = PostOffice.findOffice.findList();

        return ok(officeworkerpanel.render(packages, u1.postOffice, packagesForOfficeWorker, offices));
    }

    /**
     * This method is used for opening up delivery worker panel
     * It filters up packages and shows up only packages that are ready for shipment.
     * It also only shows packages that are going to destination where delivery worker driving office is.
     *
     * @return - ok and opens delivery worker panel
     */
    @Security.Authenticated(Authenticators.DeliveryWorkerFilter.class)
    public Result deliveryWorkerPanel() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        //Getting shipments that contains delivery worker office, with status ready for shipping
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();
        List<Package> packages = new ArrayList<>();
        //Adding packages with status ready for shipping from shipment list, to packages list
        for (int i = 0; i < shipments.size(); i++) {
            packages.add(shipments.get(i).packageId);
        }

        List<Package> packagesForUser = new ArrayList<>(packages);
        List<Package> userPackages = u1.packages;
        //List of packages that delivery worker can take and transport
        List<Package> readyPackages = getReadyToBeTakenPackages(packages, u1);

        List<Package> finalUserPackages = getUserPackages(packagesForUser, userPackages);
        if (u1.isCourier) {
            return redirect(routes.WorkerController.deliveryCourierPanel());
        }
        return ok(deliveryworkerpanel.render(readyPackages, finalUserPackages, userPackages));
    }

    /**
     * Method that opens up delivery courier panel
     *
     * @return - ok and opens up delivery courier panel
     */
    @Security.Authenticated(Authenticators.DeliveryWorkerFilter.class)
    public Result deliveryCourierPanel() {
        User u1 = SessionHelper.getCurrentUser(ctx());

        List<Package> packagesForShipment = getPackagesWithoutCourier();
        List<Package> courierPackages = u1.packages;
        List<Package> courierPackagesForDelivery = getCourierPackagesForHomeDelivery(courierPackages);
        return ok(deliverycourierpanel.render(packagesForShipment, courierPackagesForDelivery, courierPackages));
    }

    /**
     * Action method that is used for sending delivery worker home. It simulates situation when delivery worker comes to another office
     * and when he couldn't find any packages that are ready for shipping so he can go home.
     *
     * @return - redirect to delivery worker panel
     */
    @Security.Authenticated(Authenticators.DeliveryWorkerFilter.class)
    public Result driveHome() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        PostOffice userOffice = PostOffice.findPostOfficeByName(u1.drivingOffice);
        u1.drivingOffice = u1.postOffice.name;
        u1.postOffice = userOffice;
        u1.update();
        return redirect(routes.WorkerController.deliveryWorkerPanel());
    }

    /**
     * Method that returns packages that are in process of transport
     *
     * @param courierPackages
     * @return - list of packages for courier worker
     */
    public static List<Package> getCourierPackagesForHomeDelivery(List<Package> courierPackages) {
        List<Package> packages = new ArrayList<>();
        List<Package> packagesForShipment = Package.finder.where().eq("statusForCourier", StatusHelper.READY_FOR_SHIPPING).findList();
        for (int i = 0; i < packagesForShipment.size(); i++) {
            for (int j = 0; j < courierPackages.size(); j++) {
                if (packagesForShipment.get(i).id == courierPackages.get(j).id) {
                    packages.add(packagesForShipment.get(i));
                }
            }
        }
        return packages;
    }

    /**
     * Method that returns list of packages that are free and waiting to be taken
     *
     * @return - list of packages that are ready for shipment
     */
    public static List<Package> getPackagesWithoutCourier() {
        List<Package> packagesForShipment = Package.finder.where().eq("statusForCourier", StatusHelper.READY_FOR_SHIPPING).findList();
        List<Package> packages = new ArrayList<>();
        for (int i = 0; i < packagesForShipment.size(); i++) {
            if (!packagesForShipment.get(i).isTaken) {
                packages.add(packagesForShipment.get(i));
            }
        }
        return packages;
    }


    /**
     * Method that returns list of packages that delivery worker can take for transport
     * Delivery worker can take packages that goes to destination where his driving office is
     *
     * @param packages - list of packages
     * @param u1       - delivery worker
     * @return - list of packages that will be taken
     */
    public static List<Package> getReadyToBeTakenPackages(List<Package> packages, User u1) {
        List<Package> packagesToBeTaken = new ArrayList<>();
        //Putting packages that are not taken by another delivery worker to list
        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).isTaken == false) {
                packagesToBeTaken.add(packages.get(i));
            }
        }
        List<Package> finalPackages = new ArrayList<>();
        //Filtering packages for delivery worker, so he can se only packages with his driving office
        for (int i = 0; i < packagesToBeTaken.size(); i++) {
            List<Shipment> shipmentPackages = packagesToBeTaken.get(i).shipmentPackages;
            for (int j = 0; j < shipmentPackages.size(); j++) {
                //Checking if list comes to end
                if (j != shipmentPackages.size() - 1) {
                    //If office from shipment is equal delivery worker driving office, package will be added to list
                    if (shipmentPackages.get(j + 1).postOfficeId.name.equals(u1.drivingOffice)) {
                        finalPackages.add(shipmentPackages.get(j + 1).packageId);
                        break;
                    }
                } else {
                    if (shipmentPackages.get(shipmentPackages.size() - 1).postOfficeId.name.equals(u1.drivingOffice)) {
                        finalPackages.add(shipmentPackages.get(shipmentPackages.size() - 1).packageId);
                    }
                }
            }
        }
        return finalPackages;
    }

    /**
     * Method that returns packages that delivery worker transported
     *
     * @param packagesForUser
     * @param userPackages
     * @return - list of user packages
     */
    public static List<Package> getUserPackages(List<Package> packagesForUser, List<Package> userPackages) {
        List<Package> finalUserPackages = new ArrayList<>();
        for (int i = 0; i < packagesForUser.size(); i++) {
            for (int j = 0; j < userPackages.size(); j++) {
                if (packagesForUser.get(i).id == userPackages.get(j).id) {
                    finalUserPackages.add(userPackages.get(j));
                }
            }
        }
        return finalUserPackages;
    }

}
