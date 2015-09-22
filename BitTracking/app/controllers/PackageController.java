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

        return ok(packageadd.render(PostOffice.findOffice.findList(), newPackage, u1));
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

        String officeName = boundForm.field("officePost").value();
        PostOffice office = PostOffice.findPostOfficeByName(officeName);
        Package pack = new Package();

        try {
            pack = boundForm.get();

            pack.trackingNum = (UUID.randomUUID().toString());

            pack.save();

        } catch (IllegalStateException e) {

            flash("wrongFormatBoth", "Please insert numbers only!");

            return badRequest(packageadd.render(PostOffice.findOffice.findList(), boundForm, u1));
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

            Ebean.delete(p.shipmentPackages.get(i));
        }

        Ebean.delete(p);
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

    public Result updateStatus(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        Package pack = Package.finder.byId(id);
        Calendar c = null;
        Date date = null;

        if (u1 != null && (u1.typeOfUser == UserType.ADMIN || u1.typeOfUser == UserType.DELIVERY_WORKER)) {

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

        } else if (u1 != null && u1.typeOfUser == UserType.OFFICE_WORKER) {
            List<Shipment> shipmentsByPostOffice = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
            List<Package> packages = new ArrayList<>();
            for (int i = 0; i < shipmentsByPostOffice.size(); i++) {
                packages.add(shipmentsByPostOffice.get(i).packageId);
                if (shipmentsByPostOffice.get(i).packageId.id == pack.id) {
                    if (pack.destination.equals(u1.postOffice.address)) {
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
        return redirect(routes.Application.index());
    }

    @Security.Authenticated(Authenticators.AdminDeliveryWorkerFilter.class)
    public Result changePackageStatus(Long id) {

        return ok(packagestatus.render(Package.findPackageById(id)));
    }
}
