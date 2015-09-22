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

        List<PostOffice> offices = PostOffice.findOffice.findList();
        if (offices == null || offices.size() == 0) {
            return ok(adminpostofficeadd.render());
        }

        return ok(packageadd.render(PostOffice.findOffice.findList(), newPackage));
    }

    /**
     * Method that saves package to database using (packageadd.scala.html) form
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result savePackage() {

        Form<models.Package> boundForm = newPackage.bindFromRequest();

        String id = boundForm.field("officePost").value();

        PostOffice office = PostOffice.findPostOffice(Long.parseLong(id));
        Package pack = new Package();
        try {
            pack = boundForm.get();
            pack.trackingNum = (UUID.randomUUID().toString());

            System.out.println(pack.weight);
            System.out.println(pack.price);

            pack.save();

        } catch (IllegalStateException e) {

            flash("wrongFormatBoth", "Please insert numbers only!");

            return badRequest(packageadd.render(PostOffice.findOffice.findList(), boundForm));
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
        return ok(packageadd.render(PostOffice.findOffice.findList(), newPackage));

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
        if (u1 != null && (u1.typeOfUser == UserType.ADMIN || u1.typeOfUser == UserType.DELIVERY_WORKER)) {


            List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();

            for (int i = 0; i < shipments.size(); i++) {
                if (shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING) {
                    shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                    System.out.println(shipments.get(i).postOfficeId.name);
                    Calendar c = Calendar.getInstance();
                    Date date = c.getTime();
                    shipments.get(i).dateCreated = date;
                    Ebean.update(shipments.get(i));
                    shipments.get(i + 1).status = StatusHelper.RECEIVED;
                    Ebean.update(shipments.get(i + 1));
                    break;
                }
            }
            List<Package> packages = new ArrayList<>();
            List<Shipment> shipments1 = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();

            for (int i = 0; i < shipments1.size(); i++) {

                packages.add(shipments1.get(i).packageId);
            }
            return ok(deliveryworkerpanel.render(packages));

        } else if (u1 != null && u1.typeOfUser == UserType.OFFICE_WORKER) {
            List<Shipment> shipments = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
            List<Package> packages = new ArrayList<>();
            for (int i = 0; i < shipments.size(); i++) {
                packages.add(shipments.get(i).packageId);
                if (shipments.get(i).packageId.id == pack.id) {
                    if (pack.destination.equals(u1.postOffice.address)) {
                        packages.remove(i);
                        List<Shipment> shipmentss = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
                        for (int j = 0; j < shipmentss.size(); j++) {
                            shipmentss.get(j).status = StatusHelper.DELIVERED;
                            Ebean.update(shipmentss.get(j));
                        }
                        int last = shipmentss.size() - 1;
                        Calendar c = Calendar.getInstance();
                        Date date = c.getTime();
                        shipmentss.get(last).dateCreated = date;
                        Ebean.update(shipmentss.get(last));
                        packages.add(shipmentss.get(last).packageId);
                        break;
                    }
                    shipments.get(i).status = StatusHelper.READY_FOR_SHIPPING;
                    Ebean.update(shipments.get(i));
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
