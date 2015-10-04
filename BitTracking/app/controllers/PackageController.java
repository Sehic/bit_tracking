package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.Package;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

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
        if(office == null){
            flash("wrongInitialOffice", "Please choose one office!");
            return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
        }
        Package pack = new Package();

        List<PostOffice> offices = PostOffice.findOffice.findList();

        try {
            pack = boundForm.get();

            PostOffice officeByName = PostOffice.findPostOfficeByName(pack.destination);
            if(officeByName== null){
                flash("wrongFinalOffice", "Please choose one office!");
                return badRequest(packageadd.render(PostOffice.findOffice.findList(), locations, boundForm, u1));
            }

            pack.trackingNum = (UUID.randomUUID().toString());

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

    /**
     * Method that updates package informations (packagedetails.scala.html)
     *
     * @param id
     * @return
     */
    public Result updatePackage(Long id) {

        return TODO;
    }

    /**
     * Method that is used for opening up form for editing package (adminpackage.scala.html)
     *
     * @param id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result editPackage(Long id) {
        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }

    /**
     * This method is used for changing status as delivery worker. When package is ready for shipping, delivery worker takes package
     * and replaces it to another office, where package gets status recieved until office worker checks it.
     * @param u1 - current logged in delivery worker
     * @param pack - package which status will be updated
     * @param c - Calendar
     * @param date - Date which is used for timestamp
     * @return - delivery worker panel
     */
    public Result updateStatusAsDeliveryWorker(User u1, Package pack, Calendar c, Date date) {

        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();

        for (int i = 0; i < shipments.size(); i++) {
            if (shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING) {
                shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                c = Calendar.getInstance();
                date = c.getTime();
                shipments.get(i).dateCreated = date;
                Ebean.update(shipments.get(i));
                shipments.get(i + 1).status = StatusHelper.RECEIVED;
                Ebean.update(shipments.get(i + 1));
                break;
            }
        }
        List<Package> packages = new ArrayList<>();
        List<Shipment> shipmentByOfficeAndStatus = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();

        for (int i = 0; i < shipmentByOfficeAndStatus.size(); i++) {

            packages.add(shipmentByOfficeAndStatus.get(i).packageId);
        }
        return ok(deliveryworkerpanel.render(packages));
    }

    /**
     * Method that is called when office worker has packages with status recieved. After he clicks it, package will change status to
     * ready for shipping, or delivered if office workers office is package final destination.
     * @param u1 - current logged in office worker
     * @param pack - package on which worker changes status
     * @param c - Calendar
     * @param date - Date
     * @return - office worker panel
     */
    public Result updateStatusAsOfficeWorker(User u1, Package pack, Calendar c, Date date) {

        List<Shipment> shipmentsByPostOffice = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
        List<Package> packages = new ArrayList<>();
        for (int i = 0; i < shipmentsByPostOffice.size(); i++) {
            packages.add(shipmentsByPostOffice.get(i).packageId);
            if (shipmentsByPostOffice.get(i).packageId.id == pack.id) {
                if (pack.destination.equals(u1.postOffice.name)) {
                    packages.remove(i);
                    List<Shipment> shipmentByPackage = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
                    for (int j = 0; j < shipmentByPackage.size(); j++) {
                        shipmentByPackage.get(j).status = StatusHelper.DELIVERED;
                        Ebean.update(shipmentByPackage.get(j));
                    }
                    int last = shipmentByPackage.size() - 1;
                    c = Calendar.getInstance();
                    date = c.getTime();
                    shipmentByPackage.get(last).dateCreated = date;
                    Ebean.update(shipmentByPackage.get(last));
                    packages.add(shipmentByPackage.get(last).packageId);
                    break;
                }
                shipmentsByPostOffice.get(i).status = StatusHelper.READY_FOR_SHIPPING;
                Ebean.update(shipmentsByPostOffice.get(i));
            }
        }
        return ok(officeworkerpanel.render(packages, u1.postOffice));
    }

    /**
     * Method that is used for calling methods for changing package status
     * @param id - package id
     * @return - delivery or office worker panel
     */
    public Result updateStatus(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        Package pack = Package.finder.byId(id);
        Calendar c = null;
        Date date = null;

        if (u1 != null && (u1.typeOfUser == UserType.ADMIN || u1.typeOfUser == UserType.DELIVERY_WORKER)) {
            updateStatusAsDeliveryWorker(u1, pack, c, date);
            return redirect(routes.Application.deliveryWorkerPanel());
        }

        if (u1 != null && u1.typeOfUser == UserType.OFFICE_WORKER) {
            updateStatusAsOfficeWorker(u1, pack, c, date);
            return redirect(routes.UserController.officeWorkerPanel());
        }

        return redirect(routes.Application.index());
    }

    @Security.Authenticated(Authenticators.AdminDeliveryWorkerFilter.class)
    public Result changePackageStatus(Long id) {

        return ok(packagestatus.render(Package.findPackageById(id)));
    }
}
